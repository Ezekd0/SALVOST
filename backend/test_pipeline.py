import json
from app.main import app
from fastapi.testclient import TestClient

def test_pipeline():
    print("Testing ML Pipeline End-to-End via TestClient...")
    client = TestClient(app)
    
    # 1. Login
    login_data = {"username": "demo", "password": "demo123"}
    r = client.post("/auth/token", data=login_data)
    if r.status_code != 200:
        print("Login failed:", r.text)
        return
    token = r.json().get("access_token")
    headers = {"Authorization": f"Bearer {token}"}
    print("Login successful, token retrieved.")
    
    # 2. Test Scenarios
    scenarios = [
        {"name": "Normal Traffic", "event_type": "Benign"},
        {"name": "Port Scan", "event_type": "Port Scan"},
        {"name": "Brute Force", "event_type": "Brute Force"},
        {"name": "DDoS", "event_type": "DDoS"},
        {"name": "Malware Traffic", "event_type": "Malware Traffic"}
    ]
    
    for s in scenarios:
        payload = {
            "source_ip": "192.168.1.100",
            "destination_ip": "10.0.0.5",
            "protocol": "TCP",
            "event_type": s["event_type"],
            "payload_size": 1500,
            "timestamp": "2026-09-30T10:00:00Z"
        }
        
        print(f"\n--- Testing Scenario: {s['name']} ---")
        r_analysis = client.post("/analysis/run", json=payload, headers=headers)
        if r_analysis.status_code != 200:
            print("Analysis failed:", r_analysis.text)
            continue
            
        result = r_analysis.json()
        print(f"Classification: {result.get('classification')}")
        print(f"Threat Score: {result.get('threat_score')}")
        
        # Verify incident creation
        r_incidents = client.get("/incidents/", headers=headers)
        incidents = r_incidents.json()
        print(f"Total Incidents after {s['name']}: {len(incidents)}")
        
if __name__ == "__main__":
    test_pipeline()
