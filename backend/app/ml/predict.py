import joblib
import pandas as pd
import numpy as np
import shap
import json
import os

model_path = os.path.join(os.path.dirname(__file__), "models/rf_model.pkl")

# Load model if exists, otherwise it will fail gracefully or we can handle it
try:
    model = joblib.load(model_path)
except Exception as e:
    model = None
    print(f"Warning: Model not found at {model_path}. Train the model first.")

# Create a lightweight tree explainer for SHAP
# Note: For MVP, we'll initialize the explainer once if model is available
explainer = shap.TreeExplainer(model) if model else None

def predict_event(event_data: dict):
    if not model:
        raise ValueError("Model not loaded.")
        
    df = pd.DataFrame([event_data])
    
    # Ensure columns match training data
    features = ["destination_port", "flow_duration", "packet_rate", "bytes_rate"]
    for f in features:
        if f not in df.columns:
            df[f] = 0.0
            
    X = df[features]
    
    # Predict
    prediction = model.predict(X)[0]
    probabilities = model.predict_proba(X)[0]
    confidence = float(np.max(probabilities))
    
    # Map to severity based on classification
    # Benign, Port Scan, Brute Force, DDoS, Malware Traffic
    if prediction == "Benign":
        severity = "Low"
        score = np.random.uniform(0, 24)
    elif prediction == "Port Scan":
        severity = "Moderate"
        score = np.random.uniform(25, 49)
    elif prediction == "Brute Force":
        severity = "High"
        score = np.random.uniform(50, 74)
    else: # DDoS or Malware Traffic
        severity = "Critical"
        score = np.random.uniform(75, 100)
        
    # Explainability (SHAP)
    shap_values = explainer.shap_values(X)
    
    # shap_values is a list of arrays for multi-class. We want the explanation for the predicted class.
    # Get index of predicted class
    class_index = list(model.classes_).index(prediction)
    
    # Extract feature contributions for this class
    # TreeExplainer shap_values format varies by version, usually a list of arrays for classification
    if isinstance(shap_values, list):
        contributions = shap_values[class_index][0]
    else:
        # Some versions return a single array if binary or different format
        contributions = shap_values[0, :, class_index] if len(shap_values.shape) > 2 else shap_values[0]
        
    feature_contributions = []
    for i, feature in enumerate(features):
        feature_contributions.append({
            "feature": feature,
            "value": float(X.iloc[0, i]),
            "contribution": float(contributions[i])
        })
        
    # Sort by absolute contribution
    feature_contributions.sort(key=lambda x: abs(x["contribution"]), reverse=True)
    
    return {
        "classification": prediction,
        "confidence": confidence,
        "threat_score": round(score, 2),
        "severity": severity,
        "explanation": feature_contributions
    }
