package com.ahmed.jazirahwifiapp.ui.login.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.databinding.FragmentUserEnrollmentSuccessBinding
import com.ahmed.jazirahwifiapp.ui.login.LoginViewModel
import com.ahmed.jazirahwifiapp.ui.login.signin.SignInViewModel
import com.ahmed.jazirahwifiapp.ui.main.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class UserEnrollmentSuccessFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentUserEnrollmentSuccessBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserEnrollmentSuccessBinding.inflate(inflater, container, false)
        isCancelable = false
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            this.btnContinue.setOnClickListener {
                navigateToMain()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun navigateToMain() {
        Intent(requireActivity(), MainActivity::class.java).also {
            requireActivity().startActivity(it)
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left)
        }
    }
}