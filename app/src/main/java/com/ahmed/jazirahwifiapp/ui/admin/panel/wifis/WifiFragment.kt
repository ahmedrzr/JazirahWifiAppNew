package com.ahmed.jazirahwifiapp.ui.admin.panel.wifis

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.adapters.WifiAdapter
import com.ahmed.jazirahwifiapp.databinding.FragmentWifiBinding
import com.ahmed.jazirahwifiapp.model.Wifi
import com.ahmed.jazirahwifiapp.ui.NotificationViewModel
import com.ahmed.jazirahwifiapp.ui.UiViewModel
import com.ahmed.jazirahwifiapp.ui.admin.panel.PanelViewModel
import com.ahmed.jazirahwifiapp.utils.Resource
import java.lang.Exception

private const val TAG = "WifiFragment"

class WifiFragment : Fragment() {
    private var _binding: FragmentWifiBinding? = null
    private val binding get() = _binding
    private lateinit var wifiViewModel: WifiViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var wifiAdapter: WifiAdapter
    private var wifiCollections = arrayListOf<Wifi>()
    private lateinit var uiViewModel: UiViewModel
    private var selectedWifiItemToUpdate: MutableMap<Int, Wifi> = mutableMapOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWifiBinding.inflate(inflater, container, false)
        wifiViewModel = ViewModelProvider(requireActivity()).get(WifiViewModel::class.java)
        uiViewModel = ViewModelProvider(requireActivity()).get(UiViewModel::class.java)
        notificationViewModel =
            ViewModelProvider(requireActivity()).get(NotificationViewModel::class.java)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        wifiViewModel.queryWifi()
        observers()
        binding?.apply {
            btnApply.setOnClickListener {
                if (etWifiAccessCodeAdmin.text.trim().toString().isNotEmpty()) {
                    val wifiAccessCode = etWifiAccessCodeAdmin.text.trim().toString().toInt()
                    wifiViewModel.updateWifiAccessCode(selectedWifiItemToUpdate, wifiAccessCode)
                }
            }
            cbToggleAll.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    updateAll(true)
                } else {
                    updateAll(false)
                }
            }
        }
    }

    private fun updateAll(isSelected: Boolean) {
        wifiCollections.forEachIndexed { index, wifi ->
            if (isSelected) {
                selectedWifiItemToUpdate[index] = wifi
                wifiCollections[index].isSelected = true
            } else {
                selectedWifiItemToUpdate.remove(index)
                wifiCollections[index].isSelected = false
            }

        }
        wifiAdapter.updateItemSelectedAll(wifiCollections)
    }

    private fun observers() {
        wifiViewModel.getWifiResult().observe(requireActivity(), Observer { it ->
            when (val response = it as Resource<*>) {
                is Resource.Success -> {
                    val data = response.data as MutableList<*>
                    wifiCollections.clear()
                    for (d in data) {
                        val wifi = d as Wifi
                        wifiCollections.add(wifi)
                    }
                    wifiAdapter.differ.submitList(wifiCollections)
                    wifiAdapter.notifyDataSetChanged()
                }
            }
        })
        uiViewModel.getInterComLiveData().observe(requireActivity(), Observer {
            if (it != null) {
                val documentId = it["documentId"].toString()
                val position = it["position"].toString().toInt()
                try {
                    if (wifiViewModel.isNetworkConnected()) {
                        val deletedItem = wifiCollections[position]
                        wifiCollections.removeAt(position)
                        wifiAdapter.differ.submitList(wifiCollections)
                        wifiAdapter.notifyItemRemoved(position)
                        wifiViewModel.setWifiToDelete(documentId, deletedItem.wifiName)
                    } else {
                        notificationViewModel.prepareMessage(
                            false,
                            true,
                            false,
                            requireContext().getString(R.string.no_internet)
                        )
                    }
                } catch (e: Exception) {
                    notificationViewModel.prepareMessage(false, true, false, e.message.toString())
                }
            }
        })
        wifiViewModel.getUpdatedWifiAccessCodeStats().observe(requireActivity(), Observer {
            when (val response = it as Resource<*>) {
                is Resource.Success -> {
                    binding?.apply {
                        this.lyModifyData.visibility = View.GONE
                        this.etWifiAccessCodeAdmin.clearFocus()
                        this.etWifiAccessCodeAdmin.text.clear()
                    }
                    wifiAdapter.updateChanges(selectedWifiItemToUpdate)
                    selectedWifiItemToUpdate.clear()
                }
            }
        })

    }

    private fun setUpRecyclerView() {
        wifiAdapter = WifiAdapter(true)
        binding?.rvWifi?.apply {
            adapter = wifiAdapter
            layoutManager = LinearLayoutManager(activity)
        }
        wifiAdapter.setOnItemEditListener { wifi, i ->
            val action = WifiFragmentDirections.actionNavWifiToWifiEditFragment(wifi)
            Navigation.findNavController(requireActivity().findViewById(R.id.nav_host_fragment_content_admin))
                .navigate(action)
        }
        wifiAdapter.setOnItemDeleteListener { wifi, i ->
            val action = WifiFragmentDirections.actionNavWifiToConfirmBottomSheetDialog2(wifi, i)
            Navigation.findNavController(requireActivity().findViewById(R.id.nav_host_fragment_content_admin))
                .navigate(action)
        }
        wifiAdapter.setOnItemShareListener {
            val sharingText = "${resources.getString(R.string.wi_fi_name)} :  ${it.wifiName},\n" +
                    "${resources.getString(R.string.wi_fi_password)} :  ${it.wifiPassword}, \n" +
                    "${resources.getString(R.string.security_type)} :  ${resources.getStringArray(R.array.wifi_security_modes)[it.securityType]}    "
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/html"
            i.putExtra(Intent.EXTRA_SUBJECT, "${it.wifiName} Wi-Fi Access Credentials")
            i.putExtra(Intent.EXTRA_TEXT, sharingText)
            startActivity(Intent.createChooser(i, "Share via"))
        }
        wifiAdapter.setOnItemClickListener { wifi, i ->
            val action = WifiFragmentDirections.actionNavWifiToDisplayFragment2(wifi, true)
            Navigation.findNavController(requireActivity().findViewById(R.id.nav_host_fragment_content_admin))
                .navigate(action)
        }
        wifiAdapter.setOnCheckBoxChecked { wifi, i ->
            selectedWifiItemToUpdate[i] = wifi
            binding?.apply {
                lyModifyData.visibility = View.VISIBLE
            }
        }
        wifiAdapter.setOnCheckBoxUnChecked { wifi, i ->
            selectedWifiItemToUpdate.remove(i)
            if (selectedWifiItemToUpdate.size == 0)
                binding?.apply {
                    lyModifyData.visibility = View.GONE
                }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}