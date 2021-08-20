package com.ahmed.jazirahwifiapp.utils

import java.util.regex.Pattern

class Validations {
    fun validateString(str: String): Boolean {
        return str.trim().isNotEmpty()
    }

    fun validateEmail(email: String): Boolean {
        if (email.trim().isNotEmpty()) {
            return true
        }
        return false
    }

    fun validateTwoStrings(str1: String, str2: String): Boolean {
        if (str1.isNotEmpty() && str2.trim().isNotEmpty()) {
            if (str1.trim() == str2.trim())
                return true
        }
        return false
    }
    fun isEmailValid(email: String): Boolean {
        val EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches()

    }

}