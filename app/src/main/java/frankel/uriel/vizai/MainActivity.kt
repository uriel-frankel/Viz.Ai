package frankel.uriel.vizai

import Face
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import frankel.uriel.vizai.model.network.ApiService
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream

const val PROFILE_IMAGE_REQ_CODE = 101

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        capture.setOnClickListener {
            ImagePicker.with(this)
                .compress(400)			//Final image size will be less than 1 MB(Optional)
                .start(PROFILE_IMAGE_REQ_CODE)
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
                    showError()
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<Array<Face>?>,
                    response: Response<Array<Face>?>
                ) {
                    if (response.isSuccessful) {
                       runOnUiThread {
                           emotion.text = getString(response.body()?.get(0)?.faceAttributes?.emotion?.getEmotion() ?: R.string.error)
                       }

                    }
                }


            })
    }

    private fun showError() {
        Toast.makeText(
            this@MainActivity,
            "Error",
            Toast.LENGTH_LONG
        ).show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            // File object will not be null for RESULT_OK
            val file = ImagePicker.getFile(data)
            file?.apply {
                Log.e("TAG", "Path:${absolutePath}")
                when (requestCode) {
                    PROFILE_IMAGE_REQ_CODE -> {
                        Picasso.get().load(path).into(image)
                        uploadFile(this)
                    }
                }

            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }



}
