package com.ahmed.jazirahwifiapp.ui.admin.panel.wifis

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ahmed.jazirahwifiapp.WifiApplication
import com.ahmed.jazirahwifiapp.repository.WifiRepo

class WifiViewModel(val app: Application) : AndroidViewModel(app) {
    private val wifiRepo = WifiRepo(app)

    fun getWifiResult() = wifiRepo.getWifiCollections()
    fun queryWifi() = wifiRepo.queryRealTimeData()

    fun getWifiDeletedStatus() = wifiRepo.getDeletedWifiItemTrace()
    fun setWifiToDelete(documentId: String,wifiName:String) = wifiRepo.deleteWifiFromFirebase(documentId,wifiName)

    fun isNetworkConnected(): Boolean {
        return (app as WifiApplication).hasInternetConnection()
    }

    fun getUpdatedWifiAccessCodeStats() = wifiRepo.getWifiAccessCodeUpdateStatus()
    fun updateWifiAccessCode(map: Map<Int, Any>, wifiAccessCode:Int) =
        wifiRepo.updateWifiItemMultiple(map,wifiAccessCode)
}