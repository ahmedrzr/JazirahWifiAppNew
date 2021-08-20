package com.ahmed.jazirahwifiapp.ui.admin.panel.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.databinding.FragmentUserPermissionEditBinding
import com.ahmed.jazirahwifiapp.model.Permission
import com.ahmed.jazirahwifiapp.utils.Resource


class AppUserPermissionEdit : Fragment() {
    private var _binding: FragmentUserPermissionEditBinding? = null
    private lateinit var appUserPermissionViewModel: AppUserPermissionViewModel
    private val args by navArgs<AppUserPermissionEditArgs>()
    private var appAccessSelected: Boolean = false
    private var isEditViewSelected: Boolean = false
    private var adminSelected: Boolean = false
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserPermissionEditBinding.inflate(inflater, container, false)
        appUserPermissionViewModel =
            ViewModelProvider(requireActivity()).get(AppUserPermissionViewModel::class.java)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAppAccessType()
        updateArgs(args.permissionsArg)
        binding?.btnUpdate?.setOnClickListener{
            updatePermissions()
        }
          observer()
    }

    private fun observer() {
    }

    private fun updateArgs(permissions: Permission) {
        val admin = if (permissions.admin) 0 else 1
        val isViewAllowed = if (permissions.isEditViewAllowed) 0 else 1
        val appAccess = if (permissions.appAccess) 0 else 1

        binding?.tvDisplayName?.text = permissions.displayName
        binding?.tvEmailAddress?.text = permissions.email
        binding?.etWifiAccessCode?.setText(permissions.wifiAccessCode.toString())
        binding?.spinnerAdminAccess?.setSelection(admin)
        binding?.spinnerEditViewAccess?.setSelection(isViewAllowed)
        binding?.spinnerAppAccess?.setSelection(appAccess)
    }

    private fun updatePermissions() {
        if (validateUpdates().isNotEmpty()) {
             appUserPermissionViewModel.updateUserPermission(args.permissionsArg.id, validateUpdates())
        }
    }

    private fun validateUpdates(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val wifiAccessCode = binding?.etWifiAccessCode?.text.toString()
        if(wifiAccessCode.isNotEmpty()){
            if (wifiAccessCode.toString().toInt() != args.permissionsArg.wifiAccessCode) {
                map["wifiAccessCode"] = wifiAccessCode.toString().toInt()
            }
        }

        if (adminSelected != args.permissionsArg.admin) {
            map["admin"] = adminSelected
        }
        if (isEditViewSelected != args.permissionsArg.isEditViewAllowed) {
            map["isEditViewAllowed"] = isEditViewSelected
        }
        if (appAccessSelected != args.permissionsArg.appAccess) {
            map["appAccess"] = appAccessSelected
        }
        return map
    }

    private fun initAppAccessType() {
        val appAccessAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.app_access, R.layout.dropdown_item
        )
        val allowEditaAapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.app_access, R.layout.dropdown_item
        )
        val adminAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.app_access, R.layout.dropdown_item
        )
        appAccessAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.spinnerAppAccess?.adapter = appAccessAdapter
        allowEditaAapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.spinnerEditViewAccess?.adapter = allowEditaAapter
        adminAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.spinnerAdminAccess?.adapter = adminAdapter

        binding?.spinnerAppAccess?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    appAccessSelected = p2 == 0
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
        binding?.spinnerEditViewAccess?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    isEditViewSelected = p2 == 0
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
        binding?.spinnerAdminAccess?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    adminSelected = p2 == 0
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}