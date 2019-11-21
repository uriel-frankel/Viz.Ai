package frankel.uriel.vizai

import Face
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import frankel.uriel.vizai.camera.CameraHelper
import frankel.uriel.vizai.camera.PICK_IMAGE
import frankel.uriel.vizai.camera.PhotoPathListener
import frankel.uriel.vizai.camera.REQUEST_TAKE_PHOTO
import frankel.uriel.vizai.model.network.ApiService
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*



class MainActivity : AppCompatActivity() {

    private lateinit var cameraHelper: CameraHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraHelper = CameraHelper(this, object : PhotoPathListener {
            override fun onPhotoReady(path: String) {
                val file = File(path)
                uploadFile(file)
            }
        })

        capture.setOnClickListener {
            cameraHelper.takePicture()
        }

        open.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }


    }

    private fun uploadFile(file: File) {
        Picasso.get().load(file).into(image)

        val inputStream = FileInputStream(file)
        val buf = ByteArray(inputStream.available())
        while (inputStream.read(buf) !== -1);
        val requestBodyByte = RequestBody
            .create("application/octet-stream".toMediaType(), buf)
        val content_disposition =
            "attachment; filename=\"" + file.path + "\""
        ApiService.instance.service.detectFaces(content_disposition, requestBodyByte)
            ?.enqueue(object : Callback<Array<Face>?> {
                override fun onFailure(call: Call<Array<Face>?>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<Array<Face>?>,
                    response: Response<Array<Face>?>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@MainActivity,
                            "happy " + response.body()?.get(0)?.faceAttributes?.emotion,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }


            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO) {
            cameraHelper.onActivityResult(requestCode, resultCode, data)
        } else {
            val image = downSizeImage(data?.data)

            uploadFile(image)
        }
    }

    private fun downSizeImage(imageUri: Uri?): File {
        val scaleDivider = 4


        // 1. Convert uri to bitmap

        val fullBitmap =
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

        // 2. Get the downsized image content as a byte[]
        val scaleWidth = fullBitmap.width / scaleDivider
        val scaleHeight = fullBitmap.height / scaleDivider
        val downsizedImageBytes =
            getDownsizedImageBytes(fullBitmap, scaleWidth, scaleHeight)

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            "urururur", /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )
        val bos = BufferedOutputStream(FileOutputStream(image))
        bos.write(downsizedImageBytes)
        bos.flush()
        bos.close()
        return image
    }

    fun getDownsizedImageBytes(
        fullBitmap: Bitmap?, scaleWidth: Int, scaleHeight: Int
    ): ByteArray? {
        val scaledBitmap =
            Bitmap.createScaledBitmap(fullBitmap!!, scaleWidth, scaleHeight, true)
        val baos = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        cameraHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
