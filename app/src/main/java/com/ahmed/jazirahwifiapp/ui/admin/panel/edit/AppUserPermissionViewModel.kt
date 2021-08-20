package com.ahmed.jazirahwifiapp.ui.admin.panel.edit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ahmed.jazirahwifiapp.repository.AppPermissionRepo

class AppUserPermissionViewModel(application: Application) : AndroidViewModel(application) {
    private val appPermissionRepo = AppPermissionRepo(application)

    fun getUpdatePermissionStatus() = appPermissionRepo.getPermissionUpdateStatus()
    fun updateUserPermission(documentId: String, map: Map<String, Any>) =
        appPermissionRepo.updateAppPermissions(documentId, map)
}