package com.example.tanmay.emojify

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.util.SparseArray
import android.widget.Toast
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector

class Emojifier {

    companion object {

        val LOG_TAG = Emojifier::class.java.simpleName

        fun detectFaces(context: Context, image: Bitmap) {

            /* Setting “tracking enabled” to false is recommended
            for detection for unrelated individual images (as
            opposed to video or a series of consecutively captured
            still images), since this will give a more accurate result.
            But for detection on consecutive images (e.g., live video),
            having tracking enabled gives a more accurate and faster result.*/

            val detector = FaceDetector.Builder(context)
                    .setTrackingEnabled(false)
                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .build()

            // Build a Frame with the given Bitmap
            val frame = Frame.Builder().setBitmap(image).build()

            // faces is a collection of Face instances
            var faces: SparseArray<Face> = detector.detect(frame)

            // Log the number of faces
            Log.d(LOG_TAG, "detectFaces : number of faces = " + faces.size());

            // If there are no faces detected, show a Toast
            if (faces.size() == 0) {
                Toast.makeText(context, R.string.no_faces_message, Toast.LENGTH_SHORT).show();
            }

            detector.release()

        }

        fun getClassifications() {


        }

    }

}