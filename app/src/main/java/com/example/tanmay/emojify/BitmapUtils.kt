package com.example.tanmay.emojify

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class BitmapUtils {

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
            Log.v(BitmapUtils.javaClass.simpleName, "Successully created temp file $imageFileName at ${storageDir.absolutePath}.")

            return output
        }


    }

}