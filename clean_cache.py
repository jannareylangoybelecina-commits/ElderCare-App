import os
import shutil
import time
import subprocess

# 1. Kill Gradle Daemons carefully
try:
    print("Stopping Gradle Daemons...")
    output = subprocess.check_output('wmic process where "name=\'java.exe\'" get ProcessId,CommandLine', shell=True).decode(errors='ignore')
    for line in output.splitlines():
        if "GradleDaemon" in line or "gradle" in line:
            parts = line.strip().split()
            if parts:
                pid = parts[-1]
                if pid.isdigit():
                    print(f"Killing Gradle PID: {pid}")
                    os.system(f"taskkill /F /PID {pid}")
except Exception as e:
    print("Error stopping daemons:", e)

time.sleep(2) # Give OS time to release file locks

# 2. Clean global caches
cache_path = r"C:\Users\Admin\.gradle\caches\8.9"
print(f"Attempting to delete {cache_path}")
if os.path.exists(cache_path):
    try:
        shutil.rmtree(cache_path)
        print("Success: Removed global 8.9 cache.")
    except Exception as e:
        print(f"Failed to delete global cache: {e}")
        os.system(f'rmdir /s /q "{cache_path}"')

# 3. Clean local project caches
local_path = r"D:\AndroidStudioProjects\ElderCare\.gradle"
print(f"Attempting to delete {local_path}")
if os.path.exists(local_path):
    try:
        shutil.rmtree(local_path)
        print("Success: Removed local .gradle cache.")
    except Exception as e:
        print(f"Failed to delete local cache: {e}")
        os.system(f'rmdir /s /q "{local_path}"')
