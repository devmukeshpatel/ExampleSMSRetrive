package com.mkpateldev.examplesmsretrive.view

import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender.SendIntentException
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.mkpateldev.examplesmsretrive.BuildConfig
import com.mkpateldev.examplesmsretrive.R
import com.mkpateldev.examplesmsretrive.databinding.ActivityMainBinding
import com.mkpateldev.examplesmsretrive.model.Response.Response
import com.mkpateldev.examplesmsretrive.model.dto.PeopleModel
import com.mkpateldev.examplesmsretrive.model.dto.TokenResponse
import com.mkpateldev.examplesmsretrive.comonUtils.SharePrefs
import com.mkpateldev.examplesmsretrive.comonUtils.Utils
import com.mkpateldev.examplesmsretrive.comonUtils.ViewUtils
import com.mkpateldev.examplesmsretrive.comonUtils.ViewUtils.Companion.snackBar
import com.mkpateldev.examplesmsretrive.comonUtils.observe
import com.mkpateldev.examplesmsretrive.model.dto.OtpVerifyRequest
import com.mkpateldev.examplesmsretrive.viewModel.AuthViewModel
import com.mkpateldev.examplesmsretrive.model.repository.AppRepository
import com.mkpateldev.examplesmsretrive.viewModel.AuthViewModelFactory


class MainActivity : AppCompatActivity() {
    private val CREDENTIAL_PICKER_REQUEST = 1
    private lateinit var binding: ActivityMainBinding
    private val RESOLVE_HINT = 1
    private val SMS_CONSENT_REQUEST = 2
    private var tv: TextView? = null
    private lateinit var viewModel: AuthViewModel
    private var mobile: String = ""
    private var cTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(
            this, AuthViewModelFactory(
                application,
                AppRepository(applicationContext)
            )
        )[AuthViewModel::class.java]

        observe(viewModel.otpVerifyData, ::handleOtpVerifyResult)
        observe(viewModel.tokenData, ::handleResultToken)

         init()

         try {
             requestHint()
         } catch (e: SendIntentException) {
             e.printStackTrace()
         }


         val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(smsVerificationReceiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(smsVerificationReceiver, intentFilter)
        }
        val task: Task<Void> = SmsRetriever.getClient(this).startSmsUserConsent(null)
         task.addOnSuccessListener(object : OnSuccessListener<Void?> {
             override fun onSuccess(aVoid: Void?) {
                 Log.d("A", "Task Running")
             }
         })
         Log.d("A", "Value:$task")
    }

    fun verifyOtp(v: View) {
        var otp = binding.etOtp.text.toString().trim()
        if (Utils.isNullOrEmpty(otp)) {
            binding.root.snackBar("Mobile Number")
        } else {
            viewModel.otpVerify(
                OtpVerifyRequest(
                    mobile,
                    true,
                    "",
                    BuildConfig.VERSION_NAME,
                    Build.VERSION.RELEASE,
                    Build.MODEL,
                    Utils.getDeviceUniqueID(this)!!
                )
            )
        }
    }

    fun changeNumber(v:View) {
        isOtpVerify(false)
        if (cTimer != null) {
            cTimer?.cancel()
        }
    }

    private fun handleResultToken(it: Response<TokenResponse>) {
        when (it) {
            is Response.Loading -> {}
            is Response.Success -> {
                it.data?.let {
                    SharePrefs.getInstance(this@MainActivity)
                        .putString(SharePrefs.TOKEN, it.access_token.toString())
                }
            }

            is Response.Error -> {
                binding.root.snackBar(it.errorMesssage.toString())
            }
        }
    }

    fun resendOtp(v: View) {

    }

    private fun init() {
        tv = findViewById(R.id.etOtp)
        isOtpVerify(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (cTimer != null) {
            cTimer?.cancel()
        }
    }

    private fun handleOtpVerifyResult(it: Response<PeopleModel>) {
        when (it) {
            is Response.Loading -> {
                ViewUtils.showProgressDialog(this)
            }

            is Response.Success -> {
                it.data?.let {
                    ViewUtils.hideProgressDialog()
                    viewModel.callToken("password", "username", "password")
                }
            }

            is Response.Error -> {
                ViewUtils.hideProgressDialog();
                binding.root.snackBar(it.errorMesssage.toString())
            }
        }
    }

    private fun isOtpVerify(isOtpVerify: Boolean) {
        if (isOtpVerify) {
            binding.llOtpVerify.visibility = View.VISIBLE
            binding.tvMobileNumber.text = mobile
            binding.tvResendOtp.visibility = View.INVISIBLE
            startTimer(binding.tvOtpTimer, binding.tvResendOtp)
        }
    }

    private fun startTimer(tvResendOtpTimer: TextView, tvResendOtp: TextView) {
        cTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvResendOtpTimer.text = "Resend" + millisUntilFinished / 1000
            }

            override fun onFinish() {
                tvResendOtp.isEnabled = true
                tvResendOtp.visibility = View.VISIBLE
            }
        }.start()
    }


    // Construct a request for phone numbers and show the picker
    @Throws(SendIntentException::class)
    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val intent: PendingIntent = Credentials.getClient(this).getHintPickerIntent(hintRequest)
        startIntentSenderForResult(
            intent.intentSender,
            RESOLVE_HINT, null, 0, 0, 0
        )
    }

    private val smsVerificationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val smsRetrieverStatus: Status? = extras!![SmsRetriever.EXTRA_STATUS] as Status?
                if (smsRetrieverStatus != null) {
                    when (smsRetrieverStatus.statusCode) {
                        CommonStatusCodes.SUCCESS -> {
                            // Get consent intent
                            val consentIntent =
                                extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                            //                        Intent page = new Intent(getApplicationContext(),Main2Activity.class);
                            try {
                                // Start activity to show consent dialog to user, activity must be started in
                                // 5 minutes, otherwise you'll receive another TIMEOUT intent
                                startActivityForResult(consentIntent!!, SMS_CONSENT_REQUEST)
                            } catch (e: ActivityNotFoundException) {
                                // Handle the exception ...
                                e.printStackTrace()
                            }
                        }

                        CommonStatusCodes.TIMEOUT ->                         // Time out occurred, handle the error.
                            Toast.makeText(context, "TIME_OUT", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CREDENTIAL_PICKER_REQUEST ->                 // Obtain the phone number from the result
                if (resultCode == RESULT_OK) {
                    val credential: Credential = data?.getParcelableExtra(Credential.EXTRA_KEY)!!
                    credential.id
                    // credential.getId();  <-- will need to process phone number string
                }

            SMS_CONSENT_REQUEST -> if (resultCode == RESULT_OK) {

                // Get SMS message content
                val message = data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                Log.d("A", "message:->$message")
                tv!!.text = message
                // Extract one-time code from the message and complete verification
                // `sms` contains the entire text of the SMS message, so you will need
                // to parse the string.
//                  String oneTimeCode = parseOneTimeCode(message); // define this function

                // send one time code to the server
            } else if (resultCode == RESULT_CANCELED) {
                // Consent canceled, handle the error ...
                val task = SmsRetriever.getClient(this).startSmsUserConsent(null)
                Log.d("A", "cancelled")
            }
        }
    }
}