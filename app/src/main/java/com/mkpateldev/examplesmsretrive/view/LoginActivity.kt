package com.mkpateldev.examplesmsretrive.view

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.mkpateldev.examplesmsretrive.BuildConfig
import com.mkpateldev.examplesmsretrive.MyApplication
import com.mkpateldev.examplesmsretrive.R
import com.mkpateldev.examplesmsretrive.databinding.ActivityLoginBinding
import com.mkpateldev.examplesmsretrive.model.dto.OtpRequest
import com.mkpateldev.examplesmsretrive.model.dto.OtpVerifyRequest
import com.mkpateldev.examplesmsretrive.model.dto.PeopleModel
import com.mkpateldev.examplesmsretrive.model.dto.TokenResponse
import com.mkpateldev.examplesmsretrive.utils.SharePrefs
import com.mkpateldev.examplesmsretrive.utils.Utils
import com.mkpateldev.examplesmsretrive.utils.observe
import com.mkpateldev.examplesmsretrive.viewModel.AuthViewModel
import com.sk.user.agent.data.repository.AppRepository

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel
    private var fcmToken: String = ""
    private var mobile: String = ""
    private var cTimer: CountDownTimer? = null
    private var responseOTP: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this, AuthViewModelFactory(application,
            AppRepository(applicationContext)
        )).get(
            AuthViewModel::class.java
        )
        observe(viewModel.otpData, ::handleResult)
        observe(viewModel.otpVerifyData, ::handleOtpVerifyResult)
        observe(viewModel.tokenData, ::handleResultToken)
        init()
    }

    fun init() {
        isOtpVerify(false)

    }

    fun getOtpClick(view: View) {
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

    fun changeNumber(view: View) {
        isOtpVerify(false)
        if (cTimer != null) {
            cTimer?.cancel()
        }
    }

    fun resendOtp(view: View) {
        fetchOtp()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (cTimer != null) {
            cTimer?.cancel()
        }
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
                        isOtpVerify(true)
                    }
                }
            }
            is Response.Error -> {
                hideProgressDialog()
                binding.root.snackbar(it.errorMesssage.toString())
            }
        }
    }

    private fun handleOtpVerifyResult(it: Response<PeopleModel>) {
        when (it) {
            is Response.Loading -> {
                showProgressDialog(this)
            }
            is Response.Success -> {
                it.data?.let {
                    // hideProgressDialog()
                    if (it.active) {
                        SharePrefs.getInstance(applicationContext)
                            .putBoolean(SharePrefs.IS_LOGIN, true)
                        SaveLocalInfo.savePeopleInfo(applicationContext,it,false)
                        val regApk: UserAuth = it.registeredApk!!
                        if (regApk != null) {
                            val username: String = regApk.userName!!
                            val password: String = regApk.password!!
                            SharePrefs.getInstance(applicationContext).putString(SharePrefs.TOKEN_NAME, username)
                            SharePrefs.getInstance(applicationContext).putString(SharePrefs.TOKEN_PASSWORD, password)
                            viewModel.callToken("password", username, password)
                        }
                    } else {
                        startActivity(Intent(this, ContactUsActivity::class.java))
                        Utils.leftTransaction(this)
                    }

                }
            }
            is Response.Error -> {
                hideProgressDialog();
                binding.root.snackbar(it.errorMesssage.toString())
            }
        }
    }

    private fun handleResultToken(it: Response<TokenResponse>) {
        when (it) {
            is Response.Loading -> {
                //  showProgressDialog(this)
            }
            is Response.Success -> {
                it.data?.let {
                    hideProgressDialog()
                    SharePrefs.getInstance(this@LoginWithOtpActivity)
                        .putString(SharePrefs.TOKEN, it.access_token.toString())
                    MyApplication.instance?.mixPanelAnalytics = MixPanelAnalytics(applicationContext)
                    MyApplication.instance?.mixPanelAnalytics!!.startAnalyticSession()
                    Intent(this, SplashActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                    Utils.leftTransaction(this)
                }
            }
            is Response.Error -> {
                hideProgressDialog();
                binding.root.snackbar(it.errorMesssage.toString())
            }
        }
    }

    private fun isOtpVerify(isOtpVerify: Boolean) {
        if (isOtpVerify) {
            binding.llLoginWithOtp.visibility = View.GONE
            binding.llOtpVerify.visibility = View.VISIBLE
            binding.tvMobileNumber.setText(mobile)
            binding.tvResendOtp.setVisibility(View.INVISIBLE)
            startTimer(binding.tvOtpTimer, binding.tvResendOtp)
        } else {
            binding.llLoginWithOtp.visibility = View.VISIBLE
            binding.llOtpVerify.visibility = View.GONE
        }
    }

    fun startTimer(tvResendOtpTimer: TextView, tvResendOtp: TextView) {
        cTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvResendOtpTimer.text =
                    getString(R.string.resend_otp_colun) + millisUntilFinished / 1000
            }

            override fun onFinish() {
                tvResendOtp.isEnabled = true
                tvResendOtp.visibility = View.VISIBLE
            }
        }.start()
    }
}