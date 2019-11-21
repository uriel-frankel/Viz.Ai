package frankel.uriel.vizai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import frankel.uriel.vizai.utils.Resource
import frankel.uriel.vizai.viewmodel.FacesViewModel
import kotlinx.android.synthetic.main.activity_main.*

const val PROFILE_IMAGE_REQ_CODE = 101

class MainActivity : FragmentActivity() {


    private lateinit var viewModel: FacesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        capture.setOnClickListener {
            ImagePicker.with(this)
                .compress(400)
                .start(PROFILE_IMAGE_REQ_CODE)
        }


        viewModel = ViewModelProviders.of(this).get(FacesViewModel::class.java)

        viewModel.emotion.observe(this, Observer {
            when(it){
                is Resource.Loading -> {
                    emotion.text = null
                    progressBar.visibility = View.VISIBLE
                    capture.isEnabled = false
                }
                is Resource.Success -> {
                    progressBar.visibility = View.GONE
                    emotion.text = getString(it.data)
                    capture.isEnabled = true
                }
                is Resource.Failure -> {
                    progressBar.visibility = View.GONE
                    emotion.text = getString(R.string.error)
                    capture.isEnabled = true

                }
            }

        })

        viewModel.croppedBitmap.observe(this, Observer {
            image.setImageBitmap(it)

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val file = ImagePicker.getFile(data)
            file?.apply {
                when (requestCode) {
                    PROFILE_IMAGE_REQ_CODE -> {
                        Picasso.get().load(this).into(image)
                        viewModel.sendImageToServer(this)
                    }
                }

            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }
    }

}
