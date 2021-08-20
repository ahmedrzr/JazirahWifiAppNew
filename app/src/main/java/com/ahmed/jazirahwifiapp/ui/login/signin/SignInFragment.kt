package com.ahmed.jazirahwifiapp.ui.login.signin

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.databinding.SignInFragmentBinding
import com.ahmed.jazirahwifiapp.ui.NotificationViewModel
import com.ahmed.jazirahwifiapp.ui.login.LoginViewModel
import com.ahmed.jazirahwifiapp.utils.Validations

class SignInFragment : Fragment() {
    private var _binding: SignInFragmentBinding? = null
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SignInFragmentBinding.inflate(inflater, container, false)
        signInViewModel = ViewModelProvider(requireActivity()).get(SignInViewModel::class.java)
        loginViewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)
        notificationViewModel =
            ViewModelProvider(requireActivity()).get(NotificationViewModel::class.java)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            this?.tvSignUp?.setOnClickListener {
                val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
                Navigation.findNavController(
                    requireActivity().findViewById(R.id.nav_host_fragment_activity_login)
                ).navigate(action)
            }
            this?.btnLogin?.setOnClickListener {
                logInUser()
            }
            this?.tvForgotPassword?.setOnClickListener {
                val action =
                    SignInFragmentDirections.actionSignInFragmentToForgotPasswordBottomSheet()
                Navigation.findNavController(requireActivity().findViewById(R.id.nav_host_fragment_activity_login))
                    .navigate(action)
            }
        }
    }

    private fun logInUser() {
        val email = binding?.etEmailAddress?.text.toString().trim()
        val password = binding?.etPassword?.text.toString().trim()
        val validation = Validations()
        if (validation.validateEmail(email) && validation.validateString(password)) {
            if (validation.isEmailValid(email)) {
                signInViewModel.userSignIn(email, password)
            } else notificationViewModel.prepareMessage(
                false,
                true,
                false,
                requireContext().getString(R.string.error_invalid_email)
            )

        } else notificationViewModel.prepareMessage(
            false,
            true,
            false,
            requireContext().getString(R.string.missing_input_fields)
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}