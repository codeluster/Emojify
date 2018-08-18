package com.example.tanmay.emojify

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class BitmapUtils {

    // this is equivalent to static in java
    companion object {

        // Create a temporary file to store image
        fun createTempImageFile(context: Context): File {

            // Get the timestamp for unique file names
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(Date())

            // Name of the image
            val imageFileName = "JPEG_$timeStamp";

            // Get the cache directory
            val storageDir: File = context.getExternalCacheDir()

            val output = File.createTempFile(imageFileName /* prefix */,
                    ".jpg" /* suffix */,
                    storageDir /* directory */)

            // Log successful creation of temp file
            Log.v(BitmapUtils.javaClass.simpleName, "Successfully created temp file $imageFileName at ${storageDir.absolutePath}.")

            return output
        }

        // Delte the image file for a given path
        fun deleteImageFile(context: Context, imagePath: String): Boolean {

            // Get the file
            val imageFile = File(imagePath)

            // Delete the imageFile
            val deleted = imageFile.delete()

            // Show a toast if image couldn't be deleted
            if (!deleted) {
                val errorMessage = "Error finding image."
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }

            return deleted
        }

        // Resamples the captured photo to fit the screen for better memory usage
        fun resamplePic(context: Context, imagePath: String): Bitmap {

            // Get a windowManager object
            val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

            // Create a DisplayMetrics object
            val metrics = DisplayMetrics()

            // Store the Metrics in metrics instance of DisplayMetrics
            manager.defaultDisplay.getMetrics(metrics)

            val bitmapOptions = BitmapFactory.Options()
            bitmapOptions.inJustDecodeBounds = true

            // Decode the image and store information in bitmapOptions object
            BitmapFactory.decodeFile(imagePath, bitmapOptions)

            // Ratios of current:target sizes in pixels
            val widthRatio = bitmapOptions.outWidth / metrics.widthPixels
            val heightRatio = bitmapOptions.outHeight / metrics.heightPixels

            // Determine how much to scale down the image
            val scaleFactor = Math.min(widthRatio, heightRatio)

            // Decode the image file into a Bitmap sized to fill the View
            bitmapOptions.inJustDecodeBounds = false
            bitmapOptions.inSampleSize = scaleFactor

            return BitmapFactory.decodeFile(imagePath)

        }

    }

}