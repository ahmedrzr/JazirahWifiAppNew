package com.ahmed.jazirahwifiapp.ui.admin.panel.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ahmed.jazirahwifiapp.repository.AppPermissionsSettingRepo
import com.ahmed.jazirahwifiapp.utils.Constants.APP_PERMISSION_CODE

class AppPermissionViewModel(application: Application) : AndroidViewModel(application) {
    private val appPermissionSettingRepo = AppPermissionsSettingRepo(application)

    fun getAppPermissionSettings() = appPermissionSettingRepo.getAppPermissions()
    fun fetchAppPermissionSettings() = appPermissionSettingRepo.fetchAppPermissionSettings()


    fun getUpdatedAppPermissionStatus() = appPermissionSettingRepo.getUpdatedAppPermissionStatus()
    fun updateAppPermissionSettings(appPermissionMap: Map<String,Any>, documentId:String) {
        appPermissionSettingRepo.updateAppPermissionSettings(appPermissionMap, documentId)
    }

}