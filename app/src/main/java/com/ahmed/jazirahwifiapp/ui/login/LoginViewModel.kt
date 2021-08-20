package com.ahmed.jazirahwifiapp.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.ahmed.jazirahwifiapp.model.Permission
import com.ahmed.jazirahwifiapp.repository.AppPermissionRepo
import com.ahmed.jazirahwifiapp.repository.AuthRepo
import com.ahmed.jazirahwifiapp.utils.DataMsgHandler
import com.ahmed.jazirahwifiapp.utils.Resource


class LoginViewModel(app: Application) : AndroidViewModel(app) {
    private val authRepo = AuthRepo(app)
    private val appPermissionRepo = AppPermissionRepo(app)
    private val dataMsgHandler = DataMsgHandler()

    fun setMsgHandler(isErrorMessage: Boolean, message: String) {
        dataMsgHandler.setUpdateUiNotification(isErrorMessage, message)
    }

    fun getUiMessageNotification(): MutableLiveData<Map<String, Any>> =
        dataMsgHandler.getUpdateUiNotifications()

    fun queryAppPermissions(email: String) {
        appPermissionRepo.queryUser(email)
    }

    fun getAppPermissions():MutableLiveData<Resource<Permission>> = appPermissionRepo.getAppPermissions()

    fun signOut()= authRepo.setLogOut()


}