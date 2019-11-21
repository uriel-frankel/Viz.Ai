package frankel.uriel.vizai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import frankel.uriel.vizai.utils.Resource
import frankel.uriel.vizai.viewmodel.FacesViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

const val PROFILE_IMAGE_REQ_CODE = 101

class MainActivity : AppCompatActivity() {


    private lateinit var viewModel: FacesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        capture.setOnClickListener {
            ImagePicker.with(this)
                .compress(400)			//Final image size will be less than 1 MB(Optional)
                .start(PROFILE_IMAGE_REQ_CODE)
        }


        viewModel = ViewModelProviders.of(this).get(FacesViewModel::class.java)

        viewModel.emotion.observe(this, Observer {
            when(it){
                is Resource.Loading -> {
                    emotion.text = null
                    progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    progressBar.visibility = View.GONE
                    emotion.text = getString(it.data)
                }
                is Resource.Failure -> {
                    progressBar.visibility = View.GONE
                    emotion.text = getString(R.string.error)

                }
            }

        })

    }

    private fun uploadFile(file: File) {
        Picasso.get().load(file).into(image)
        viewModel.sendImageToServer(file)

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
