package com.ahmed.jazirahwifiapp.ui.main.dialogs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ahmed.jazirahwifiapp.repository.AuthRepo

class SignOutConfirmViewModel(val app:Application):AndroidViewModel(app) {
    private val authRepo = AuthRepo(app)
    fun setSignOut() =
        authRepo.setLogOut()

    fun isLoggedOut() = authRepo.isLoggedOut()
}