package com.ahmed.jazirahwifiapp.utils

import androidx.lifecycle.MutableLiveData

class DataMsgHandler {
    private val msgTriggerNotifications: MutableLiveData<Map<String, Any>> = MutableLiveData()
    private val liveDataHandler: MutableLiveData<Any> = MutableLiveData()


    fun setUpdateUiNotification(isErrorMessage: Boolean, message: String) {
        val messageHandler = mutableMapOf<String, Any>()
        messageHandler["isError"] = isErrorMessage
        messageHandler["message"] = message
        msgTriggerNotifications.postValue(messageHandler)
    }

    fun getUpdateUiNotifications() =
        msgTriggerNotifications

    fun setLiveDataHandler(any: Any) {
        liveDataHandler.postValue(any)
    }

    fun getLiveDataHandler() = liveDataHandler
     fun prepareMsg(isErrorTrigger: Boolean, message: String): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        map["isError"] = isErrorTrigger
        map["message"] = message
        return map
    }
}