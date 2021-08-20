package com.ahmed.jazirahwifiapp.ui.main.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.databinding.LayoutBottomSheetConfirmBinding
import com.ahmed.jazirahwifiapp.ui.UiViewModel

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ConfirmBottomSheetDialog : BottomSheetDialogFragment() {
    private var _binding: LayoutBottomSheetConfirmBinding? = null
    private val args by navArgs<ConfirmBottomSheetDialogArgs>()
    private val binding get() = _binding!!
    private lateinit var uiViewModel:UiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutBottomSheetConfirmBinding.inflate(inflater, container, false)
        val root = _binding!!.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiViewModel =
            ViewModelProvider(requireActivity()).get(UiViewModel::class.java)
        binding.apply {
            this.tvConfirmItem.text = args.wifiArg.wifiName
            this.btnConfirm.setOnClickListener {
                val map = mutableMapOf<String, Any>()
                map["documentId"] = args.wifiArg.id
                map["position"] = args.pos
                uiViewModel.setInterComLiveDat(map)
                dismiss()
            }
            this.btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}