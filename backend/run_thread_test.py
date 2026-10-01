import uvicorn
from app.main import app
import threading
import time
import requests

def run_server():
    uvicorn.run(app, host="127.0.0.1", port=8000)

if __name__ == "__main__":
    t = threading.Thread(target=run_server, daemon=True)
    t.start()
    time.sleep(3)
    
    print("Testing ML Pipeline End-to-End...")
    
    BASE_URL = "http://127.0.0.1:8000"
    
    # 1. Login
    login_data = {"username": "demo", "password": "demo123"}
    r = requests.post(f"{BASE_URL}/auth/login", data=login_data)
    if r.status_code != 200:
        print("Login failed:", r.text)
        import sys
        sys.exit(1)
        
    token = r.json().get("access_token")
    headers = {"Authorization": f"Bearer {token}"}
    print("Login successful.")
    
    # 2. Test Scenarios
    scenarios = [
        {"name": "Normal Traffic", "event_type": "Benign"},
        {"name": "Port Scan", "event_type": "Port Scan"},
        {"name": "Brute Force", "event_type": "Brute Force"},
        {"name": "DDoS", "event_type": "DDoS"},
        {"name": "Malware Traffic", "event_type": "Malware Traffic"}
    ]
    
    for s in scenarios:
        if s["event_type"] == "Benign":
            payload = {
                "source_ip": "192.168.1.100", "destination_ip": "10.0.0.5",
                "destination_port": 80, "protocol": "TCP", "event_type": s["event_type"],
                "payload_size": 1500, "flow_duration": 100, "packet_rate": 50, "bytes_rate": 1000,
                "timestamp": "2026-09-30T10:00:00Z"
            }
        elif s["event_type"] == "Port Scan":
            payload = {
                "source_ip": "192.168.1.100", "destination_ip": "10.0.0.5",
                "destination_port": 50000, "protocol": "TCP", "event_type": s["event_type"],
                "payload_size": 1500, "flow_duration": 5, "packet_rate": 500, "bytes_rate": 500,
                "timestamp": "2026-09-30T10:00:00Z"
            }
        elif s["event_type"] == "Brute Force":
            payload = {
                "source_ip": "192.168.1.100", "destination_ip": "10.0.0.5",
                "destination_port": 22, "protocol": "TCP", "event_type": s["event_type"],
                "payload_size": 1500, "flow_duration": 2000, "packet_rate": 10, "bytes_rate": 100,
                "timestamp": "2026-09-30T10:00:00Z"
            }
        elif s["event_type"] == "DDoS":
            payload = {
                "source_ip": "192.168.1.100", "destination_ip": "10.0.0.5",
                "destination_port": 80, "protocol": "TCP", "event_type": s["event_type"],
                "payload_size": 1500, "flow_duration": 50, "packet_rate": 5000, "bytes_rate": 50000,
                "timestamp": "2026-09-30T10:00:00Z"
            }
        elif s["event_type"] == "Malware Traffic":
            payload = {
                "source_ip": "192.168.1.100", "destination_ip": "10.0.0.5",
                "destination_port": 4444, "protocol": "TCP", "event_type": s["event_type"],
                "payload_size": 1500, "flow_duration": 5000, "packet_rate": 20, "bytes_rate": 200,
                "timestamp": "2026-09-30T10:00:00Z"
            }
        
        print(f"\n--- Testing Scenario: {s['name']} ---")
        r_analysis = requests.post(f"{BASE_URL}/analysis/run", json=payload, headers=headers)
        if r_analysis.status_code != 200:
            print("Analysis failed:", r_analysis.text)
            continue
            
        result = r_analysis.json()
        analysis = result.get('analysis') or {}
        print(f"Classification: {analysis.get('classification')}")
        print(f"Threat Score: {analysis.get('threat_score')}")
        
        # Verify incident creation
        r_incidents = requests.get(f"{BASE_URL}/incidents/", headers=headers)
        incidents = r_incidents.json()
        print(f"Total Incidents after {s['name']}: {len(incidents)}")
        
    print("\nAll tests completed.")
