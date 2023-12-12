package com.mkpateldev.examplesmsretrive.model.dto

import com.google.gson.annotations.SerializedName

 class TokenResponse {

    @SerializedName(".expires")
    var expires: String? = null

    @SerializedName(".issued")
    var issued: String? = null

    @SerializedName("AppName")
    var appName: String? = null

    @SerializedName("userName")
    var userName: String? = null

    @SerializedName("as:client_id")
    var client_id: String? = null

    @SerializedName("expires_in")
    var expires_in = 0

    @SerializedName("token_type")
    var token_type: String? = null

    @SerializedName("access_token")
    var access_token: String? = null
}