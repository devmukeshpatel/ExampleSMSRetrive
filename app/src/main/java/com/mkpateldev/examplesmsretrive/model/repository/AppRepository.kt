package com.mkpateldev.examplesmsretrive.model.repository

import android.content.Context

import com.mkpateldev.examplesmsretrive.MyApplication
import com.mkpateldev.examplesmsretrive.model.dto.OtpRequest
import com.mkpateldev.examplesmsretrive.model.dto.OtpVerifyRequest


class AppRepository(
    applicationContext: Context
) {
    private var apiService = (applicationContext as MyApplication).apiService


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