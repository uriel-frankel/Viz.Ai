package frankel.uriel.vizai.viewmodel

import Face
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

    val emotion: MutableLiveData<Resource<Int>> by lazy {
        MutableLiveData<Resource<Int>>()
    }

    fun sendImageToServer(file: File) {
        emotion.postValue(Resource.Loading())

        val requestBody = file.asRequestBody("application/octet-stream".toMediaType())
        val contentDisposition =  "attachment; filename=\"" + file.path + "\""
        ApiService.instance.service.detectFaces(contentDisposition, requestBody)
            ?.enqueue(object : Callback<Array<Face>?> {
                override fun onFailure(call: Call<Array<Face>?>, t: Throwable) {

                    emotion.postValue(Resource.Failure())
                }

                override fun onResponse(
                    call: Call<Array<Face>?>,
                    response: Response<Array<Face>?>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()?.get(0)?.faceAttributes?.emotion?.getEmotion()
                        data?.apply {
                            emotion.postValue(
                                Resource.Success(
                                    this
                                )
                            )

                        } ?: emotion.postValue(Resource.Failure())


                    }
                }


            })
    }


}