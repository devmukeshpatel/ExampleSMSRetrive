package com.mkpateldev.examplesmsretrive.comonUtils

import android.content.Context
import android.provider.Settings


class Utils {

    companion object {

        fun isNullOrEmpty(s: String?): Boolean {
            return s.isNullOrEmpty() || s == "null" || s == "0"
        }

        fun getDeviceUniqueID(context: Context): String? {
            return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }
    }

}