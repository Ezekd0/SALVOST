import requests
import time

BASE_URL = "http://127.0.0.1:8000"

print("1. Testing Backend Health...")
try:
    resp = requests.get(f"{BASE_URL}/docs")
    if resp.status_code == 200:
        print("Backend is UP")
    else:
        print(f"Backend returned status {resp.status_code}")
except Exception as e:
    print(f"Backend health check failed: {e}")

print("\n2. Testing Login...")
login_data = {
    "username": "demo",
    "password": "demo123"
}
# FastAPI OAuth2PasswordRequestForm requires form data
resp = requests.post(f"{BASE_URL}/auth/login", data=login_data)
if resp.status_code == 200:
    token = resp.json().get("access_token")
    print("Login successful! Token acquired.")
else:
    print(f"Login failed! {resp.status_code} {resp.text}")
    exit(1)

headers = {"Authorization": f"Bearer {token}"}

print("\n3. Testing Dashboard Metrics...")
resp = requests.get(f"{BASE_URL}/dashboard/summary", headers=headers)
print(f"Dashboard status: {resp.status_code} - Data: {resp.json()}")

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

print("\n4. Testing Scenarios (Threat Analysis)...")
for s in scenarios:
    print(f"  Injecting {s['name']}...")
    resp = requests.post(f"{BASE_URL}/analysis/run", json=s["features"], headers=headers)
    
    # We should get back an event, the analysis is in the 'analysis' field
    if resp.status_code == 200:
        event = resp.json()
        analysis = event.get('analysis', {})
        print(f"  Status: {resp.status_code} - Result: {analysis.get('classification')} (Score: {analysis.get('threat_score')})")
    else:
        print(f"  Status: {resp.status_code} - Result: {resp.text}")
    time.sleep(1)

print("\n5. Verifying Incidents Created...")
resp = requests.get(f"{BASE_URL}/incidents/", headers=headers)
incidents = resp.json()
print(f"Total Incidents: {len(incidents)}")
if len(incidents) > 0:
    incident_id = incidents[0]['id']
    print(f"Fetching details for incident {incident_id}...")
    resp = requests.get(f"{BASE_URL}/incidents/{incident_id}", headers=headers)
    print(f"Incident {incident_id} details status: {resp.status_code}")

print("\n6. Verifying Dashboard Metrics Update...")
resp = requests.get(f"{BASE_URL}/dashboard/summary", headers=headers)
print(f"Dashboard status: {resp.status_code} - Data: {resp.json()}")

print("\n7. Verifying Response Center...")
resp = requests.get(f"{BASE_URL}/incidents/responses/recent", headers=headers)
print(f"Response center status: {resp.status_code} - Responses: {len(resp.json())}")

print("\nTest Script Completed.")
