package frankel.uriel.vizai.model.network


import Face
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface AzureService {

    @POST("detect/?returnFaceId=true&returnFaceLandmarks=false&returnFaceAttributes=emotion&recognitionModel=recognition_01&returnRecognitionModel=false&detectionModel=detection_01")
    fun detectFaces(@Header("Content-Disposition") contentDisposition: String, @Body photo: RequestBody): Call<Array<Face>?>?
}