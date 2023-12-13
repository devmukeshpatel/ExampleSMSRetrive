package com.mkpateldev.examplesmsretrive.view

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.mkpateldev.examplesmsretrive.BuildConfig
import com.mkpateldev.examplesmsretrive.R
import com.mkpateldev.examplesmsretrive.databinding.ActivityLoginBinding
import com.mkpateldev.examplesmsretrive.model.Response.Response
import com.mkpateldev.examplesmsretrive.model.dto.OtpRequest
import com.mkpateldev.examplesmsretrive.model.dto.PeopleModel
import com.mkpateldev.examplesmsretrive.comonUtils.Utils
import com.mkpateldev.examplesmsretrive.comonUtils.ViewUtils.Companion.hideProgressDialog
import com.mkpateldev.examplesmsretrive.comonUtils.ViewUtils.Companion.showProgressDialog
import com.mkpateldev.examplesmsretrive.comonUtils.ViewUtils.Companion.snackBar
import com.mkpateldev.examplesmsretrive.comonUtils.observe
import com.mkpateldev.examplesmsretrive.viewModel.AuthViewModel
import com.mkpateldev.examplesmsretrive.model.repository.AppRepository
import com.mkpateldev.examplesmsretrive.viewModel.AuthViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel
    private var fcmToken: String = ""
    private var mobile: String = ""
    private var responseOTP: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(application, AppRepository(applicationContext))
        )[AuthViewModel::class.java]

        observe(viewModel.otpData, ::handleResult)
    }

    fun getOtpClick(v: View) {
        fetchOtp()
    }

    private fun fetchOtp() {
        mobile = binding.etMobileNumber.text.toString().trim()
        viewModel.fetchOtp(
            OtpRequest(
                mobile,
                fcmToken,
                BuildConfig.VERSION_NAME,
                Build.VERSION.RELEASE,
                Build.MODEL,
                Utils.getDeviceUniqueID(this)!!
            )
        )

    }

    private fun handleResult(it: Response<PeopleModel>) {
        when (it) {
            is Response.Loading -> {
                showProgressDialog(this)
            }

            is Response.Success -> {
                it.data?.let {
                    hideProgressDialog()
                    responseOTP = it.oTP
                    if (!Utils.isNullOrEmpty(responseOTP)) {
                        Log.e("mobileOTPViewModel", ">>>$responseOTP")
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    }
                }
            }

            is Response.Error -> {
                hideProgressDialog()
                binding.root.snackBar(it.errorMesssage.toString())
            }
        }
    }

}