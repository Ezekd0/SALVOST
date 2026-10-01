import requests

BASE_URL = "http://localhost:8000"

def run_tests():
    print("Testing Registration...")
    reg_payload = {
        "full_name": "Test Evaluator 3",
        "email": "test3@evaluator.com",
        "username": "testeval3",
        "password": "Password123!",
        "confirm_password": "Password123!"
    }
    r = requests.post(f"{BASE_URL}/auth/register", json=reg_payload)
    print("Register response:", r.status_code, r.text)

    print("\nTesting Login...")
    login_payload = {
        "username": "testeval3",
        "password": "Password123!"
    }
    r = requests.post(f"{BASE_URL}/auth/login", data=login_payload)
    print("Login response:", r.status_code)
    token = r.json().get("access_token")
    headers = {"Authorization": f"Bearer {token}"}

    print("\nTesting Dashboard (No events)...")
    r = requests.get(f"{BASE_URL}/dashboard/summary", headers=headers)
    print("Dashboard response:", r.status_code, r.text)

    print("\nTesting Threat Analysis (Port Scan)...")
    analysis_payload = {
      "source_ip": "198.51.100.14",
      "destination_ip": "10.0.0.25",
      "destination_port": 22,
      "protocol": "TCP",
      "flow_duration": 0.05,
      "packet_rate": 500,
      "bytes_rate": 300,
      "is_demo": True
    }
    r = requests.post(f"{BASE_URL}/analysis/run", json=analysis_payload, headers=headers)
    print("Analysis response:", r.status_code, r.text)

    print("\nTesting Dashboard (With events)...")
    r = requests.get(f"{BASE_URL}/dashboard/summary", headers=headers)
    print("Dashboard response:", r.status_code, r.text)

    print("\nTesting Incidents...")
    r = requests.get(f"{BASE_URL}/incidents/", headers=headers)
    print("Incidents response:", r.status_code, r.text)

    print("\nTesting Registration 4 (Isolation)...")
    reg_payload2 = {
        "full_name": "Test Evaluator 4",
        "email": "test4@evaluator.com",
        "username": "testeval4",
        "password": "Password123!",
        "confirm_password": "Password123!"
    }
    r = requests.post(f"{BASE_URL}/auth/register", json=reg_payload2)
    print("Register 2 response:", r.status_code, r.text)

    r = requests.post(f"{BASE_URL}/auth/login", data={"username": "testeval4", "password": "Password123!"})
    token2 = r.json().get("access_token")
    headers2 = {"Authorization": f"Bearer {token2}"}

    print("\nTesting Dashboard 4 (No events for isolated user)...")
    r = requests.get(f"{BASE_URL}/dashboard/summary", headers=headers2)
    print("Dashboard 2 response:", r.status_code, r.text)

if __name__ == "__main__":
    run_tests()
