package com.ahmed.jazirahwifiapp.ui.main.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.databinding.FragmentProfileUpdateBottomSheetDialogBinding
import com.ahmed.jazirahwifiapp.ui.main.profile.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ProfileUpdateBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: FragmentProfileUpdateBottomSheetDialogBinding? = null
    private val binding get() = _binding
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileUpdateBottomSheetDialogBinding.inflate(inflater, container, false)
        profileViewModel = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            this.btnCancel.setOnClickListener {
                dismiss()
            }
            this.btnConfirm.setOnClickListener {
                val map = mutableMapOf<String, String>()
                map["email"] = this.etEmailAddress.text.toString().trim()
                map["password"] = this.etPassword.text.toString().trim()
                profileViewModel.setCredentials(map)
                dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}