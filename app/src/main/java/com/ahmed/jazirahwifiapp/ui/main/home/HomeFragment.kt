package com.ahmed.jazirahwifiapp.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.adapters.WifiAdapter
import com.ahmed.jazirahwifiapp.model.Wifi
import android.content.Intent
import android.util.Log
import com.ahmed.jazirahwifiapp.databinding.FragmentHomeBinding
import com.ahmed.jazirahwifiapp.ui.NotificationViewModel
import com.ahmed.jazirahwifiapp.ui.UiViewModel
import com.ahmed.jazirahwifiapp.ui.main.MainViewModel
import com.ahmed.jazirahwifiapp.utils.Constants
import com.ahmed.jazirahwifiapp.utils.Resource
import com.ahmed.jazirahwifiapp.utils.UserPreferences
import com.google.android.material.transition.MaterialFade
import kotlinx.android.synthetic.main.recycler_empty_view.*
import java.lang.Exception


class HomeFragment : Fragment() {
    private val TAG = "HomeFragment"
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var uiViewModel: UiViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var wifiAdapter: WifiAdapter
    private var wifiCollections = arrayListOf<Wifi>()
    private val binding get() = _binding
    private var userWifiAccessCode: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFade()
        returnTransition = MaterialFade()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        uiViewModel = ViewModelProvider(requireActivity()).get(UiViewModel::class.java)
        notificationViewModel =
            ViewModelProvider(requireActivity()).get(NotificationViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated: ")
        setUpRecyclerView()
        observerData()
        val userPreferences = UserPreferences.getUserPermissionPreference(requireContext())
        userWifiAccessCode = userPreferences.getInt(Constants.APP_WIFI_ACCESS_CODE, -1)
        if (userWifiAccessCode!! > -1)
            homeViewModel.subscribeToWifiCollection(userWifiAccessCode!!)
    }

    private fun observerData() {
        //fetch wifi items
        homeViewModel.getWifiCollections().observe(requireActivity(), Observer {
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
                    setLayouts(wifiCollections.size)
                }
            }
        })
        uiViewModel.getInterComLiveData().observe(requireActivity(), Observer { map ->
            map?.let {
                val documentId = map["documentId"] as String
                val position = map["position"] as Int
                try {
                    if (homeViewModel.isNetworkConnected()) {
                        val deletedItem = wifiCollections[position]
                        wifiCollections.removeAt(position)
                        wifiAdapter.differ.submitList(wifiCollections)
                        wifiAdapter.notifyItemRemoved(position)
                        homeViewModel.deleteWifiItemFirestore(documentId, deletedItem.wifiName)
                    } else notificationViewModel.prepareMessage(
                        false,
                        true,
                        false,
                        requireContext().getString(R.string.no_internet)
                    )
                } catch (e: Exception) {
                    notificationViewModel.prepareMessage(
                        false,
                        true,
                        false,
                        e.message.toString()
                    )
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setUpRecyclerView() {
        wifiAdapter = WifiAdapter(false)
        binding?.apply {
            this.rvWifi.apply {
                this.adapter = wifiAdapter
                this.layoutManager = LinearLayoutManager(activity)
            }
        }
        wifiAdapter.setOnItemDeleteListener { it, pos ->
            val action =
                HomeFragmentDirections.actionNavigationHomeToConfirmBottomSheetDialog(it, pos)
            Navigation.findNavController(requireActivity().findViewById(R.id.nav_host_fragment_activity_main))
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
        wifiAdapter.setOnItemClickListener { it, pos ->
            val action = HomeFragmentDirections.actionNavigationHomeToDisplayFragment(it)
            Navigation.findNavController(requireActivity().findViewById(R.id.nav_host_fragment_activity_main))
                .navigate(action)
        }
        wifiAdapter.setOnItemEditListener { it, pos ->
            val action =
                HomeFragmentDirections.actionNavigationHomeToNavigationEnroll(it, pos, true)
            Navigation.findNavController(requireActivity().findViewById(R.id.nav_host_fragment_activity_main))
                .navigate(action)
        }
    }

    private fun setLayouts(count: Int) {
        binding?.apply {
            if (count == 0) {
                lyEmpty.visibility = View.VISIBLE
                rvWifi.visibility = View.GONE
            }
            if (count > 0) {
                lyEmpty.visibility = View.GONE
                rvWifi.visibility = View.VISIBLE
            }
        }
    }
}



