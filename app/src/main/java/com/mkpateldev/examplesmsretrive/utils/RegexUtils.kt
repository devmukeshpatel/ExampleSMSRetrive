package com.mkpateldev.examplesmsretrive.utils

import java.util.regex.Pattern

object RegexUtils {
    private const val MOBILE_NO_PATTERN = "^[5-9][0-9]{9}$"

    private val EMAIL_ADDRESS: Pattern = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    )
    fun isValidEmail(email: String): Boolean {
        return EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidMobileNo(mobile: String?): Boolean {
        val pattern = Pattern.compile(MOBILE_NO_PATTERN)
        val matcher = pattern.matcher(mobile)
        return matcher.matches()
    }
}
