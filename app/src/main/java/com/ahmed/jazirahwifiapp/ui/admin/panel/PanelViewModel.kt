package com.ahmed.jazirahwifiapp.ui.admin.panel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.ahmed.jazirahwifiapp.repository.AuthRepo
import com.ahmed.jazirahwifiapp.utils.DataMsgHandler
import com.google.firebase.auth.FirebaseAuth

class PanelViewModel(application: Application) : AndroidViewModel(application) {
    private val dataMsgHandler = DataMsgHandler()
    private val authRepo = AuthRepo(application)


    fun updateUiComponents() = dataMsgHandler.getUpdateUiNotifications()
    fun setUpdateUiComponents(isError: Boolean, msg: String) =
        dataMsgHandler.setUpdateUiNotification(isError, msg)

    fun getAuthUser() = authRepo.getAuthUser()

    fun setSignOut() = authRepo.setLogOut()


}