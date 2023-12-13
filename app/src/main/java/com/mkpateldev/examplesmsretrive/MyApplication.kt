package com.mkpateldev.examplesmsretrive

import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.multidex.MultiDex
import com.google.gson.JsonObject
import com.mkpateldev.examplesmsretrive.model.api.APIService
import com.mkpateldev.examplesmsretrive.model.api.RetrofitHelper.getInstance
import com.mkpateldev.examplesmsretrive.comonUtils.LocaleHelper
import com.mkpateldev.examplesmsretrive.comonUtils.SharePrefs
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyApplication : Application(), LifecycleObserver {
    var apiService: APIService? = null

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"))
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        apiService = getInstance(applicationContext)
        instance = this
    }

    fun CallToken() {
        val call = apiService!!.getExpireToken(
            "password", SharePrefs.getInstance(
                applicationContext
            )
                .getString(SharePrefs.TOKEN_NAME), SharePrefs.getInstance(applicationContext)
                .getString(SharePrefs.TOKEN_PASSWORD)
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                try {
                    val `object` = JSONObject(response.body().toString())
                    SharePrefs.getInstance(applicationContext)
                        .putString(SharePrefs.TOKEN, `object`.getString("access_token"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(applicationContext, "An error has occured", Toast.LENGTH_LONG).show()
            }
        })
    }

    companion object {
        @get:Synchronized
        var instance: MyApplication? = null
            private set
    }
}