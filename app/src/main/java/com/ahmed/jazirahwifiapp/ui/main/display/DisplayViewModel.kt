package com.ahmed.jazirahwifiapp.ui.main.display

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.ahmed.jazirahwifiapp.repository.AppPermissionRepo
import com.ahmed.jazirahwifiapp.repository.WifiRepo

class DisplayViewModel(app: Application) : AndroidViewModel(app) {
    private val appPermissionRepo = AppPermissionRepo(app)

    fun getUserByWifiAccessCode() = appPermissionRepo.getUserByWifiAccessCode()
    fun setUserByWifiAccessCode(wifiAccessCodes: ArrayList<Int>) =
        appPermissionRepo.queryUserByWifiAccessCode(wifiAccessCodes)

}