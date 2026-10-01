import subprocess
import time
import sys

print("Starting uvicorn...")
server = subprocess.Popen(["venv/bin/uvicorn", "app.main:app", "--host", "127.0.0.1", "--port", "8000"], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)

time.sleep(5)
print("Running test pipeline...")
test = subprocess.run(["venv/bin/python", "test_pipeline.py"], capture_output=True, text=True)
print("Test output:", test.stdout)
print("Test errors:", test.stderr)

print("Terminating server...")
server.terminate()
stdout, _ = server.communicate()
print("Uvicorn Output:\n", stdout)
print("Done!")
