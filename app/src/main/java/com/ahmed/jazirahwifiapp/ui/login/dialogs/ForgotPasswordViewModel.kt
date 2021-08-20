package com.ahmed.jazirahwifiapp.ui.login.dialogs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ahmed.jazirahwifiapp.repository.AuthRepo

class ForgotPasswordViewModel(val app: Application) : AndroidViewModel(app) {
    private val authRepo = AuthRepo(app)

    fun sendPasswordResetLink(email: String) =
        authRepo.sendResetPasswordLink(email)

    fun getForgotPasswordLinkResponse() = authRepo.getForgotPasswordLinkResponse()
}