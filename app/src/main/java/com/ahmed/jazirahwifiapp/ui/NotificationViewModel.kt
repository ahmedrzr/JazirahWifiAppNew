package com.ahmed.jazirahwifiapp.ui

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController

class NotificationViewModel(val app: Application) : AndroidViewModel(app) {
    private val notify: MutableLiveData<Map<String, Any>> = MutableLiveData()

    fun setNotify(map: Map<String, Any>) = notify.postValue(map)
    fun getNotify() = notify

    fun showNotification(
        navController: NavController,
        destinationId: Int,
        map: Map<String, Any>
    ) {
        val isError = map["isError"] as Boolean
        val isInfo = map["isInfo"] as Boolean
        val isSuccess = map["isSuccess"] as Boolean
        val message = map["message"] as String
        val bundle = Bundle().apply {
            this.putBoolean("isError", isError)
            this.putBoolean("isInfo", isInfo)
            this.putBoolean("isSuccess", isSuccess)
            this.putString("message", message)
        }
        navController.navigate(destinationId, bundle)
    }

    fun prepareMessage(
        isErrorTriggered: Boolean,
        isInfoTriggered: Boolean,
        isSuccessTriggered: Boolean,
        message: String
    ) {
        val map = mutableMapOf<String, Any>()
        map["isError"] = isErrorTriggered
        map["isInfo"] = isInfoTriggered
        map["isSuccess"] = isSuccessTriggered
        map["message"] = message
        notify.postValue(map)
    }
}