package com.mkpateldev.examplesmsretrive.model.api

import com.google.gson.JsonObject
import com.mkpateldev.examplesmsretrive.model.dto.OtpRequest
import com.mkpateldev.examplesmsretrive.model.dto.OtpVerifyResponse
import com.mkpateldev.examplesmsretrive.model.dto.TokenResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface APIService {

    @POST("api/SalesAppLogin/ViaOtp")
    suspend fun getOtp(@Body otpRequest: OtpRequest): Response<OtpVerifyResponse>


    @GET("api/SalesAppLogin/GetLogedSalesPerson")
    suspend fun verifyOtp(
        @Query("MobileNumber") mobile: String,
        @Query("IsOTPverified") isOTPverified: Boolean,
        @Query("fcmid") fcmId: String,
        @Query("CurrentAPKversion") currentAPKversion: String,
        @Query("PhoneOSversion") phoneOSversion: String,
        @Query("UserDeviceName") userDeviceName: String,
        @Query("DeviceId") deviceId: String
    ): Response<OtpVerifyResponse>

    @FormUrlEncoded
    @POST("token")
    suspend fun getToken(
        @Field("grant_type") grant_type: String?,
        @Field("username") username: String?,
        @Field("password") password: String?
    ): Response<TokenResponse>

    @FormUrlEncoded
    @POST("token")
    fun getExpireToken(
        @Field("grant_type") grant_type: String?,
        @Field("username") username: String?,
        @Field("password") password: String?
    ): Call<JsonObject>
}