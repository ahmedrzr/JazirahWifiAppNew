package com.ahmed.jazirahwifiapp.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ahmed.jazirahwifiapp.WifiApplication
import com.ahmed.jazirahwifiapp.repository.AppPermissionRepo
import com.ahmed.jazirahwifiapp.repository.AppPermissionsSettingRepo
import com.ahmed.jazirahwifiapp.repository.AuthRepo
import com.ahmed.jazirahwifiapp.repository.WifiRepo

class MainViewModel(val app: Application) : AndroidViewModel(app) {
    private val appSettingRepo = AppPermissionsSettingRepo(app)
    private val appUserPermissionRepo = AppPermissionRepo(app)
    private val wifiRepo = WifiRepo(app)
    private val authUser = AuthRepo(app)


    fun fetchAppPermissionSettings() = appSettingRepo.fetchAppPermissionSettingsRealTime()
    fun getAppPermissionSettings() = appSettingRepo.getAppPermissions()

    fun getQueryUserPermission() = appUserPermissionRepo.getAppPermissions()
    fun queryUserPermission(email: String) = appUserPermissionRepo.queryUserRealTime(email)


    fun enrollWifiItem(map: Map<String, Any>) = wifiRepo.enrollWifi(map)
    fun getEnrollWifiStatus() = wifiRepo.getEnrolledStatus()

    fun updateWifiItem(documentId: String, map: Map<String, Any>, wifiName:String) =
        wifiRepo.updateWifiItem(documentId, map, wifiName)

    fun getWifiUpdateStatus() = wifiRepo.getUpdatedWifiStatus()
    fun isNetworkConnected() = (app as WifiApplication).hasInternetConnection()

    val getAuthUserDisplayName get() = authUser.getAuthUser().value?.data?.displayName

}