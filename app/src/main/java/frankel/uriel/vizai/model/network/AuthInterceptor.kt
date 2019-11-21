package frankel.uriel.vizai.model.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class AuthInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        request = request.newBuilder()
            .addHeader("Ocp-Apim-Subscription-Key", "bb86a397ddd74552905e61ed92149739")
            .build()
        return chain.proceed(request)
    }
}