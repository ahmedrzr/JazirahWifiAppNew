package com.ahmed.jazirahwifiapp.ui.login.signup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ahmed.jazirahwifiapp.model.Permission
import com.ahmed.jazirahwifiapp.repository.AppPermissionRepo
import com.ahmed.jazirahwifiapp.repository.AuthRepo
import com.google.firebase.auth.FirebaseUser

class SignUpViewModel(app: Application) : AndroidViewModel(app) {
    private var authRepo = AuthRepo(app)
    private var appPermissionRepo = AppPermissionRepo(app)
    fun registerNewUser(displayName: String, email: String, password: String) =
        authRepo.signUpUser(displayName, email, password)

    fun getSignUpUser() = authRepo.getAuthUser()

    fun getSignUpNotifications() = authRepo.getAuthRepoNotification()

    fun setDefaultAppPermissions(authUser: FirebaseUser) {
        val map = mutableMapOf<String, Any>()
        map["displayName"] = authUser.displayName.toString()
        map["email"] = authUser.email.toString()
        map["appAccess"] = false
        map["admin"] = false
        map["isEditViewAllowed"] = false
        map["wifiAccessCode"] = -1
        appPermissionRepo.addToPermissionCollection(map)
    }

    fun getDefaultPermissionResponse() = appPermissionRepo.getDefaultPermissionResponse()

}