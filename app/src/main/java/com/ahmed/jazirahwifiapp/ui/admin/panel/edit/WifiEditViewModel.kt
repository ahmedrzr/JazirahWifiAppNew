package com.ahmed.jazirahwifiapp.ui.admin.panel.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ahmed.jazirahwifiapp.repository.WifiRepo

class WifiEditViewModel(application: Application) : AndroidViewModel(application) {
    private val wifiRepo = WifiRepo(application)

    fun getWifiUpdateStatus() = wifiRepo.getUpdatedWifiStatus()
    fun updateWifiData(documentId: String, map: Map<String, Any>,wifiName:String) =
        wifiRepo.updateWifiItem(documentId, map,wifiName)
}