package com.mkpateldev.examplesmsretrive.comonUtils

import android.content.Context
import android.content.SharedPreferences

class SharePrefs(appContext: Context) {
    private val sharedPreferences: SharedPreferences = appContext.getSharedPreferences(
        SHARED_PREFERENCE,
        0
    )

    fun putString(key: String?, `val`: String?) {
        sharedPreferences.edit().putString(key, `val`).apply()
    }

    fun getString(key: String): String {
        return sharedPreferences.getString(key, "")!!
    }

    fun putInt(key: String?, `val`: Int?) {
        sharedPreferences.edit().putInt(key, `val`!!).apply()
    }

    fun putLong(key: String?, `val`: Long?) {
        sharedPreferences.edit().putLong(key, `val`!!).apply()
    }

    fun putBoolean(key: String?, `val`: Boolean?) {
        sharedPreferences.edit().putBoolean(key, `val`!!).apply()
    }

    fun getBoolean(key: String?): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun getInt(key: String?): Int {
        return sharedPreferences.getInt(key, 0)
    }

    fun getLong(key: String?): Long {
        return sharedPreferences.getLong(key, 0)
    }


    companion object {
        @JvmField
        var TOKEN = "token"
        var TOKEN_NAME = "TokenName"
        var TOKEN_PASSWORD = "Token_password"
        private var instance: SharePrefs? = null
        var SHARED_PREFERENCE = "Example"

        @JvmStatic
        fun getInstance(ctx: Context): SharePrefs {
            if (instance == null) {
                instance = SharePrefs(ctx)
            }
            return instance!!
        }

    }
}