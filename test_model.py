import os
import sys
sys.path.append(os.path.join(os.path.dirname(__file__), 'backend'))

from app.ml.predict import predict_event

scenarios = [
    {
        "name": "Normal Traffic", 
        "features": {
            "source_ip": "192.168.1.10", "destination_ip": "10.0.0.5", 
            "destination_port": 443, "protocol": "TCP", 
            "flow_duration": 100.0, "packet_rate": 50.0, "bytes_rate": 1000.0, "is_demo": True
        }
    },
    {
        "name": "Port Scan", 
        "features": {
            "source_ip": "192.168.1.11", "destination_ip": "10.0.0.6", 
            "destination_port": 40000, "protocol": "TCP", 
            "flow_duration": 5.0, "packet_rate": 500.0, "bytes_rate": 500.0, "is_demo": True
        }
    },
    {
        "name": "Brute Force", 
        "features": {
            "source_ip": "192.168.1.12", "destination_ip": "10.0.0.7", 
            "destination_port": 22, "protocol": "TCP", 
            "flow_duration": 2000.0, "packet_rate": 10.0, "bytes_rate": 100.0, "is_demo": True
        }
    },
    {
        "name": "DDoS", 
        "features": {
            "source_ip": "192.168.1.13", "destination_ip": "10.0.0.8", 
            "destination_port": 80, "protocol": "UDP", 
            "flow_duration": 50.0, "packet_rate": 5000.0, "bytes_rate": 50000.0, "is_demo": True
        }
    },
    {
        "name": "Malware Traffic", 
        "features": {
            "source_ip": "192.168.1.14", "destination_ip": "10.0.0.9", 
            "destination_port": 8080, "protocol": "TCP", 
            "flow_duration": 5000.0, "packet_rate": 20.0, "bytes_rate": 200.0, "is_demo": True
        }
    }
]

for s in scenarios:
    print(f"\n--- {s['name']} ---")
    res = predict_event(s["features"])
    print(f"Prediction: {res['classification']}")
    print(f"Threat Score: {res['threat_score']:.2f}")
    print(f"Severity: {res['severity']}")

