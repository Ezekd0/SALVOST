import subprocess
import time

backend = subprocess.Popen(["venv/bin/python", "-m", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"], cwd="backend")
frontend = subprocess.Popen(["npm", "run", "preview", "--", "--host", "0.0.0.0", "--port", "4173"], cwd="frontend")

try:
    while True:
        time.sleep(1)
except KeyboardInterrupt:
    backend.terminate()
    frontend.terminate()
