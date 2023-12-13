package com.mkpateldev.examplesmsretrive.comonUtils

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.mkpateldev.examplesmsretrive.R

class ViewUtils {

    companion object {
        private var customDialog: Dialog? = null
        fun View.snackBar(message: String) {
            Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
                snackbar.setAction("Ok") {
                    snackbar.dismiss()
                }
            }.show()
        }
        fun hideProgressDialog() {
            if (customDialog != null) {
                customDialog!!.dismiss()
            }
        }
        fun showProgressDialog(activity: Activity) {
            try {
                if (customDialog != null) {
                    customDialog?.dismiss()
                    customDialog = null
                }
                customDialog = Dialog(activity, R.style.CustomDialog)
                val mView: View = LayoutInflater.from(activity).inflate(R.layout.progress_dialog, null)
                customDialog!!.setContentView(mView)
                customDialog!!.setCancelable(false)

                if (!activity.isFinishing && !customDialog!!.isShowing) {
                    customDialog!!.show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}