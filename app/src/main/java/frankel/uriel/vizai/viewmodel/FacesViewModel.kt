package frankel.uriel.vizai.viewmodel

import Face
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import frankel.uriel.vizai.model.network.ApiService
import frankel.uriel.vizai.utils.Resource
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class FacesViewModel : ViewModel() {

    private var imageFile: File? = null
    val emotion: MutableLiveData<Resource<Int>> by lazy {
        MutableLiveData<Resource<Int>>()
    }
    val croppedBitmap: MutableLiveData<Bitmap> by lazy {
        MutableLiveData<Bitmap>()
    }

    fun sendImageToServer(file: File) {
        this.imageFile = file
        emotion.postValue(Resource.Loading())

        val requestBody = file.asRequestBody("application/octet-stream".toMediaType())
        val contentDisposition = "attachment; filename=\"" + file.path + "\""
        ApiService.instance.service.detectFaces(contentDisposition, requestBody)
            ?.enqueue(object : Callback<Array<Face>?> {
                override fun onFailure(call: Call<Array<Face>?>, t: Throwable) {

                    emotion.postValue(Resource.Failure())
                }

                override fun onResponse(
                    call: Call<Array<Face>?>,
                    response: Response<Array<Face>?>
                ) {
                    if (response.isSuccessful && response.body()?.size ?: 0 > 0) {
                        val face = response.body()?.get(0)
                        val data = face?.faceAttributes?.emotion?.getEmotion()
                        data?.apply {
                            emotion.postValue(
                                Resource.Success(
                                    this
                                )
                            )

                        } ?: emotion.postValue(Resource.Failure())


                        val bitmap = BitmapFactory.decodeFile(imageFile?.path)

                        face?.faceRectangle?.apply {

                            croppedBitmap.postValue(
                                Bitmap.createBitmap(
                                    bitmap,
                                    left,
                                    top,
                                    width,
                                    height
                                )
                            )

                        }

                    } else {
                        emotion.postValue(Resource.Failure())
                    }
                }


            })
    }


}