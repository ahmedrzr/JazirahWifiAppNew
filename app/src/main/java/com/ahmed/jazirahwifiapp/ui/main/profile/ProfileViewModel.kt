package com.ahmed.jazirahwifiapp.ui.main.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.ahmed.jazirahwifiapp.repository.AuthRepo

class ProfileViewModel(app: Application) : AndroidViewModel(app) {
    private val authRepo = AuthRepo(app)
    private val credentials: MutableLiveData<Map<String, String>> = MutableLiveData()

    fun getAuthUserData() = authRepo.getAuthUser()

    fun setCredentials(map: Map<String, String>) = credentials.postValue(map)
    fun getCredentials() = credentials

    fun upDateUserProfile(
        authEmail: String, authPwd: String, displayName: String,
        email: String, password: String
    ) = authRepo.updateAuthUserProfile(
        authEmail,
        authPwd,
        displayName,
        email,
        password
    )


}