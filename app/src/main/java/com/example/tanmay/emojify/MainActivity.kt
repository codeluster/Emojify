package com.example.tanmay.emojify

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import android.widget.ImageView
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

        const val FILE_PROVIDER_AUTHORITY = "com.tanmay.emojify.fileprovider"

        val LOG_TAG = MainActivity::class.java.simpleName

    }

    var mEmojifyButton: Button? = null
    var mSaveFab: FloatingActionButton? = null
    var mClearFab: FloatingActionButton? = null
    var mShareFab: FloatingActionButton? = null
    var mTitleBox: TextView? = null
    var mImageFrame: ImageView? = null

    // Path where temp photo is stored
    var mTempPhotoPath: String? = null

    // Bitmap object where image is stored
    var mResultsBitmap: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mEmojifyButton = action_initiate_emojification
        mSaveFab = action_save_image
        mClearFab = action_clear_image
        mShareFab = action_share_image
        mTitleBox = title_text
        mImageFrame = imageView

        mEmojifyButton?.setOnClickListener { emojifyMe() }
        mSaveFab?.setOnClickListener { saveImage() }
        mClearFab?.setOnClickListener { clearImage() }
        mShareFab?.setOnClickListener { shareImage() }

    }


    // Get's executed when mEmojifyButton is pressed
    fun emojifyMe() {

        // Check for external storage writing permissions
        if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // If permission not granted then request it
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_PERMISSION)

        } else {
            // Launch the camera if permission exists
            launchCamera();
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    launchCamera()
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // If the image capture activity was successful
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Process the image and set it to the ImageView
            processAndSetImage()
        } else {
            // Otherwise, delete the temporary image file
            BitmapUtils.deleteImageFile(this, mTempPhotoPath!!)
        }

    }

    fun processAndSetImage() {

        // Toggle the views
        toggleViews()

        // Resample the saved image to fit the ImageView
        mResultsBitmap = BitmapUtils.resamplePic(this, mTempPhotoPath!!)

        // Set the new Bitmap to the ImageView
        mImageFrame?.setImageBitmap(mResultsBitmap)

    }

    fun toggleViews(clear: Boolean = false) {

        if (clear) {

            val s0f = mImageFrame as ImageView
            s0f.setImageResource(0)

            mImageFrame?.visibility = View.GONE
            mSaveFab?.visibility = View.GONE
            mClearFab?.visibility = View.GONE
            mShareFab?.visibility = View.GONE

            mEmojifyButton?.visibility = View.VISIBLE
            mTitleBox?.visibility = View.VISIBLE


        } else {
            mEmojifyButton?.visibility = View.GONE
            mTitleBox?.visibility = View.GONE

            mImageFrame?.visibility = View.VISIBLE
            mSaveFab?.visibility = View.VISIBLE
            mClearFab?.visibility = View.VISIBLE
            mShareFab?.visibility = View.VISIBLE
        }

    }

    fun saveImage() {

        if (mTempPhotoPath != null) {
            // Delete the temporary file
            BitmapUtils.deleteImageFile(this, mTempPhotoPath!!)
            // Save the high quality image
            BitmapUtils.saveImage(this, mResultsBitmap!!)
        }
    }

    fun shareImage() {

        if (mTempPhotoPath != null && mResultsBitmap != null) {
            // Delete the temporary image file
            BitmapUtils.deleteImageFile(this, mTempPhotoPath!!)

            // Save the image
            BitmapUtils.saveImage(this, mResultsBitmap!!)

            // Share the image
            BitmapUtils.shareImage(this, mTempPhotoPath!!)
        }
    }

    fun clearImage() {
        toggleViews(clear = true)

        // Delete the temporary image file
        BitmapUtils.deleteImageFile(this, mTempPhotoPath!!)
    }

}
