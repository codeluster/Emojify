package com.example.tanmay.emojify

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private companion object {
        val REQUEST_IMAGE_CAPTURE = 1289;
        val REQUEST_STORAGE_PERMISSION = 28497;

        val FILE_PROVIDER_AUTHORITY = "com.example.android.fileprovider"

        val LOG_TAG = MainActivity.javaClass.simpleName

    }

    var mEmojifyButton: Button? = null
    var mSaveFab: FloatingActionButton? = null
    var mClearFab: FloatingActionButton? = null
    var mShareFab: FloatingActionButton? = null
    var mTitleBox: TextView? = null

    // Path where temp photo is stored
    var mTempPhotoPath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mEmojifyButton = action_initiate_emojification as Button
        mSaveFab = action_save_image
        mClearFab = action_clear_image
        mShareFab = action_share_image
        mTitleBox = title_text

        val x93 = mEmojifyButton as View
        x93.setOnClickListener {
            emojifyMe()
        }

    }

    // Get's executed when mEmojifyButton is pressed
    fun emojifyMe() {

        // Check for external storage writing permissions
        if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // If permission not granted then request it
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_PERMISSION);

        } else {
            // Launch the camera if permission exists
            launchCamera();
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch camera
                    launchCamera();
                } else Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun launchCamera() {

        // Create the capture image intent
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {

            // Create the temporary file where the photo should go
            var photoFile: File? = null

            try {
                photoFile = BitmapUtils.createTempImageFile(this)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

            // Get the path of the temporary file
            mTempPhotoPath = photoFile?.absolutePath

            if (photoFile != null) {

                // Get the content Uri for the image file
                val photoUri: Uri = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY,
                        photoFile)

                // Add the Uri sp the camera can store the image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                // Launch the camera activity
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)

            } else Log.e(LOG_TAG, "Problem launching camera. photoFile was null.")
        }

    }
}
