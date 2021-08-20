package com.ahmed.jazirahwifiapp.ui.admin.panel.users

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ahmed.jazirahwifiapp.repository.AppPermissionRepo

class UsersViewModel(application: Application) : AndroidViewModel(application) {
    val appPermissionRepo = AppPermissionRepo(application)

    fun getUsers() = appPermissionRepo.getQueryUsers()
    fun queryUsers() = appPermissionRepo.queryUsers()
}