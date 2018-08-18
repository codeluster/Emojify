package com.example.tanmay.emojify

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.support.v4.content.FileProvider
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class BitmapUtils {

    // this is equivalent to static in java
    companion object {

        private val FILE_PROVIDER_AUTHORITY = "com.example.android.fileprovider"

        val LOG_TAG = BitmapUtils::class.java.simpleName

        // Create a temporary file to store image
        fun createTempImageFile(context: Context): File {

            val imageFileName = generateFileName()

            // Get the cache directory
            val storageDir: File = context.getExternalCacheDir()

            val output = File.createTempFile(imageFileName /* prefix */,
                    ".jpg" /* suffix */,
                    storageDir /* directory */)

            // Log successful creation of temp file
            Log.v(LOG_TAG, "Successfully created temp file $imageFileName at ${storageDir.absolutePath}.")

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

        private fun generateFileName(): String {
            // Get the timestamp for unique file names
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(Date())

            // Name of the image
            return "JPEG_$timeStamp";
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

        fun saveImage(context: Context, image: Bitmap): String? {

            // Generate a unique name for the file
            val imageFileName = generateFileName() + ".jpg"

            // Get the genral path to public directory
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

            // Append name of app specific directory
            val storageDir = File("$path/Emojify")

            var success = true

            // Check if path exists
            if (!storageDir.exists()) {
                success = storageDir.mkdirs()
            }

            var savedImagePath: String? = null

            if (success) {
                // Create a new file
                val imageFile = File(storageDir, imageFileName)
                // Store the absolute path of the image
                savedImagePath = imageFile.absolutePath

                try {
                    val fOut = FileOutputStream(imageFile)
                    // Compress the Bitmap to a JPEG file and hand it to output stream
                    image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                    // Close the output stream
                    fOut.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // Add the image to system gallery
                galleryAddPic(context, savedImagePath)

                Log.v(LOG_TAG, "Image saved at $savedImagePath")


            }

            return savedImagePath

        }

        private fun galleryAddPic(context: Context, imagePath: String) {
            // Create a media scan intent
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            // Fetch file at the location
            val f = File(imagePath)
            // Create a content Uri for the file
            val contentUri = Uri.fromFile(f)
            // Set contentUri as data for intent
            mediaScanIntent.data = contentUri
            // Send out broadcast to all media scanners
            context.sendBroadcast(mediaScanIntent)

        }

        fun shareImage(context: Context, imagePath: String) {
            // Fetch the file
            val f = File(imagePath)
            // Create a new share intent
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/*"
            val photoUri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, f)
            shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri)
            context.startActivity(shareIntent)

        }

    }

}