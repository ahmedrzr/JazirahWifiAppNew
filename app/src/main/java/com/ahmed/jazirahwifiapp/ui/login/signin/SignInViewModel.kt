package com.ahmed.jazirahwifiapp.ui.login.signin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ahmed.jazirahwifiapp.repository.AuthRepo

class SignInViewModel(app: Application) : AndroidViewModel(app) {
    private var authRepo = AuthRepo(app)

    fun userSignIn(email: String, password: String) {
        authRepo.signInUser(email, password)
    }
    fun getAuthUser() = authRepo.getAuthUser()



}