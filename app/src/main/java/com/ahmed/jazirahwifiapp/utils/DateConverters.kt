package com.ahmed.jazirahwifiapp.utils

import android.annotation.SuppressLint
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.regex.Pattern

object DateConverters {
    @SuppressLint("SimpleDateFormat")
    fun getTimeStampToLocal(timestamp: Timestamp): String? {
        return try {
            val dateFormat =
                SimpleDateFormat("E dd-MMM yyyy h:mm a")
            dateFormat.format(timestamp.toDate())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            e.message.toString()
        }
    }

    fun convertArrayToString(array: MutableList<Int>): String {
        return array.joinToString(separator = ",") { it -> "$it" }
    }

    fun convertStringArrayToString(array: MutableList<String>): String {
        return array.joinToString(separator = ",") { it -> it }
    }


}