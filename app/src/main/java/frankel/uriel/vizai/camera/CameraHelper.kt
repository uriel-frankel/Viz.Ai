package frankel.uriel.vizai.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import frankel.uriel.vizai.BuildConfig
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val PERMISSION_REQUEST_CODE = 101
const val REQUEST_TAKE_PHOTO = 102
const val PICK_IMAGE = 103
class CameraHelper(val activity: Activity, val photoPathListener: PhotoPathListener) {

    private lateinit var imageFileName: String
    private var currentPhotoPath: String? = null


    fun takePicture() {
        if (checkPermission()) {
            takePictureInner()
        } else {
            requestPermission()
        }
    }


    private fun checkPermission() =
        (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED)


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                Manifest.permission.CAMERA
            ), PERMISSION_REQUEST_CODE
        )
    }

    private fun takePictureInner() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        activity,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    activity.startActivityForResult(takePictureIntent,
                        REQUEST_TAKE_PHOTO
                    )
                }
            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED

                ) {
                    takePictureInner()
                } else {
                    Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {

            //To get the File for further usage
            // Get the dimensions of the View

            val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val image = File.createTempFile(
                imageFileName + "rotated", /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
            )

            ImageRotator.prepareAndStoreImageFile(
                File(
                    currentPhotoPath
                ), image
            )
            currentPhotoPath = Uri.fromFile(image).path
            photoPathListener.onPhotoReady(currentPhotoPath!!)

        }
    }


}