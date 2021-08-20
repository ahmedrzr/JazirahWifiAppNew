package com.ahmed.jazirahwifiapp.ui.admin.panel.edit

import android.os.Bundle
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.databinding.FragmentWifiEditBinding
import com.ahmed.jazirahwifiapp.utils.Constants
import com.ahmed.jazirahwifiapp.utils.DateConverters
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.synthetic.main.fragment_wifi_edit.*
import java.util.*
import kotlin.collections.ArrayList


class WifiEditFragment : Fragment() {
    private val TAG = "WifiEditFragment"
    private lateinit var wifiEditViewModel: WifiEditViewModel
    private val args by navArgs<WifiEditFragmentArgs>()
    private var _binding: FragmentWifiEditBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWifiEditBinding.inflate(inflater, container, false)
        wifiEditViewModel = ViewModelProvider(requireActivity()).get(WifiEditViewModel::class.java)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intDropDowns()
        updateField()
        binding?.btnUpdate?.setOnClickListener {
            update()
        }
    }

    private fun update() {
        wifiEditViewModel.updateWifiData(
            args.wifiArgs.id,
            prepareDataToUpload(),
            binding?.etWifiName?.text.toString().trim()
        )
    }

    private fun prepareDataToUpload(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val wifiName = binding?.etWifiName?.text.toString().trim()
        if (wifiName != args.wifiArgs.wifiName)
            map["wifiName"] = wifiName
        val wifiPassword = binding?.etWifiPassword?.text.toString().trim()
        if (wifiPassword != args.wifiArgs.wifiPassword)
            map["wifiPassword"] = wifiPassword
        val securityTypeIndex = resources.getStringArray(R.array.wifi_security_modes).indexOf(
            binding?.autoCompleteSecurityType?.text.toString()
        )
        if (securityTypeIndex != args.wifiArgs.securityType)
            map["securityType"] = securityTypeIndex
        val connectionType = resources.getStringArray(R.array.wifi_connection_types).indexOf(
            binding?.autoCompleteConnectionType?.text.toString()
        )
        if (connectionType != args.wifiArgs.connectionType)
            map["connectionType"] = connectionType
        val routerLoginId = binding?.etLoginId?.text.toString().trim()
        if (routerLoginId != args.wifiArgs.routerLoginId)
            map["routerLoginId"] = routerLoginId
        val routerLoginPwd = binding?.etPassword?.text.toString().trim()
        if (routerLoginPwd != args.wifiArgs.routerLoginPassword)
            map["routerLoginPassword"] = routerLoginPwd
        val deviceLocation = binding?.etDeviceLocation?.text.toString().trim()
        if (deviceLocation != args.wifiArgs.deviceLocation)
            map["deviceLocation"] = deviceLocation
        val ipAddress = binding?.etIpAddress?.text.toString().trim()
        if (ipAddress != args.wifiArgs.ipAddress)
            map["ipAddress"] = ipAddress
        val broadcastSsid = binding?.broadcastSwitch?.isChecked as Boolean
        if (broadcastSsid != args.wifiArgs.visibility)
            map["visibility"] = broadcastSsid
        val permissions = binding?.etAccessible?.text.toString().trim()
        var updatePermission = permissions.split(",").map { it.trim() }
        if (permissions != DateConverters.convertArrayToString(args.wifiArgs.permission!!))
            map["permission"] = updatePermission.map { it.toInt() }.toList()
        @ServerTimestamp
        map["updatedAt"] = Timestamp.now()
        return map
    }

    private fun updateField() {
        val wifi = args.wifiArgs
        binding?.apply {
            this.etWifiName.setText(wifi.wifiName)
            this.etWifiPassword.setText(wifi.wifiPassword)
            this.autoCompleteSecurityType.setText(
                resources.getStringArray(R.array.wifi_security_modes)
                        [wifi.securityType], false
            )
            this.autoCompleteConnectionType.setText(
                resources.getStringArray(R.array.wifi_connection_types)
                        [wifi.connectionType], false
            )
            if (wifi.connectionType == 1) {
                this.layIpAddress.visibility = View.GONE
            }
            this.etIpAddress.setText(wifi.ipAddress)
            this.etLoginId.setText(wifi.routerLoginId)
            this.etPassword.setText(wifi.routerLoginPassword)
            this.etDeviceLocation.setText(wifi.deviceLocation)
            val data = if (args.wifiArgs.permission == null) {
                ""
            } else DateConverters.convertArrayToString(args.wifiArgs.permission!!)
            this.etAccessible.setText(data)
            this.broadcastSwitch.isChecked = wifi.visibility
        }
    }


    private fun intDropDowns() {
        val securityTypes = resources.getStringArray(R.array.wifi_security_modes)
        val wifiSecurityAdapter =
            ArrayAdapter(
                requireContext(), R.layout.dropdown_item,
                securityTypes as Array<out String>
            )
        binding?.autoCompleteSecurityType?.setAdapter(wifiSecurityAdapter)
        binding?.autoCompleteSecurityType?.setText(wifiSecurityAdapter.getItem(3), false)
        val connectionTypes = resources.getStringArray(R.array.wifi_connection_types)
        val connectionTypesAdapter =
            ArrayAdapter(
                requireContext(), R.layout.dropdown_item,
                connectionTypes as Array<out String>
            )
        binding?.autoCompleteConnectionType?.setAdapter(connectionTypesAdapter)
        binding?.autoCompleteConnectionType?.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, i, _ ->
                if (i == 1)
                    binding?.layIpAddress?.visibility = View.GONE
                else binding?.layIpAddress?.visibility = View.VISIBLE
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}