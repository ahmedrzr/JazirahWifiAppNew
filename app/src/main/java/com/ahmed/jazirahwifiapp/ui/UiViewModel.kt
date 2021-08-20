package com.ahmed.jazirahwifiapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UiViewModel : ViewModel() {

    private val interComLiveData: MutableLiveData<Map<String, Any>> = MutableLiveData()
    fun setInterComLiveDat(map: Map<String, Any>) = interComLiveData.postValue(map)
    fun getInterComLiveData() = interComLiveData
}