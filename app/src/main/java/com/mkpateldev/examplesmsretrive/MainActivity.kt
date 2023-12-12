package com.mkpateldev.examplesmsretrive

import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task


class MainActivity : AppCompatActivity() {
    private val CREDENTIAL_PICKER_REQUEST = 1
    private val RESOLVE_HINT = 1
    private val SMS_CONSENT_REQUEST = 2
    private var tv: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv = findViewById(R.id.textview2)

        try {
            requestHint()
        } catch (e: SendIntentException) {
            e.printStackTrace()
        }


        val intentFilter: IntentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsVerificationReceiver, intentFilter)
        val task: Task<Void> = SmsRetriever.getClient(this).startSmsUserConsent(null)
        task.addOnSuccessListener(object : OnSuccessListener<Void?> {
            override fun onSuccess(aVoid: Void?) {
                Log.d("A", "Task Running")
            }
        })

        Log.d("A", "Value:$task")
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