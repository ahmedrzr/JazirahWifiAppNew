package com.ahmed.jazirahwifiapp.ui.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.databinding.ActivityLoginBinding
import com.ahmed.jazirahwifiapp.model.Permission
import com.ahmed.jazirahwifiapp.ui.NotificationViewModel
import com.ahmed.jazirahwifiapp.ui.login.dialogs.ForgotPasswordViewModel
import com.ahmed.jazirahwifiapp.ui.login.signin.SignInViewModel
import com.ahmed.jazirahwifiapp.ui.login.signup.SignUpViewModel
import com.ahmed.jazirahwifiapp.ui.main.MainActivity
import com.ahmed.jazirahwifiapp.utils.Constants.SHARED_PREFERENCE_AUTH_USER
import com.ahmed.jazirahwifiapp.utils.Resource
import com.ahmed.jazirahwifiapp.utils.UserPreferences
import com.google.firebase.auth.FirebaseUser


class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        signInViewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        signUpViewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        notificationViewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        forgotPasswordViewModel =
            ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)
        obServer()

    }

    private fun obServer() {
        notificationViewModel.getNotify().observe(this, Observer { map ->
            val navController = findNavController(R.id.nav_host_fragment_activity_login)
            notificationViewModel.showNotification(
                navController,
                R.id.notificationDialogFragment3,
                map
            )
        })
        signInViewModel.getAuthUser().observe(this, Observer { it ->
            it?.let { authUser ->
                when (authUser) {
                    is Resource.Loading -> showProgressLoading(true)
                    is Resource.Error -> {
                        showProgressLoading(false)
                        notificationViewModel.prepareMessage(
                            true,
                            false,
                            false,
                            authUser.message.toString()
                        )
                    }
                    is Resource.Success -> {
                        showProgressLoading(false)
                        authUser.data?.let { it1 -> navigateToMain(it1) }
                    }
                }
            }
        })
        signUpViewModel.getSignUpUser().observe(this, Observer {
            when (it) {
                is Resource.Loading -> showProgressLoading(true)
                is Resource.Error -> {
                    showProgressLoading(false)
                    notificationViewModel.prepareMessage(
                        true,
                        false,
                        false,
                        it.message.toString()
                    )
                }
                is Resource.Success -> {
                    showProgressLoading(false)
                }
            }
        })
        forgotPasswordViewModel.getForgotPasswordLinkResponse()
            .observe(this, Observer {
                when (it) {
                    is Resource.Loading -> showProgressLoading(true)
                    is Resource.Error -> {
                        showProgressLoading(false)
                        notificationViewModel.prepareMessage(
                            true,
                            false,
                            false,
                            it.message.toString()
                        )
                    }
                    is Resource.Success -> {
                        showProgressLoading(false)
                    }
                }
            })
    }

    private fun navigateToMain(authUser: FirebaseUser) {
        UserPreferences.userAuthData(this, authUser)
        Intent(this, MainActivity::class.java).apply {
            startActivity(this)
            finish()
            overridePendingTransition(R.anim.from_right, R.anim.to_left)
        }
    }

    private fun showProgressLoading(showLoading: Boolean) {
        if (showLoading)
            binding?.progressBar?.visibility = View.VISIBLE
        else binding?.progressBar?.visibility = View.GONE
    }

}