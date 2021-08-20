package com.ahmed.jazirahwifiapp.ui.main.display

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.databinding.DisplayFragmentBinding
import com.ahmed.jazirahwifiapp.ui.main.MainViewModel
import com.ahmed.jazirahwifiapp.utils.Constants
import com.ahmed.jazirahwifiapp.utils.DateConverters
import com.ahmed.jazirahwifiapp.utils.Resource
import com.ahmed.jazirahwifiapp.utils.UserPreferences
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.android.synthetic.main.display_fragment.*


class DisplayFragment : Fragment() {
    private val TAG = "DisplayFragment"
    private lateinit var mainViewModel: MainViewModel
    private var _binding: DisplayFragmentBinding? = null
    private val binding get() = _binding
    private val args by navArgs<DisplayFragmentArgs>()
    private var menu: Menu? = null
    private lateinit var displayViewModel: DisplayViewModel
    private var isEditViewAllowed: Boolean? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DisplayFragmentBinding.inflate(inflater, container, false)
        mainViewModel =
            ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        displayViewModel =
            ViewModelProvider(requireActivity()).get(DisplayViewModel::class.java)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = requireActivity().getString(
            R.string.details_wifi
        )
        val userPermissionPreference = UserPreferences.getUserPermissionPreference(requireContext())
        isEditViewAllowed =
            userPermissionPreference.getBoolean(Constants.APP_IS_EDIT_VIEW_ALLOWED, false)
        if (args.fromAdmin) {
            if (args.wifiArg.permission != null) {
                observers()
                displayViewModel.setUserByWifiAccessCode(args.wifiArg.permission!!)
            }
        }
        args.wifiArg.let {
            binding?.apply {
                this.tvWifiName.text = it.wifiName
                this.tvWifiPassword.text = it.wifiPassword
                this.tvSecurityType.text =
                    resources.getStringArray(R.array.wifi_security_modes)[it.securityType]
                this.tvConnectionType.text =
                    resources.getStringArray(R.array.wifi_connection_types)[it.connectionType]
                if (it.connectionType == 1) this.lyIpAddress.visibility = View.GONE
                this.tvIpAddress.text = it.ipAddress

                if (!isEditViewAllowed!!) {
                    this.tvLoginId.isEnabled = false
                    this.tvLoginPassword.isEnabled = false
                    this.tvLoginId.text = "*".repeat(it.routerLoginId.length)
                    this.tvLoginPassword.text = "*".repeat(it.routerLoginPassword.length)
                    this.cbLoginId.isEnabled = false
                    cbLoginPassword.isEnabled = false
                } else {
                    this.tvLoginId.text = it.routerLoginId
                    this.tvLoginPassword.text = it.routerLoginPassword
                }
                if (args.fromAdmin) {
                    this.tvLoginId.text = it.routerLoginId
                    this.tvLoginPassword.text = it.routerLoginPassword
                    this.tvLoginId.isEnabled = true
                    this.tvLoginPassword.isEnabled = true
                    this.cbLoginId.isEnabled = true
                    cbLoginPassword.isEnabled = true
                    lyAccessibility.visibility = View.VISIBLE

                }

                this.tvDeviceLocation.text = it.deviceLocation
                this.tvVisibility.text = it.visibility.toString()
                val timesStamp = if (it.updatedAt != null)
                    DateConverters.getTimeStampToLocal(it.updatedAt)
                else
                    String().orEmpty()
                this.tvUpdateAt?.text = timesStamp
                this.fab.setOnClickListener {
                    shareData()
                }
                this.fabCancel?.setOnClickListener {
                    visibilityGone()
                }
            }
        }
        visibilityGone()

    }

    private fun observers() {
        displayViewModel.getUserByWifiAccessCode().observe(requireActivity(), Observer {
            when (val response = it as Resource<*>) {
                is Resource.Error -> Log.e(TAG, "observers: ERROR : ${response.message}")
                is Resource.Loading -> Log.i(TAG, "observers: LOADING....")
                is Resource.Success -> {
                    val data = it.data as MutableList<String>
                    val converter = DateConverters.convertStringArrayToString(data)
                    binding?.apply {
                        lyAccessibility.visibility = View.VISIBLE
                        tvAccessibility.text = converter
                    }
                }
            }
        })
    }

    private fun visibilityGone() {
        binding?.apply {
            cbWifiName.visibility = View.GONE
            cbWifiPassword.visibility = View.GONE
            cbSecurityType.visibility = View.GONE
            cbConnectionType.visibility = View.GONE
            cbIpAddress.visibility = View.GONE
            cbLoginId.visibility = View.GONE
            cbLoginPassword.visibility = View.GONE
            cbDeviceLocation.visibility = View.GONE
            cbVisibility.visibility = View.GONE
            cbUpdateAt?.visibility = View.GONE
            fab.visibility = View.GONE
            fabCancel.visibility = View.GONE
        }
        menu?.findItem(R.id.action_share)?.isVisible = true
    }

    private fun visibilityNormal() {
        menu?.findItem(R.id.action_share)?.isVisible = false
        binding?.apply {
            cbWifiName.visibility = View.VISIBLE
            cbWifiName.isChecked = true
            cbWifiPassword.visibility = View.VISIBLE
            cbWifiPassword.isChecked = true
            cbSecurityType.visibility = View.VISIBLE
            cbSecurityType.isChecked = true
            cbConnectionType.visibility = View.VISIBLE
            cbConnectionType.isChecked = false
            cbIpAddress.visibility = View.VISIBLE
            cbIpAddress.isChecked = false
            cbLoginId.visibility = View.VISIBLE
            cbLoginId.isChecked = false
            cbLoginPassword.visibility = View.VISIBLE
            cbLoginPassword.isChecked = false
            cbDeviceLocation.visibility = View.VISIBLE
            cbDeviceLocation.isChecked = true
            cbVisibility.visibility = View.VISIBLE
            cbVisibility.isChecked = true
            cbUpdateAt.visibility = View.VISIBLE
            cbUpdateAt.isChecked = true
            fab.visibility = View.VISIBLE
            fabCancel.visibility = View.VISIBLE
        }
    }

    private fun prepareDataToShare(): String {
        binding.let {
            val sb = StringBuilder()
            if (cbWifiName.isChecked)
                sb.append(" ${binding?.wifiName?.text} : ${binding?.tvWifiName?.text},")
            if (cbWifiPassword.isChecked)
                sb.append(" ${binding?.wifiPassword?.text} : ${binding?.tvWifiPassword?.text},")
            if (cbSecurityType.isChecked)
                sb.append(" ${binding?.securityType?.text} : ${binding?.tvSecurityType?.text},")
            if (cbConnectionType.isChecked)
                sb.append(" ${binding?.connectionType?.text} : ${binding?.tvConnectionType?.text},")
            if (cbIpAddress.isChecked)
                sb.append(" ${binding?.ipAddress?.text} : ${binding?.tvIpAddress?.text},")
            if (cbLoginId.isChecked)
                sb.append(" ${binding?.loginId?.text} : ${binding?.tvLoginId?.text},")
            if (cbLoginPassword.isChecked)
                sb.append(" ${binding?.loginPassword?.text} : ${binding?.tvLoginPassword?.text},")
            if (cbDeviceLocation.isChecked)
                sb.append(" ${binding?.deviceLocation?.text} : ${binding?.tvDeviceLocation?.text},")
            if (cbVisibility.isChecked)
                sb.append(" ${binding?.visibility?.text} : ${binding?.tvVisibility?.text},")
            if (cbUpdateAt.isChecked)
                sb.append(" ${binding?.updateAt?.text} : ${binding?.tvUpdateAt?.text},")
            return sb.toString()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
//                val action = DisplayFragmentDirections.actionDisplayFragmentPop()
//                Navigation.findNavController(requireActivity().findViewById(R.id.nav_host_fragment_activity_main))
//                    .navigate(action)
                return true
            }
            R.id.action_share -> {
                visibilityNormal()
                binding?.apply {
                    fab.visibility = View.VISIBLE
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareData() {
        val sharingText = prepareDataToShare()
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/html"
        i.putExtra(
            Intent.EXTRA_SUBJECT,
            "${args.wifiArg!!.wifiName} Wi-Fi Access Credentials"
        )
        i.putExtra(Intent.EXTRA_TEXT, sharingText)
        startActivity(Intent.createChooser(i, "Share via"))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_menu, menu)
        this.menu = menu
        super.onCreateOptionsMenu(menu, inflater)
    }

}


