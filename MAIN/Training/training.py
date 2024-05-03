#! /usr/bin/python

# import the necessary packages
from imutils import paths
import face_recognition
import pickle
import cv2
import os
import tempfile
import pyrebase
import firebase_admin
from firebase_admin import credentials, storage

config = {
  "apiKey": "AIzaSyCYJl3u-m0aynyAnysMVv_oumHjd43Ph18",
  "authDomain": "shields-32fbc.firebaseapp.com",
  "databaseURL": "https://shields-32fbc-default-rtdb.firebaseio.com",
  "projectId": "shields-32fbc",
  "storageBucket": "shields-32fbc.appspot.com",
  "messagingSenderId": "965835072665",
  "appId": "1:965835072665:web:f2405c1005627fbceb8408"
};


cred = credentials.Certificate('shieldskey.json')  # Replace with your service account key JSON file
firebase = firebase_admin.initialize_app(cred, config)
bucket = storage.bucket()

# our images are located in the images folder in Firebase Storage
print("[INFO] Beginning training process...")

# initialize the list of known encodings and known names
knownEncodings = []
knownNames = []

# function to download image from Firebase Storage
blob_iterator = bucket.list_blobs()

# loop over the image paths
for blob in blob_iterator:
    if blob.name.startswith('images/') and '/' in blob.name:
      if not blob.name.endswith('/'):
        # Exctract person's name from subfolder.
        person = blob.name.split('/')[1]
        
        # Download images.
        _, temp_local_filename = tempfile.mkstemp()
        blob.download_to_filename(temp_local_filename)
        # download the input image and convert it from RGB (OpenCV ordering)
        # to dlib ordering (RGB)
        image = cv2.imread(temp_local_filename)
        rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

        # detect the (x, y)-coordinates of the bounding boxes
        # corresponding to each face in the input image
        #boxes = face_recognition.face_locations(rgb, model="hog")
        boxes = face_recognition.face_locations(rgb, model="hog")

        # compute the facial embedding for the face
        encodings = face_recognition.face_encodings(rgb, boxes)

        # loop over the encodings
        for encoding in encodings:
            # add each encoding + name to our set of known names and
            # encodings
            knownEncodings.append(encoding)
            knownNames.append(person)
            
        # Remove the temporary file.
        os.remove(temp_local_filename)

# dump the facial encodings + names to disk
print("[INFO] Training complete...serializing encodings...")
data = {"encodings": knownEncodings, "names": knownNames}
f = open("face_encodings.pickle", "wb")
f.write(pickle.dumps(data))
f.close()
