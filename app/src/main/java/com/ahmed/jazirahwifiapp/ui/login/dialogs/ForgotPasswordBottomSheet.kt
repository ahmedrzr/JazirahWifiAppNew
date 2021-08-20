package com.ahmed.jazirahwifiapp.ui.login.dialogs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.databinding.FragmentForgotPasswordBottomSheetBinding
import com.ahmed.jazirahwifiapp.ui.NotificationViewModel
import com.ahmed.jazirahwifiapp.utils.Resource
import com.ahmed.jazirahwifiapp.utils.Validations
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ForgotPasswordBottomSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentForgotPasswordBottomSheetBinding? = null
    private val binding get() = _binding
    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel
    private lateinit var notificationViewModel: NotificationViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBottomSheetBinding.inflate(inflater, container, false)
        forgotPasswordViewModel =
            ViewModelProvider(requireActivity()).get(ForgotPasswordViewModel::class.java)
        notificationViewModel =
            ViewModelProvider(requireActivity()).get(NotificationViewModel::class.java)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.apply {
            this.rlPrePost.visibility = View.VISIBLE
            this.rlAfterPost.visibility = View.GONE

            this.btnNext.setOnClickListener {
                val email = this.etEmailAddress.text.toString().trim()
                val validations = Validations()
                if (validations.validateString(email) && validations.isEmailValid(email)) {
                    forgotPasswordViewModel.sendPasswordResetLink(email)
                } else
                    notificationViewModel.prepareMessage(
                        false,
                        true,
                        false,
                        requireContext().getString(R.string.error_invalid_email)
                    )
            }
            this.btnDone.setOnClickListener {
                dismiss()
            }
        }
        observer()
    }

    private fun observer() {
        forgotPasswordViewModel.getForgotPasswordLinkResponse()
            .observe(requireActivity(), Observer {
                when (it) {
                    is Resource.Success -> {
                        postRest()
                    }
                }
            })
    }

    private fun postRest() {
        binding?.rlPrePost?.visibility = View.GONE
        binding?.rlAfterPost?.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        requireActivity().viewModelStore.clear();

    }

}