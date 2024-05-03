import firebase_admin
from firebase_admin import db, credentials
import bluetooth
import subprocess
import pyrebase
import time
import os
import json
import signal
import sys
#|====================================================================================================|
# Check if the app is already initialized
if not firebase_admin._apps:
    # If not initialized, initialize the app
    cred = credentials.Certificate('push.json')
    firebase_admin.initialize_app(cred, {
        "databaseURL": "https://shields-32fbc-default-rtdb.firebaseio.com",
        "projectId": "shields-32fbc",
        "storageBucket": "shields-32fbc.appspot.com",
        "messagingSenderId": "965835072665"
    })
#|====================================================================================================|
# Global variable setup.
paired_devices = {}
tags_directory = "tags"
local_devices_file = os.path.join(tags_directory, "paired_devices.json")
#|====================================================================================================|
# First time setup, if running program the very first time, add new tags.
def first_time_setup():
    # Create local directory for tags if it doesn't exist.
    os.makedirs(tags_directory, exist_ok=True)
    
    # Check already paired devices.
    result = subprocess.run(['bluetoothctl', 'devices'], capture_output=True, text=True)
    devices = [line.split(' ', 2) for line in result.stdout.splitlines() if line]
    print("Please select the numbers corresponding to phones:")
    for i, (_, addr, name) in enumerate(devices):
        print(f"{i + 1}: {name} ({addr})")
    
    selected_indices = input("Enter the numbers of devices that are phones (comma separated): ")
    selected_indices = [int(x.strip()) - 1 for x in selected_indices.split(",")]

    # Store device info in Firebase (Real Time Database) and local JSON file.
    for index in selected_indices:
        _, addr, name = devices[index]
        user_id = f"user_{index + 1}"
        paired_devices[addr] = {'name': name, 'user_id': user_id}
        db.reference('tags').child(addr).set(paired_devices[addr])

    # Write to local JSON file.
    with open(local_devices_file, 'w') as f:
        json.dump(paired_devices, f)
#|====================================================================================================|
def load_paired_devices():
    if os.path.exists(local_devices_file):
        with open(local_devices_file, 'r') as f:
            return json.load(f)
    return {}
#|====================================================================================================|
def is_paired(addr):
    return addr in paired_devices
#|====================================================================================================|
def manage_face_recognition(start=True, addr=None, user_id=None):
    action = "Starting" if start else "Stopping"
    print(f"{action} face recognition and RSSI data collection...")
    os.environ['DISPLAY'] = ':0'
    subprocess.Popen(["python", "face.py"]) if start else subprocess.run(["pkill", "-f", "face.py"])
    if start and addr and user_id:
        subprocess.Popen(["python", "rssi_data.py", addr, user_id])
    else:
        subprocess.run(["pkill", "-f", "rssi_data.py"])
#|====================================================================================================|
def discover_devices():
    print("Scanning for Bluetooth devices...")
    start_time = time.time()
    devices = bluetooth.discover_devices(duration=8, lookup_names=True, flush_cache=True, lookup_class=True)
    print(f"Devices found: {devices}")  # Debug: List all detected devices.
    found_authorized_device = False
    for addr, name, _ in devices:
        if addr in paired_devices:
            elapsed_time = time.time() - start_time
            user_id = paired_devices[addr]['user_id']
            print(f"Authorized User ID found: {user_id}, time taken: {elapsed_time:.2f}s")
            found_authorized_device = True
        else:
            print(f"Unrecognized or unauthorized device: {name} ({addr})")
    return found_authorized_device
#|====================================================================================================|
def main():
    global paired_devices
    if not os.path.exists(local_devices_file):
        first_time_setup()

    paired_devices = load_paired_devices()
    print(f"Loaded paired devices: {paired_devices}")  # Debug: Check loaded devices.

    try:
        while True:
            authorized_device_found = discover_devices()
            if authorized_device_found:
                manage_face_recognition(start=True)
                time.sleep(30)  # Run face recognition for 30 seconds.
                manage_face_recognition(start=False)

                restart_scan = input("Do you want to restart the scan? (yes/no): ")
                if restart_scan.lower() != "yes":
                    break
            else:
                print("No authorized devices detected. Waiting before next scan...")
                time.sleep(5)  # Wait before scanning again.
    except KeyboardInterrupt:
        print("Gracefully exiting...")
    finally:
        manage_face_recognition(start=False)  # Ensuring subprocesses are terminated.
#|====================================================================================================|
if __name__ == "__main__":
    main()
