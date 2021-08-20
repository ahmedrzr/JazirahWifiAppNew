package com.ahmed.jazirahwifiapp.ui.main.profile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ahmed.jazirahwifiapp.R
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.ahmed.jazirahwifiapp.databinding.FragmentProfileBinding
import com.ahmed.jazirahwifiapp.ui.NotificationViewModel
import com.ahmed.jazirahwifiapp.ui.main.MainViewModel
import com.ahmed.jazirahwifiapp.utils.Resource
import com.ahmed.jazirahwifiapp.utils.Validations
import com.google.firebase.auth.FirebaseUser


class ProfileFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding
    private var authUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        mainViewModel =
            ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        notificationViewModel =
            ViewModelProvider(requireActivity()).get(NotificationViewModel::class.java)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            this.btnUpdate.setOnClickListener {
                val action =
                    ProfileFragmentDirections.actionNavigationProfileToProfileUpdateBottomSheetDialog()
                Navigation.findNavController(requireActivity().findViewById(R.id.nav_host_fragment_activity_main))
                    .navigate(action)
            }
        }
        observer()
    }


    private fun observer() {
        // getting auth user data
        profileViewModel.getAuthUserData().observe(requireActivity(), Observer {
            when (it) {
                is Resource.Success -> {
                    authUser = it.data
                    binding?.apply {
                        this.etFullName.setText(it.data?.displayName)
                        this.etEmailAddress.setText(it.data?.email)
                    }
                }
            }
        })
        //get email credentials from confirm dialog
        profileViewModel.getCredentials().observe(requireActivity(), Observer { map ->
            val authEmail = map["email"].toString()
            val authPwd = map["password"].toString()
            validateAndUpdate(authEmail, authPwd)
        })
    }

    private fun validateAndUpdate(authEmail: String, authPassword: String) {
        val validation = Validations()
        if (validation.isEmailValid(authEmail) && validation.validateString(authEmail) &&
            validation.validateString(authPassword)
        ) {
            val displayName = binding?.etFullName?.text.toString()
            val email = binding?.etEmailAddress?.text.toString()
            val password = binding?.etPassword?.text.toString()
            if(validation.validateEmail(email)){
                profileViewModel.upDateUserProfile(
                    authEmail,
                    authPassword,
                    displayName,
                    email,
                    password
                )
            }else
                notificationViewModel.prepareMessage(
                    false,
                    true,
                    false,
                    requireContext().getString(R.string.error_invalid_email)
                )

        } else
            notificationViewModel.prepareMessage(
                false,
                true,
                false,
                "Please provide currently logged email & password"
            )
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.signOutAction -> {
                signOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOut() {
        val action = ProfileFragmentDirections.actionNavigationProfileToSignOutBottomSheetFragment()
        Navigation.findNavController(requireActivity().findViewById(R.id.nav_host_fragment_activity_main))
            .navigate(action)
    }

}