package com.mkpateldev.examplesmsretrive.model.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.mkpateldev.examplesmsretrive.BuildConfig
import com.mkpateldev.examplesmsretrive.MyApplication
import com.mkpateldev.examplesmsretrive.comonUtils.Aes256
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object RetrofitHelper {
    fun getInstance(context: Context): APIService {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .readTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(6, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(Interceptor addInterceptor@{ chain: Interceptor.Chain ->
                var response: Response? = null
                try {
                    val request = chain.request()
                    response = chain.proceed(request)
                    if (response.code == 200) {
                        if (!request.url.toString().contains("/token")
                        ) {
                            try {
                                val jsonObject = JSONObject()
                                jsonObject.put("message", JSONObject(response.body!!.string()))
                                val data =
                                    jsonObject.getJSONObject("message").getString("Data")
                                val destr = Aes256.decrypt(
                                    data,
                                    SimpleDateFormat(
                                        "yyyyMMdd",
                                        Locale.ENGLISH
                                    ).format(Date()) + "1201"
                                )
                                if (BuildConfig.DEBUG) {
                                    printMsg(destr)
                                }
                                val contentType = response.body!!.contentType()
                                val responseBody = destr.toResponseBody(contentType)
                                return@addInterceptor response.newBuilder().body(responseBody)
                                    .build()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    if (response.code == 401) {
                        MyApplication.instance!!.CallToken()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return@addInterceptor response ?: Response.Builder()
                    .code(200).request(chain.request())
                    .protocol(Protocol.HTTP_1_0).message("asd")
                    .body("asd".toResponseBody()).build()
            })
            .addInterceptor(interceptor)
            .build()
        val gson = GsonBuilder()
            .setLenient()
            .create()
        return Retrofit.Builder()
            .client(client)
            .baseUrl(
                if (BuildConfig.DEBUG) BuildConfig.apiEndpoint else "YOUR_END_POINT"
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)
    }

    private fun printMsg(msg: String) {
        val chunkCount = msg.length / 4050 // integer division
        for (i in 0..chunkCount) {
            val max = 4050 * (i + 1)
            if (max >= msg.length) {
                println(msg.substring(4050 * i))
            } else {
                println(msg.substring(4050 * i, max))
            }
        }
    }
}