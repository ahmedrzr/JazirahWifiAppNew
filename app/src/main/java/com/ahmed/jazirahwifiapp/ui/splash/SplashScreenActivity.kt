package com.ahmed.jazirahwifiapp.ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.databinding.ActivitySplashScreenBinding
import com.ahmed.jazirahwifiapp.model.Permission
import com.ahmed.jazirahwifiapp.ui.login.LoginActivity
import com.ahmed.jazirahwifiapp.ui.main.MainActivity
import com.ahmed.jazirahwifiapp.utils.Constants.WAITING_TIME
import com.ahmed.jazirahwifiapp.utils.Resource
import com.ahmed.jazirahwifiapp.utils.UserPreferences
import com.bumptech.glide.Glide
import kotlinx.coroutines.*

class SplashScreenActivity : AppCompatActivity() {
    private val TAG = "SplashScreenActivity"
    private var _binding: ActivitySplashScreenBinding? = null
    private val binding get() = _binding
    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        splashViewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
        binding?.let {
            Glide.with(this@SplashScreenActivity)
                .load(R.drawable.wifi)
                .into(it.wifiLogo)
        }
        navigateToLoginActivity()
    }

    private fun navigateToLoginActivity() {
        lifecycleScope.launch(Dispatchers.Default) {
            delay(WAITING_TIME)
            if (splashViewModel.isUserLoggedIn().value == true) {
                Intent(this@SplashScreenActivity, MainActivity::class.java)
                    .apply {
                        startActivity(this)
                        finish()
                    }
            } else {
                Intent(this@SplashScreenActivity, LoginActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            }

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}