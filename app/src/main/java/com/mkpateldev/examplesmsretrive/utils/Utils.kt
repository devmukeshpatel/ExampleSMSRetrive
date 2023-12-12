package com.mkpateldev.examplesmsretrive.utils

import android.content.Context
import android.provider.Settings


class Utils {

    companion object {

        fun isNullOrEmpty(s: String?): Boolean {
            return s == null || s.length == 0 || s.equals(
                "null"
            ) || s.equals("0")
        }

        fun getDeviceUniqueID(context: Context): String? {
            return Settings.Secure.getString(context!!.contentResolver, Settings.Secure.ANDROID_ID)
        }
    }

}