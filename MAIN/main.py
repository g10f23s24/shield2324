import threading
import time
import subprocess
import bluetooth
import json
import os
import signal
import sys

from motion import motion_detected, check_paired_devices_in_range, fetch_device_tokens, take_snapshot, upload_image_to_firebase, send_push_notification
from tag import discover_devices, first_time_setup, load_paired_devices, manage_face_recognition

def main():
    # Initialize Firebase, Bluetooth, and load paired devices
    first_time_setup()  # Only necessary for the first run
    paired_devices = load_paired_devices()

    try:
        while True:
            motion_thread = threading.Thread(target=motion_detection_thread, args=(paired_devices,))
            bluetooth_thread = threading.Thread(target=bluetooth_scanning_thread, args=(paired_devices,))
            
            motion_thread.start()
            bluetooth_thread.start()

            motion_thread.join()
            bluetooth_thread.join()

            # Restart the loop if needed
            time.sleep(5)

    except KeyboardInterrupt:
        print("Gracefully exiting...")
        sys.exit(0)

def motion_detection_thread(paired_devices):
    motion_detected()

def bluetooth_scanning_thread(paired_devices):
    authorized_device_found = discover_devices()
    if authorized_device_found:
        addr, user_id = check_paired_devices_in_range(paired_devices)
        if addr and user_id:
            if not is_paired(addr):
                # Assign the next available user ID and add to paired devices
                next_user_id = f"user_{len(paired_devices) + 1}"
                paired_devices[addr] = {'name': '', 'user_id': next_user_id}
                db.reference('tags').child(addr).set(paired_devices[addr])
                # Update local JSON file
                with open(local_devices_file, 'w') as f:
                    json.dump(paired_devices, f)
            rssi = simulate_rssi(addr)
            if rssi < -90:
                # Send push notification with snapshot
                image_stream = take_snapshot()
                if image_stream:
                    image_url = upload_image_to_firebase(image_stream)
                    device_tokens = fetch_device_tokens()
                    if device_tokens:
                        send_push_notification(device_tokens, 'Motion Detected', 'Unauthorized device detected.', image_url)
            elif -85 <= rssi <= -60:
                # Start face recognition
                manage_face_recognition(start=True, addr=addr, user_id=user_id)

if __name__ == "__main__":
    main()
