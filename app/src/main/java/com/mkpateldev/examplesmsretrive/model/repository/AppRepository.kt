package com.sk.user.agent.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mkpateldev.examplesmsretrive.MyApplication
import com.mkpateldev.examplesmsretrive.model.dto.OtpRequest
import com.mkpateldev.examplesmsretrive.model.dto.OtpVerifyRequest
import okhttp3.MultipartBody
import retrofit2.http.Query

class AppRepository(
    applicationContext: Context
) {
    var apiService = (applicationContext as MyApplication).apiService


    suspend fun getToken(grantType: String?, username: String, password: String?) =
        apiService!!.getToken(grantType, username, password)

    suspend fun getOtp(otpRequest: OtpRequest) = apiService!!.getOtp(otpRequest)

    suspend fun doOtpVerify(otpVerifyRequest: OtpVerifyRequest) = apiService!!.verifyOtp(
        otpVerifyRequest.mobile,
        otpVerifyRequest.isOTPverified,
        otpVerifyRequest.fcmid,
        otpVerifyRequest.currentAPKversion,
        otpVerifyRequest.phoneOSversion,
        otpVerifyRequest.userDeviceName,
        otpVerifyRequest.deviceId
    )


}