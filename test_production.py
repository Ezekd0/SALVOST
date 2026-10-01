import requests
import json
import time

BASE_URL = "https://cool-months-admire.loca.lt"
HEADERS = {"Bypass-Tunnel-Reminder": "true"}

def run_tests():
    print(f"Starting E2E tests against {BASE_URL}...")
    
    # 1. Health
    resp = requests.get(f"{BASE_URL}/health", headers=HEADERS)
    print(f"Health: {resp.status_code}")
    
    # 2. Login
    resp = requests.post(
        f"{BASE_URL}/auth/login",
        data={"username": "demo", "password": "demo123"},
        headers=HEADERS
    )
    assert resp.status_code == 200, f"Login failed: {resp.text}"
    token = resp.json()["access_token"]
    auth_headers = {**HEADERS, "Authorization": f"Bearer {token}"}
    print("Login: Success")

    # 3. Dashboard Metrics
    resp = requests.get(f"{BASE_URL}/dashboard/summary", headers=auth_headers)
    assert resp.status_code == 200, f"Dashboard failed: {resp.text}"
    print("Dashboard Metrics (Initial):", resp.json()["eventsAnalyzed"])

    # Scenarios
    scenarios = [
        {"name": "Normal Traffic", "payload": {"source_ip": "192.168.1.10", "destination_ip": "10.0.0.5", "destination_port": 443, "protocol": "TCP", "flow_duration": 100.0, "packet_rate": 50.0, "bytes_rate": 1000.0, "is_demo": True}},
        {"name": "Port Scan", "payload": {"source_ip": "192.168.1.11", "destination_ip": "10.0.0.6", "destination_port": 40000, "protocol": "TCP", "flow_duration": 5.0, "packet_rate": 500.0, "bytes_rate": 500.0, "is_demo": True}},
        {"name": "Brute Force", "payload": {"source_ip": "192.168.1.12", "destination_ip": "10.0.0.7", "destination_port": 22, "protocol": "TCP", "flow_duration": 2000.0, "packet_rate": 10.0, "bytes_rate": 100.0, "is_demo": True}},
        {"name": "DDoS", "payload": {"source_ip": "192.168.1.13", "destination_ip": "10.0.0.8", "destination_port": 80, "protocol": "UDP", "flow_duration": 50.0, "packet_rate": 5000.0, "bytes_rate": 50000.0, "is_demo": True}},
        {"name": "Malware Traffic", "payload": {"source_ip": "192.168.1.14", "destination_ip": "10.0.0.9", "destination_port": 8080, "protocol": "TCP", "flow_duration": 5000.0, "packet_rate": 20.0, "bytes_rate": 200.0, "is_demo": True}}
    ]

    for s in scenarios:
        print(f"\nRunning {s['name']}...")
        resp = requests.post(f"{BASE_URL}/analysis/run", json=s["payload"], headers=auth_headers)
        assert resp.status_code == 200, f"{s['name']} analysis failed: {resp.text}"
        data = resp.json()
        analysis = data.get('analysis', {})
        print(f"  Classification: {analysis.get('classification')}")
        print(f"  Score: {analysis.get('threat_score')}")
        print(f"  Response: {analysis.get('recommended_response')}")
        
        # Incident should be auto-created for non-Benign by the backend analysis router, 
        # but let's test if the incident API is reachable
        if analysis.get('classification') != "Benign":
            # Just test if the list endpoint works and see if incidents increased
            inc_resp = requests.get(f"{BASE_URL}/incidents/", headers=auth_headers)
            assert inc_resp.status_code == 200

    print("\nRe-checking Dashboard...")
    resp = requests.get(f"{BASE_URL}/dashboard/summary", headers=auth_headers)
    print("Dashboard Metrics (Final):", resp.json()["eventsAnalyzed"])
    
    print("\nChecking Live Events...")
    resp = requests.get(f"{BASE_URL}/events/", headers=auth_headers)
    assert resp.status_code == 200
    print(f"Live events found: {len(resp.json())}")

    print("\nChecking Incidents...")
    resp = requests.get(f"{BASE_URL}/incidents/", headers=auth_headers)
    assert resp.status_code == 200
    incidents = resp.json()
    print(f"Incidents found: {len(incidents)}")
    if len(incidents) > 0:
        inc = incidents[0]
        print(f"Sample Incident: {inc['id']} - {inc.get('title', inc.get('classification', 'Unknown'))} ({inc['status']})")

    print("\nChecking Responses...")
    resp = requests.get(f"{BASE_URL}/incidents/responses/recent", headers=auth_headers)
    assert resp.status_code == 200
    print(f"Responses found: {len(resp.json())}")

    print("\nAll API E2E Verification Tests Passed!")

if __name__ == "__main__":
    run_tests()
