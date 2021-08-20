package com.ahmed.jazirahwifiapp.ui.main.enrollment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.ahmed.jazirahwifiapp.R
import kotlinx.android.synthetic.main.fragment_enroll.*
import androidx.lifecycle.Observer
import com.ahmed.jazirahwifiapp.databinding.FragmentEnrollBinding
import com.ahmed.jazirahwifiapp.model.AppSetting
import com.ahmed.jazirahwifiapp.ui.NotificationViewModel
import com.ahmed.jazirahwifiapp.ui.main.MainViewModel
import com.ahmed.jazirahwifiapp.utils.Constants
import com.ahmed.jazirahwifiapp.utils.DateConverters
import com.ahmed.jazirahwifiapp.utils.Resource
import com.ahmed.jazirahwifiapp.utils.UserPreferences
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.Gson
import kotlinx.android.synthetic.main.layout_permission_denied.*
import kotlin.collections.ArrayList


class
EnrollmentFragment : Fragment() {
    private val TAG = "EnrollmentFragment"
    private lateinit var enrollmentViewModel: EnrollmentViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var mainViewModel: MainViewModel
    private var _binding: FragmentEnrollBinding? = null
    private var connectionTypes: Array<String>? = null
    private var securityTypes: Array<String>? = null
    private var connectionType: Int = 0
    private var ipAddress: String = "0.0.0.0"
    private val args by navArgs<EnrollmentFragmentArgs>()
    private val binding get() = _binding
    private lateinit var userPermissionPreference: SharedPreferences
    private var isAllowedViewEdit = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEnrollBinding.inflate(inflater, container, false)
        enrollmentViewModel =
            ViewModelProvider(requireActivity()).get(EnrollmentViewModel::class.java)
        notificationViewModel =
            ViewModelProvider(requireActivity()).get(NotificationViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPermissionPreference = UserPreferences.getUserPermissionPreference(requireContext())
        init()
        if (mainViewModel.isNetworkConnected())
            checkOnline()
        else checkOffline()
    }

    private fun init() {
        securityTypes = resources.getStringArray(R.array.wifi_security_modes)
        connectionTypes = resources.getStringArray(R.array.wifi_connection_types)
        initDropDown()
        if (args.isToEdit) {
            (activity as AppCompatActivity).supportActionBar?.title = args.wifiArg?.wifiName
        }
    }

    private fun checkOnline() {
        observerOnline()
       // observers()
    }

    private fun checkOffline() {
        observerOffline()
    }

    private fun observerOnline() {
        mainViewModel.getQueryUserPermission().observe(requireActivity(), Observer {
            if (it as Resource<*> is Resource.Success<*>) {
                val appSettings = it.data
                isAllowedViewEdit = appSettings!!.isEditViewAllowed
                checkIsUserAllowed()
            }
        })
    }

    private fun observerOffline() {
        isAllowedViewEdit =
            userPermissionPreference.getBoolean(Constants.APP_IS_EDIT_VIEW_ALLOWED, false)
        checkIsUserAllowed()
    }

    private fun checkIsUserAllowed() {

        if (isAllowedViewEdit) {
            visibleView(true)
        } else {
            visibleView(false)
        }

    }

    private fun visibleView(isViewVisible: Boolean) {
        if (isViewVisible) {
            if (args.wifiArg != null) {
                binding?.apply {
                    clUpdateFields.visibility = View.VISIBLE
                    lyPermissionDenied.visibility = View.GONE
                    btn_add.visibility = View.GONE
                    btn_update.visibility = View.VISIBLE
                    btnUpdate.setOnClickListener {
                        saveOrUpdateWifi()
                    }
                }
                updateField()
            } else {
                binding?.apply {
                    clUpdateFields.visibility = View.VISIBLE
                    lyPermissionDenied.visibility = View.GONE
                    btn_add.visibility = View.VISIBLE
                    btn_update.visibility = View.GONE
                    btnAdd.setOnClickListener {
                        saveOrUpdateWifi()
                    }
                }
            }
        } else {
            binding?.apply {
                clUpdateFields.visibility = View.GONE
                lyPermissionDenied.visibility = View.VISIBLE
                tvPermissionMsg.text =
                    requireContext().getString(R.string.permission_system_administrator)
                Glide.with(requireContext())
                    .load(R.drawable.giphy)
                    .into(gifImgViewFront)
            }
        }
    }

    private fun observers() {
        mainViewModel.getEnrollWifiStatus()
            .observe(requireActivity(), Observer {
                when (val response = it as Resource<*>) {
                    is Resource.Success -> {
                        resetFields()
                    }
                }
            })
    }

    private fun saveOrUpdateWifi() {

        if (args.wifiArg == null) {
            if (prepareDataToEnroll()["wifiName"].toString().isEmpty()) {
                Log.e(TAG, "saveOrUpdateWifi: enrollment ")
                notificationViewModel.prepareMessage(
                    false,
                    true,
                    false,
                    "WIFI name required!"
                )
            } else {
                mainViewModel.enrollWifiItem(prepareDataToEnroll())
            }

        } else {
            if (prepareDataToUpload()["wifiName"].toString().isEmpty()) {
                Log.e(TAG, "saveOrUpdateWifi: UPDATE")
                notificationViewModel.prepareMessage(
                    false,
                    true,
                    false,
                    "WIFI name required!"
                )
            } else {
                mainViewModel.updateWifiItem(
                    args.wifiArg!!.id,
                    prepareDataToUpload(),
                    binding?.etWifiName?.text.toString().trim()
                )
            }

        }

    }

    private fun prepareDataToEnroll(): Map<String, Any> {
        val wifiName = binding?.etWifiName?.text.toString().trim()
        val wifiPassword = binding?.etWifiPassword?.text.toString().trim()
        val securityTypeIndex = resources.getStringArray(R.array.wifi_security_modes).indexOf(
            binding?.autoCompleteSecurityType?.text.toString()
        )
        connectionType = resources.getStringArray(R.array.wifi_connection_types).indexOf(
            binding?.autoCompleteConnectionType?.text.toString()
        )
        val routerLoginId = binding?.etLoginId?.text.toString().trim()
        val routerLoginPwd = binding?.etPassword?.text.toString().trim()
        val deviceLocation = binding?.etDeviceLocation?.text.toString().trim()
        ipAddress = if (connectionType == 0 && binding?.etIpAddress?.text.toString().isNotEmpty())
            binding?.etIpAddress?.text.toString()
        else "0.0.0.0"
        val broadcastSsid = binding?.broadcastSwitch?.isChecked as Boolean
        val map = mutableMapOf<String, Any>()
        map["wifiName"] = wifiName
        map["wifiPassword"] = wifiPassword
        map["securityType"] = securityTypeIndex
        map["connectionType"] = connectionType
        map["ipAddress"] = ipAddress
        map["routerLoginId"] = routerLoginId
        map["routerLoginPassword"] = routerLoginPwd
        map["deviceLocation"] = deviceLocation
        map["visibility"] = broadcastSsid
        val permissionArray = ArrayList<Int>()
        permissionArray.add(userPermissionPreference.getInt(Constants.APP_WIFI_ACCESS_CODE, -1))
        map["permission"] = permissionArray
        @ServerTimestamp
        map["createdAt"] = Timestamp.now()

        @ServerTimestamp
        map["updatedAt"] = Timestamp.now()
        return map
    }

    private fun prepareDataToUpload(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val wifiName = binding?.etWifiName?.text.toString().trim()
        if (wifiName != args.wifiArg?.wifiName)
            map["wifiName"] = wifiName
        val wifiPassword = binding?.etWifiPassword?.text.toString().trim()
        if (wifiPassword != args.wifiArg?.wifiPassword)
            map["wifiPassword"] = wifiPassword
        val securityTypeIndex = resources.getStringArray(R.array.wifi_security_modes).indexOf(
            binding?.autoCompleteSecurityType?.text.toString()
        )
        if (securityTypeIndex != args.wifiArg?.securityType)
            map["securityType"] = securityTypeIndex
        val connectionType = resources.getStringArray(R.array.wifi_connection_types).indexOf(
            binding?.autoCompleteConnectionType?.text.toString()
        )
        if (connectionType != args.wifiArg?.connectionType)
            map["connectionType"] = connectionType
        val routerLoginId = binding?.etLoginId?.text.toString().trim()
        if (routerLoginId != args.wifiArg?.routerLoginId)
            map["routerLoginId"] = routerLoginId
        val routerLoginPwd = binding?.etPassword?.text.toString().trim()
        if (routerLoginPwd != args.wifiArg?.routerLoginPassword)
            map["routerLoginPassword"] = routerLoginPwd
        val deviceLocation = binding?.etDeviceLocation?.text.toString().trim()
        if (deviceLocation != args.wifiArg?.deviceLocation)
            map["deviceLocation"] = deviceLocation
        val ipAddress = binding?.etIpAddress?.text.toString().trim()
        if (ipAddress != args.wifiArg?.ipAddress)
            map["ipAddress"] = ipAddress
        val broadcastSsid = binding?.broadcastSwitch?.isChecked as Boolean
        if (broadcastSsid != args.wifiArg?.visibility)
            map["visibility"] = broadcastSsid
        @ServerTimestamp
        map["updatedAt"] = Timestamp.now()
        return map
    }

    private fun updateField() {
        (activity as AppCompatActivity).supportActionBar?.title = args.wifiArg?.wifiName
        val wifi = args.wifiArg
        binding?.apply {
            this.etWifiName.setText(wifi?.wifiName)
            this.etWifiPassword.setText(wifi?.wifiPassword)
            if (wifi != null) {
                this.autoCompleteSecurityType.setText(
                    resources.getStringArray(R.array.wifi_security_modes)
                            [wifi.securityType], false
                )
            }
            if (wifi != null) {
                this.autoCompleteConnectionType.setText(
                    resources.getStringArray(R.array.wifi_connection_types)
                            [wifi.connectionType], false
                )
            }
            if (wifi != null) {
                if (wifi.connectionType == 1) {
                    this.layIpAddress.visibility = View.GONE
                }
            }
            if (wifi != null) {
                this.etIpAddress.setText(wifi.ipAddress)
            }
            if (wifi != null) {
                this.etLoginId.setText(wifi.routerLoginId)
            }
            if (wifi != null) {
                this.etPassword.setText(wifi.routerLoginPassword)
            }
            if (wifi != null) {
                this.etDeviceLocation.setText(wifi.deviceLocation)
            }
            if (wifi != null) {
                this.broadcastSwitch.isChecked = wifi.visibility
            }
        }

    }

    private fun resetFields() {

        binding?.apply {
            etWifiName.text?.clear()
            etPassword.text?.clear()
            etIpAddress.text?.clear()
            etLoginId.text?.clear()
            etWifiPassword.text?.clear()
            etDeviceLocation.text?.clear()
            autoCompleteSecurityType.setText(securityTypes?.get(3), false)
            autoCompleteConnectionType.setText(connectionTypes?.get(0), false)
            etWifiName.requestFocus()
        }
    }

    private fun initDropDown(): Unit {
        val wifiSecurityAdapter =
            ArrayAdapter(
                requireContext(), R.layout.dropdown_item,
                securityTypes as Array<out String>
            )
        binding?.apply {
            autoCompleteSecurityType.setAdapter(wifiSecurityAdapter)
            autoCompleteSecurityType.setText(wifiSecurityAdapter.getItem(3), false)
        }

        val connectionTypesAdapter =
            ArrayAdapter(
                requireContext(), R.layout.dropdown_item,
                connectionTypes as Array<out String>
            )
        binding?.apply {
            autoCompleteConnectionType.setAdapter(connectionTypesAdapter)
            autoCompleteConnectionType.setText(
                connectionTypesAdapter.getItem(connectionType),
                false
            )
            autoCompleteConnectionType.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, i, _ ->
                    if (i == 1)
                        layIpAddress.visibility = View.GONE
                    else layIpAddress.visibility = View.VISIBLE
                    connectionType = i
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}