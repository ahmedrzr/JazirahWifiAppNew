package com.ahmed.jazirahwifiapp.ui.main.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ahmed.jazirahwifiapp.WifiApplication
import com.ahmed.jazirahwifiapp.repository.WifiRepo


class HomeViewModel(val app: Application) : AndroidViewModel(app) {
    private val wifiRepo = WifiRepo(app)
    private val itemToDelete: MutableLiveData<Map<Any, Any>> = MutableLiveData()

    fun subscribeToWifiCollection(appWifiAccessCode: Int) =
        wifiRepo.subscribeToRealTimeData(appWifiAccessCode)

    fun getWifiCollections() = wifiRepo.getWifiCollections()

    fun deleteWifiItemFirestore(documentId: String,wifiName:String) = wifiRepo.deleteWifiFromFirebase(documentId,wifiName)
    fun getDeletedItemTrace() = wifiRepo.getDeletedWifiItemTrace()


    fun setItemToDeleteFromList(map: Map<Any, Any>) = itemToDelete.postValue(map)
    fun getItemToDeleteFromList(): LiveData<Map<Any, Any>> = itemToDelete

    fun isNetworkConnected(): Boolean {
        return (app as WifiApplication).hasInternetConnection()
    }

}