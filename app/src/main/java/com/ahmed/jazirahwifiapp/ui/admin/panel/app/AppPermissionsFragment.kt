package com.ahmed.jazirahwifiapp.ui.admin.panel.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.databinding.FragmentAppPermissionsBinding
import com.ahmed.jazirahwifiapp.model.AppSetting
import com.ahmed.jazirahwifiapp.ui.admin.panel.PanelViewModel
import com.ahmed.jazirahwifiapp.utils.Resource
import com.ahmed.jazirahwifiapp.utils.Validations
import kotlinx.android.synthetic.main.fragment_app_permissions.*

class AppPermissionsFragment : Fragment() {
    private val TAG = "AppPermissionsFragment"
    private var _binding: FragmentAppPermissionsBinding? = null
    private val binding get() = _binding
    private lateinit var appPermissionViewModel: AppPermissionViewModel
    private lateinit var panelViewModel: PanelViewModel
    private lateinit var appSetting: AppSetting
    private var appPermissionTypes: Array<String>? = null
    private var pushPermissionTypes: Array<String>? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAppPermissionsBinding.inflate(inflater, container, false)
        appPermissionViewModel =
            ViewModelProvider(requireActivity()).get(AppPermissionViewModel::class.java)
        panelViewModel =
            ViewModelProvider(requireActivity()).get(PanelViewModel::class.java)
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appPermissionViewModel.fetchAppPermissionSettings()
        binding?.apply {
            btnUpdate.setOnClickListener {
                updateSettings()
            }
        }
        initDropDown()
        observers()
    }

    private fun updateSettings() {
        val appPermissionSelected = binding?.autoCompleteAppPermission?.text.toString().trim()
        val appPermission =
            appPermissionSelected == resources.getStringArray(R.array.app_access)[0]
        val pushPermissionSelected =
            binding?.autoCompleteAppPushNotificationPermission?.text.toString().trim()
        val pushPermission =
            pushPermissionSelected == resources.getStringArray(R.array.app_push_notification)[0]
        val map = mutableMapOf<String, Any>()
        if (appSetting.appPermission != appPermission)
            map["appPermission"] = appPermission

        if (appSetting.appPushNotificationPermission != pushPermission)
            map["appPushNotificationPermission"] = pushPermission
        if (map.count() > 0)
            updateToFirestore(map)

    }

    private fun initDropDown() {
        appPermissionTypes = resources.getStringArray(R.array.app_access)
        val adapter = ArrayAdapter(
            requireContext(), R.layout.dropdown_item,
            appPermissionTypes as Array<out String>
        )
        binding?.autoCompleteAppPermission?.setAdapter(adapter)
        pushPermissionTypes = resources.getStringArray(R.array.app_push_notification)
        val pushAdapter = ArrayAdapter(
            requireContext(), R.layout.dropdown_item,
            pushPermissionTypes as Array<out String>
        )
        binding?.autoCompleteAppPushNotificationPermission?.setAdapter(pushAdapter)
    }

    private fun updateToFirestore(appPermissionMap:Map<String,Any>) {
        appPermissionViewModel.updateAppPermissionSettings(appPermissionMap, appSetting.id)
    }

    private fun observers() {
        appPermissionViewModel.getAppPermissionSettings().observe(requireActivity(), Observer {
            when (val response = it as Resource<*>) {
                is Resource.Success -> {
                    appSetting = response.data as AppSetting
                    setFields(appSetting)
                }
            }
        })

    }

    private fun setFields(appSetting: AppSetting) {
        binding?.apply {
            val checkAppPermission = if (appSetting.appPermission) 0 else 1
            autoCompleteAppPermission.setText(
                resources.getStringArray(R.array.app_access)[checkAppPermission],
                false
            )
            val checkPushPermission = if (appSetting.appPushNotificationPermission) 0 else 1
            autoCompleteAppPushNotificationPermission.setText(
                resources.getStringArray(R.array.app_push_notification)[checkPushPermission],
                false
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}