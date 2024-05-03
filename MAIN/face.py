import face_recognition
from picamera2 import Picamera2
from imutils import paths
import pickle
import os
import sys
import cv2
import numpy as np
import math
from time import sleep
from gpiozero import OutputDevice
#|====================================================================================================|
# Calculating confidence for match percentage.
def face_confidence(face_distance, face_match_threshold=0.6):
    range = (1.0 - face_match_threshold)
    linear_val = (1.0 - face_distance) / (range * 2.0)

    if face_distance > face_match_threshold:
        return str(round(linear_val * 100, 2)) + '%'
    else:
        value = (linear_val + ((1.0 - linear_val) * math.pow((linear_val - 0.5) * 2, 0.2))) * 100
        return str(round(value, 2)) + '%'
#|====================================================================================================|
# Camera Configuration.        
cam = Picamera2()   
config = cam.create_preview_configuration ( main = { "size" : ( 512, 304)})
cam.preview_configuration.main.format = "YUV420"
cam.configure(config)
cam.start()
#|====================================================================================================|
class FaceRecognition:
    face_locations = []
    face_encodings = []
    face_names = []
    known_face_encodings = []
    known_face_names = []
    process_current_frame = True
    door_unlocked = False  # Flag to track door unlock status.
#|====================================================================================================|
    def __init__(self):
        self.load_encodings()
        self.initialize_solenoid()  # Initialize Solenoid.
#|====================================================================================================|
    def load_encodings(self):
        try:
            with open("/Training/face_encodings.pickle", "rb") as f: # Open pickle file with serial encodings.
                data = pickle.load(f)
                self.known_face_encodings = np.array(data['encodings'])
                self.known_face_names = data['names']
        except Exception as e:
            print("Error loading encodings:", e)
#|====================================================================================================|    
    def initialize_solenoid(self):
        # Cleanup pins before usage.
        if hasattr(self, 'solenoid'):
            self.solenoid.close()
        # setup for solenoid control, pin 18.
        # Initial value set to on (set True) so program starts with "locked door".
        self.solenoid = OutputDevice(18, initial_value=True)
#|====================================================================================================|       
    def run_recognition(self):
        while True:
            frame = cam.capture_array()
            if frame is None:
                print("Frame is None, retrying...")
                continue  # Skip processing this frame.

            if self.process_current_frame:
                small_frame = cv2.resize(frame, (0, 0), fx=0.25, fy=0.25)
                rgb_small_frame = cv2.cvtColor(small_frame, cv2.COLOR_BGR2RGB)
                self.face_locations = face_recognition.face_locations(rgb_small_frame)
                self.face_encodings = face_recognition.face_encodings(rgb_small_frame, self.face_locations)
                self.face_names = []
                 
                for face_encoding in self.face_encodings:
                    matches = face_recognition.compare_faces(self.known_face_encodings, face_encoding)
                    name = 'Unknown'
                    confidence = '0%'
                    face_distances = face_recognition.face_distance(self.known_face_encodings, face_encoding)
                    best_match_index = np.argmin(face_distances)

                    if matches[best_match_index]:
                        name = self.known_face_names[best_match_index]
                        confidence = face_confidence(face_distances[best_match_index])
                        # Check if the confidence is greater than 80%.
                        if float(confidence[:-1]) > 80:
                            if not self.door_unlocked:
                                print("Match found...")
                                self.unlock_door() # If so, unlock door.
                                self.door_unlocked = True  # Update flag
                                print("...Door is unlocked.")  # Print unlock confirmation
                        else:
                            if not self.door_unlocked:
                                print("No Match found.")
                                self.solenoid.on()  # else, keep door locked.
                    else:
                        if not self.door_unlocked:
                            print("No Match found.")
                            self.solenoid.on()  # else, keep door locked.
                        
                    # Append known name with match percentage.
                    self.face_names.append(f'{name} ({confidence})')
                  
            self.process_current_frame = not self.process_current_frame
            for (top, right, bottom, left), name in zip(self.face_locations, self.face_names):
                top *= 4
                right *= 4
                bottom *= 4
                left *= 4
                # Set bounding box color to red for unknown faces, green for known faces.                
                color = (0, 0, 255) if 'Unknown' in name else (0, 255, 0)
                # Draw Rectangle (bounding box).
                cv2.rectangle(frame, (left, top), (right, bottom), color, 2)
                cv2.rectangle(frame, (left, bottom - 35), (right, bottom), color, -1)
                cv2.putText(frame, name, (left + 6, bottom - 6), cv2.FONT_HERSHEY_DUPLEX, 0.8, (255, 255, 255), 1)
            # Title of face recognition window.
            cv2.imshow('Face Recognition', frame)
            if cv2.waitKey(1) == ord('q'): # Exit program when q is pressed.
                break
        # Stop camera.
        cam.stop()
#|====================================================================================================|
    def unlock_door(self):
        self.solenoid.off() # Turn off solenoid to unlock door.
#|====================================================================================================|        
if __name__ == '__main__':
    try:
        fr = FaceRecognition() 
        fr.initialize_solenoid()  # Initialize solenoid control.
        fr.run_recognition()
    except KeyboardInterrupt:
        if hasattr(fr, 'solenoid'):
            self.solenoid.close() # Close the solenoid on KeyboardInterrupt.
#|====================================================================================================|
