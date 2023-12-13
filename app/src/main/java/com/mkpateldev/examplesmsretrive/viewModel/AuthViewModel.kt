package com.mkpateldev.examplesmsretrive.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mkpateldev.examplesmsretrive.model.Response.Response
import com.mkpateldev.examplesmsretrive.model.dto.OtpRequest
import com.mkpateldev.examplesmsretrive.model.dto.OtpVerifyRequest
import com.mkpateldev.examplesmsretrive.model.dto.PeopleModel
import com.mkpateldev.examplesmsretrive.model.dto.TokenResponse
import com.mkpateldev.examplesmsretrive.comonUtils.NetworkUtils
import com.mkpateldev.examplesmsretrive.comonUtils.RegexUtils
import com.mkpateldev.examplesmsretrive.comonUtils.Utils
import com.mkpateldev.examplesmsretrive.model.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(
    app: Application, private val repository: AppRepository
) : AndroidViewModel(app) {
    private val tokenLiveData = MutableLiveData<Response<TokenResponse>>()
    private val otpLiveData = MutableLiveData<Response<PeopleModel>>()
    private val otpVerifyLiveData = MutableLiveData<Response<PeopleModel>>()

    val tokenData: LiveData<Response<TokenResponse>> get() = tokenLiveData
    val otpData: LiveData<Response<PeopleModel>> get() = otpLiveData
    val otpVerifyData: LiveData<Response<PeopleModel>> get() = otpVerifyLiveData


    fun callToken(grantType: String?, username: String, password: String?) =
        viewModelScope.launch(Dispatchers.IO) {
            if (NetworkUtils.isInternetAvailable(getApplication())) {
                tokenLiveData.postValue(Response.Loading())
                val result = repository.getToken(grantType, username, password)
                if (result.body() != null) {
                    tokenLiveData.postValue(Response.Success(result.body()))
                } else {
                    tokenLiveData.postValue(Response.Error("Error"))
                }
            } else {
                tokenLiveData.postValue(Response.Error("internet error"))
            }
        }

    fun fetchOtp(otpRequest: OtpRequest) = viewModelScope.launch(Dispatchers.IO) {
        if (Utils.isNullOrEmpty(otpRequest.mobile)) {
            otpLiveData.postValue(Response.Error(""))
        } else if (!RegexUtils.isValidMobileNo(otpRequest.mobile)) {
            otpLiveData.postValue(Response.Error(""))
        } else {
            getOtp(otpRequest)
        }
    }

    private suspend fun getOtp(otpRequest: OtpRequest) {
        if (NetworkUtils.isInternetAvailable(getApplication())) {
            otpLiveData.postValue(Response.Loading())
            val result = repository.getOtp(otpRequest)
            if (result.body() != null) {
                if (result.body()!!.status) {
                    otpLiveData.postValue(Response.Success(result.body()!!.peopleModel))
                } else {
                    otpLiveData.postValue(Response.Error(result.body()!!.message))
                }
            } else {
                otpLiveData.postValue(Response.Error("error"))
            }
        } else {
            otpLiveData.postValue(Response.Error("error"))
        }
    }

    fun otpVerify(otpVerifyRequest: OtpVerifyRequest) = viewModelScope.launch(Dispatchers.IO) {
        getOtpVerifyData(otpVerifyRequest)
    }

    private suspend fun getOtpVerifyData(otpVerifyRequest: OtpVerifyRequest) {
        if (NetworkUtils.isInternetAvailable(getApplication())) {
            otpVerifyLiveData.postValue(Response.Loading())
            val result = repository.doOtpVerify(otpVerifyRequest)
            if (result.body() != null) {
                if (result.body()!!.status) {
                    otpVerifyLiveData.postValue(Response.Success(result.body()!!.peopleModel))
                } else {
                    otpVerifyLiveData.postValue(Response.Error(result.body()!!.message))
                }
            } else {
                otpVerifyLiveData.postValue(Response.Error("error"))
            }
        } else {
            otpVerifyLiveData.postValue(Response.Error("error"))
        }
    }

}