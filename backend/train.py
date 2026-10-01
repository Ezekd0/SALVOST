import os
import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
import joblib

def generate_synthetic_data(samples_per_class=200):
    np.random.seed(42)
    classes = ["Benign", "Port Scan", "Brute Force", "DDoS", "Malware Traffic"]
    data = []
    
    for c in classes:
        for _ in range(samples_per_class):
            if c == "Benign":
                row = {
                    "destination_port": np.random.choice([80, 443, 22, 53]),
                    "flow_duration": np.random.normal(100, 20),
                    "packet_rate": np.random.normal(50, 10),
                    "bytes_rate": np.random.normal(1000, 200),
                    "label": c
                }
            elif c == "Port Scan":
                row = {
                    "destination_port": np.random.randint(1024, 65535),
                    "flow_duration": np.random.normal(5, 2),
                    "packet_rate": np.random.normal(500, 100),
                    "bytes_rate": np.random.normal(500, 100),
                    "label": c
                }
            elif c == "Brute Force":
                row = {
                    "destination_port": np.random.choice([22, 3389]),
                    "flow_duration": np.random.normal(2000, 500),
                    "packet_rate": np.random.normal(10, 2),
                    "bytes_rate": np.random.normal(100, 20),
                    "label": c
                }
            elif c == "DDoS":
                row = {
                    "destination_port": 80,
                    "flow_duration": np.random.normal(50, 10),
                    "packet_rate": np.random.normal(5000, 1000),
                    "bytes_rate": np.random.normal(50000, 10000),
                    "label": c
                }
            elif c == "Malware Traffic":
                row = {
                    "destination_port": np.random.choice([4444, 8080, 6667]),
                    "flow_duration": np.random.normal(5000, 1000),
                    "packet_rate": np.random.normal(20, 5),
                    "bytes_rate": np.random.normal(200, 50),
                    "label": c
                }
            data.append(row)
            
    df = pd.DataFrame(data)
    return df

def train_model():
    print("Generating synthetic data...")
    df = generate_synthetic_data()
    X = df.drop("label", axis=1)
    y = df["label"]
    
    print("Training Random Forest...")
    clf = RandomForestClassifier(n_estimators=100, random_state=42)
    clf.fit(X, y)
    
    os.makedirs("app/ml/models", exist_ok=True)
    joblib.dump(clf, "app/ml/models/rf_model.pkl")
    print("Model saved to app/ml/models/rf_model.pkl")
    return clf

if __name__ == "__main__":
    train_model()
