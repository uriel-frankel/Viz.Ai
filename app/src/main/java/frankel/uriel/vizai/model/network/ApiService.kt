package frankel.uriel.vizai.model.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiService {
    var service: AzureService

    companion object {
        val instance = ApiService()
    }

    constructor(){
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().
        addInterceptor(AuthInterceptor()).addInterceptor(loggingInterceptor).build()



        val retrofit: Retrofit = Retrofit.Builder().client(client)
            .baseUrl("https://northeurope.api.cognitive.microsoft.com/face/v1.0/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        service = retrofit.create(
            AzureService::class.java)
    }
}