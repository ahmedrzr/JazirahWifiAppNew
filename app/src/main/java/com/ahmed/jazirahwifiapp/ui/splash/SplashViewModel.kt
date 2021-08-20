package com.ahmed.jazirahwifiapp.ui.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ahmed.jazirahwifiapp.repository.AuthRepo

class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private var authRepo = AuthRepo(application)

    fun isUserLoggedIn() = authRepo.isLoggedOut()
}