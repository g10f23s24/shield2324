import firebase_admin
from firebase_admin import credentials, db, messaging, storage
from flask import Flask
import json
import datetime
import csv
import random
import math
from gpiozero import MotionSensor
from picamera2 import Picamera2
import numpy as np
import io
import os
from time import sleep
from PIL import Image
import signal
import sys
import bluetooth

# Flask app initialization
app = Flask(__name__)

# Set up the PIR sensor.
pir = MotionSensor(26)

# Load paired devices
paired_devices = {}
with open('paired_devices.json', 'r') as file:
    paired_devices = json.load(file)

# Constants for RSSI simulation
REFERENCE_RSSI = -40
PATH_LOSS_EXPONENT = 2.0

# Firebase admin setup
cred = credentials.Certificate('push.json')
firebase_admin.initialize_app(cred, {
    "databaseURL": "https://shields-32fbc-default-rtdb.firebaseio.com",
    "projectId": "shields-32fbc",
    "storageBucket": "shields-32fbc.appspot.com",
    "messagingSenderId": "965835072665"
})
bucket = storage.bucket()

# Flag to indicate if face recognition is running
face_recognition_running = False

def simulate_rssi(distance):
    path_loss = 10 * PATH_LOSS_EXPONENT * math.log10(distance)
    rssi = REFERENCE_RSSI - path_loss
    # Introduce randomness to simulate real-world fluctuations
    rssi += random.uniform(-5, 5)

    # Adjust RSSI values to simulate weak and strong signals
    if rssi < -85:
        # Simulate weak signal (< -85 dBm)
        rssi = random.uniform(-90, -86)
    elif -85 <= rssi <= -60:
        # Simulate moderate signal (-85 dBm to -60 dBm)
        rssi = random.uniform(-85, -61)
    else:
        # Simulate strong signal (>= -60 dBm)
        rssi = random.uniform(-60, -40)
    
    return rssi


def fetch_device_tokens():
    tokens_ref = db.reference('DeviceTokens')
    raw_tokens = tokens_ref.get()
    tokens = []
    if raw_tokens:
        for device_id, token_info in raw_tokens.items():
            if 'token' in token_info:
                tokens.append(token_info['token'])
    return tokens

def take_snapshot():
    try:
        cam = Picamera2()
        config = cam.create_still_configuration()
        cam.configure(config)
        cam.start()
        sleep(2)  # Camera warm-up time

        image = cam.capture_array()
        cam.stop()
        cam.close()  # Ensure the camera is properly closed

        # Convert numpy array to PIL Image and save to BytesIO stream
        img = Image.fromarray(np.uint8(image))
        stream = io.BytesIO()
        img.save(stream, format='JPEG')
        stream.seek(0)
        return stream
    except Exception as e:
        print(f"Error taking snapshot: {e}")
        if cam:
            cam.stop()
            cam.close()
        return None

def upload_image_to_firebase(stream):
    """Uploads the image stream to Firebase Storage and returns the public URL of the image."""
    # Organize images into folders by date
    date_folder = datetime.datetime.now().strftime('%Y-%m-%d')
    file_path = f"Snapshot Images/{date_folder}/motion_{datetime.datetime.now().strftime('%Y%m%d%H%M%S')}.jpg"
    
    blob = bucket.blob(file_path)
    blob.upload_from_file(stream, content_type='image/jpeg')
    blob.make_public()
    return blob.public_url

def send_push_notification(tokens, title, body, image_url=None):
    """Send a push notification with the image URL included."""
    for token in tokens:
        message = messaging.Message(
            notification=messaging.Notification(title=title, body=body),
            token=token,
            android=messaging.AndroidConfig(
                notification=messaging.AndroidNotification(image=image_url)
            )
        )
        response = messaging.send(message)
        print('Successfully sent message.')

def check_paired_devices_in_range(paired_devices):
    print("Scanning for registered devices...")
    nearby_devices = bluetooth.discover_devices(duration=8, lookup_names=True)
    print("Nearby devices found:", nearby_devices)
    for addr, name in nearby_devices:
        if addr in paired_devices:
            print(f"Registered device {name} ({addr}) is in range with user ID {paired_devices[addr]['user_id']}.")
            return paired_devices[addr]['user_id']
    print("No registered devices are in range.")
    return None

def motion_detected():
    print("Motion detected!")
    distance = random.uniform(0.5, 10)
    rssi = simulate_rssi(distance)
    print(f"Simulated RSSI: {rssi} at distance: {distance}m")
    
    # Always take a snapshot when motion is detected
    os.environ['DISPLAY'] = ':0'
    global face_recognition_running
    if not face_recognition_running:
        image_stream = take_snapshot()
        if image_stream:
            image_url = upload_image_to_firebase(image_stream)
            device_tokens = fetch_device_tokens()
            if device_tokens:
                # Check if any registered device is in range
                user_id = check_paired_devices_in_range(paired_devices)
                if user_id:
                    print(f"Registered device in range with user ID: {user_id}")
                    send_push_notification(device_tokens, 'SHIELDS system', 'Motion detected and registered device is nearby.', image_url)
                else:
                    send_push_notification(device_tokens, 'SHIELDS system', 'Motion detected but no registered devices in range.', image_url)
            else:
                print("No device tokens available.")
        else:
            print("Failed to take a snapshot.")

# Signal handling for graceful shutdown
def signal_handler(sig, frame):
    print("Signal received: ", sig)
    print("Shutting down, please wait...")
    pir.close()  # Properly close the PIR sensor
    os._exit(0)  # Forceful exit

signal.signal(signal.SIGINT, signal_handler)
signal.signal(signal.SIGTERM, signal_handler)

if __name__ == '__main__':
    try:
        pir.when_motion = motion_detected
        app.run(host='0.0.0.0', port=5000)
    finally:
        pir.close()
        print("Application closed gracefully.")
