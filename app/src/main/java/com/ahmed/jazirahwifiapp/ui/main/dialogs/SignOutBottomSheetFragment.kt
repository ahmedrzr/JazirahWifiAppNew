package com.ahmed.jazirahwifiapp.ui.main.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.databinding.FragmentSignOutBottomSheetBinding
import com.ahmed.jazirahwifiapp.ui.login.LoginActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SignOutBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentSignOutBottomSheetBinding? = null
    private val binding get() = _binding
    private lateinit var signOutConfirmViewModel: SignOutConfirmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog)
        signOutConfirmViewModel =
            ViewModelProvider(requireActivity()).get(SignOutConfirmViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignOutBottomSheetBinding.inflate(inflater, container, false)
        isCancelable = false

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            this.btnCancel.setOnClickListener {
                dismiss()
            }
            this.btnSignOut.setOnClickListener {
                signOutConfirmViewModel.setSignOut()
                dismiss()
            }
        }
        signOutConfirmViewModel.isLoggedOut().observe(requireActivity(), Observer {
            if (!it) {
                signOut()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        requireActivity().viewModelStore.clear()
    }

    private fun signOut() {
        Intent(requireActivity(), LoginActivity::class.java).apply {
            startActivity(this)
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.from_right, R.anim.to_left)
        }
    }
}