package com.ahmed.jazirahwifiapp.ui.login.signup

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.databinding.SignUpFragmentBinding
import com.ahmed.jazirahwifiapp.model.Permission
import com.ahmed.jazirahwifiapp.ui.NotificationViewModel
import com.ahmed.jazirahwifiapp.ui.login.LoginViewModel
import com.ahmed.jazirahwifiapp.utils.Resource
import com.ahmed.jazirahwifiapp.utils.UserPreferences
import com.ahmed.jazirahwifiapp.utils.Validations
import com.google.firebase.auth.FirebaseUser

class SignUpFragment : Fragment() {
    private val TAG = "SignUpFragment"
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    private var _binding: SignUpFragmentBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SignUpFragmentBinding.inflate(inflater, container, false)
        signUpViewModel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)
        loginViewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)
        notificationViewModel =
            ViewModelProvider(requireActivity()).get(NotificationViewModel::class.java)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            this.tvSignUp.setOnClickListener {
                val action = SignUpFragmentDirections.actionSignUpFragmentPop()
                Navigation.findNavController(requireActivity().findViewById(R.id.nav_host_fragment_activity_login))
                    .navigate(action)
            }
            this.btnSignup.setOnClickListener {
                registerUser()
            }
        }
        obServer()
    }


    private fun registerUser() {
        val displayName = binding?.etFullName?.text.toString().trim()
        val email = binding?.etEmailAddress?.text.toString().trim()
        val password = binding?.etPassword?.text.toString().trim()
        val confirmPassword = binding?.etConfirmPassword?.text.toString().trim()
        val validation = Validations()
        if (validation.validateString(displayName) && validation.validateString(email) &&
            validation.validateString(password) && validation.validateString(confirmPassword)
        ) {
            if (validation.isEmailValid(email)) {
                if (validation.validateTwoStrings(password, confirmPassword)) {
                    signUpViewModel.registerNewUser(displayName, email, password)
                } else  notificationViewModel.prepareMessage(
                    false, true, false,
                    requireContext().getString(R.string.mismatch)
                )
            } else  notificationViewModel.prepareMessage(
                false, true, false,
                requireContext().getString(R.string.error_invalid_email)
            )
        } else
            notificationViewModel.prepareMessage(
                false, true, false,
                requireContext().getString(R.string.missing_input_fields)
            )
    }

    private fun obServer() {
        signUpViewModel.getSignUpUser().observe(requireActivity(), Observer {
            it?.let { authUser ->
                if (authUser is Resource.Success) {
                    authUser.data?.let { it1 -> signUpViewModel.setDefaultAppPermissions(it1) }
                }
            }
        })
        signUpViewModel.getDefaultPermissionResponse().observe(requireActivity(), Observer {
            if (!(it["isError"] as Boolean)) {
                signUpViewModel.getSignUpUser().value?.data?.let { it1 ->
                    navigateToEnrollmentSuccessFragment(
                        it1
                    )
                }
            } else {
                loginViewModel.setMsgHandler(true, it["message"] as String)
            }
        })
    }

    private fun navigateToEnrollmentSuccessFragment(authUser: FirebaseUser) {
        UserPreferences.userAuthData(requireContext(), authUser)
        val action =
            SignUpFragmentDirections.actionSignUpFragmentToUserEnrollmentSuccessFragment()
        Navigation.findNavController(requireActivity().findViewById(R.id.nav_host_fragment_activity_login))
            .navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

