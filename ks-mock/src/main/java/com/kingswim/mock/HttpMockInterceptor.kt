package com.kingswim.mock


import kotlin.Throws
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

class HttpMockInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        //获取当前URL
        val url = request.url.toString()
        //根据当前URL获取Mock数据
        val responseMockData = MockHelper.getMockData(url)
        return if (responseMockData.isNotEmpty()) {
            Response.Builder()
                .code(200)
                .message("mock data")
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(responseMockData.toByteArray().toResponseBody("application/json".toMediaType()))
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .addHeader("Mock", "true")
                .build()
        } else {
            chain.proceed(chain.request())
        }
    }
}