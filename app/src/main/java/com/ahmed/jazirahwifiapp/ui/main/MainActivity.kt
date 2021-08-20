package com.ahmed.jazirahwifiapp.ui.main

import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.databinding.ActivityMainBinding
import com.ahmed.jazirahwifiapp.model.AppSetting
import com.ahmed.jazirahwifiapp.model.NotificationData
import com.ahmed.jazirahwifiapp.model.Permission
import com.ahmed.jazirahwifiapp.model.PushNotification
import com.ahmed.jazirahwifiapp.network.ConnectivityListener
import com.ahmed.jazirahwifiapp.network.RetrofitInstance
import com.ahmed.jazirahwifiapp.services.FirebaseService
import com.ahmed.jazirahwifiapp.ui.ConnectionLiveData
import com.ahmed.jazirahwifiapp.ui.NotificationViewModel
import com.ahmed.jazirahwifiapp.ui.admin.panel.PanelActivity
import com.ahmed.jazirahwifiapp.ui.main.dialogs.SignOutConfirmViewModel
import com.ahmed.jazirahwifiapp.ui.main.enrollment.EnrollmentViewModel
import com.ahmed.jazirahwifiapp.ui.main.home.HomeViewModel
import com.ahmed.jazirahwifiapp.ui.main.profile.ProfileViewModel
import com.ahmed.jazirahwifiapp.utils.Constants
import com.ahmed.jazirahwifiapp.utils.Constants.APP_PUSH_NOTIFICATION_PERMISSION
import com.ahmed.jazirahwifiapp.utils.Constants.SUBSCRIPTION_TOPIC_ENROLLMENT_EVENTS
import com.ahmed.jazirahwifiapp.utils.Resource
import com.ahmed.jazirahwifiapp.utils.UserPreferences
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.layout_permission_denied.*
import kotlinx.android.synthetic.main.layout_progress_bar.*
import kotlinx.android.synthetic.main.layout_progress_bar.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivityLog"
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var enrollmentViewModel: EnrollmentViewModel
    private lateinit var signOutConfirmViewModel: SignOutConfirmViewModel
    private lateinit var notificationViewModel: NotificationViewModel
    private var defaultAppSettings: AppSetting? = null
    private lateinit var navView: BottomNavigationView
    private var userPreference: SharedPreferences? = null
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val connectionLiveData = ConnectionLiveData(this)
        connectionLiveData.observe(this, Observer { isConnected ->
//            if (!isConnected)
//                binding.apply {
//                    this.lyNetwork.visibility = View.VISIBLE
//                }
//            else
//                binding.apply {
//                    this.lyNetwork.visibility = View.GONE}
        })

        mainViewModel =
            ViewModelProvider(this).get(MainViewModel::class.java)
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        homeViewModel =
            ViewModelProvider(this).get((HomeViewModel::class.java))
        enrollmentViewModel = ViewModelProvider(this).get(EnrollmentViewModel::class.java)
        signOutConfirmViewModel =
            ViewModelProvider(this).get(SignOutConfirmViewModel::class.java)
        notificationViewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        userPreference = UserPreferences.getAuthUserPreference(this)
        if (mainViewModel.isNetworkConnected()) {
            defaultObserver()
            mainViewModel.fetchAppPermissionSettings()
        } else {
            loadDefaultBehaviourOffline()
        }
        FirebaseMessaging.getInstance().subscribeToTopic(SUBSCRIPTION_TOPIC_ENROLLMENT_EVENTS)

    }

    private fun defaultObserver() {
        mainViewModel.getAppPermissionSettings().observe(this, Observer {
            when (val response = it as Resource<*>) {
                is Resource.Error -> {
                    showOnError()
                    notificationViewModel.prepareMessage(
                        true, false, false, response.message.toString()
                    )
                }
                is Resource.Loading -> showOnLoading()
                is Resource.Success -> {
                    showOnSuccess()
                    defaultAppSettings = response.data as AppSetting
                    UserPreferences.appPermissionData(
                        this,
                        defaultAppSettings!!.appPermission,
                        defaultAppSettings!!.appPushNotificationPermission
                    )
                    loadDefaultBehaviourOnline()

                }
            }
        })
    }

    private fun loadDefaultBehaviourOnline() {
        if (defaultAppSettings?.appPermission == true)
            appPermissionGranted()
        else
            appPermissionDenied()

    }

    private fun loadDefaultBehaviourOffline() {
        val appPermissionPreference = UserPreferences.getAppPermissionPreference(this)
        val appPermission = appPermissionPreference.getBoolean(Constants.APP_PERMISSION, false)
        if (appPermission)
            appPermissionGranted()
        else
            appPermissionDenied()

    }

    private fun appPermissionGranted() {
        val email = userPreference?.getString(Constants.APP_AUTH_USER_EMAIL, "")
        if (email != null) {
            if (email.isNotEmpty()) {
                if (mainViewModel.isNetworkConnected())
                    mainViewModel.queryUserPermission(email)
                else {
                    processNow()
                }
            } else notificationViewModel
                .prepareMessage(false, true, false, "User email not found")
            dataObserverBefore()
        }

    }

    private fun appPermissionDenied() {
        supportActionBar?.title = "Access Denied"
        binding.apply {
            lyPermissionDenied.visibility = View.VISIBLE
            Glide.with(this@MainActivity)
                .load(R.drawable.giphy)
                .into(gifImgViewFront)
            tvPermissionMsg.text = getString(R.string.access_dinied)
        }
    }

    private fun processNow() {
        val permissionsPreference = UserPreferences.getUserPermissionPreference(this)
        val appUserAccess = permissionsPreference.getBoolean(Constants.APP_USER_PERMISSION, false)
        if (appUserAccess)
            permissionGranted()
        else permissionDenied()
    }

    private fun permissionDenied() {
        supportActionBar?.title = getString(R.string.approval_required)
        binding.apply {
            lyPermissionDenied.visibility = View.VISIBLE
            Glide.with(this@MainActivity)
                .load(R.drawable.giphy)
                .into(gifImgViewFront)
            tvPermissionMsg.text = getString(R.string.contact_system_administrator)
        }
    }

    private fun permissionGranted() {
        Log.i(TAG, "permissionGranted: ")
        binding.apply {
            container.visibility = View.VISIBLE
            lyPermissionDenied.visibility = View.GONE
        }
        navView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_enroll, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController!!, appBarConfiguration)
        navView.setupWithNavController(navController!!)
        dataObserverAfter()

    }

    private fun dataObserverBefore() {
        mainViewModel.getQueryUserPermission().observe(this, Observer {
            when (val response = it as Resource<Permission>) {
                is Resource.Error -> {
                    showOnError()
                    notificationViewModel.prepareMessage(
                        true,
                        false,
                        false,
                        response.message.toString()
                    )
                }
                is Resource.Loading -> showOnLoading()
                is Resource.Success -> {
                    showOnSuccess()
                    val permission = response.data as Permission
                    UserPreferences.userPermissionData(this, permission)
                    processNow()
                }
            }
        })
    }

    private fun dataObserverAfter() {
        notificationViewModel.getNotify().observe(this, Observer { map ->
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            notificationViewModel.showNotification(
                navController,
                R.id.notificationDialogFragment,
                map
            )
        })
        // fetch wifi items
        homeViewModel.getWifiCollections().observe(this, Observer {
            when (val response = it as Resource<*>) {
                is Resource.Error -> {
                    showOnError()
                    notificationViewModel.prepareMessage(
                        true,
                        false,
                        false,
                        response.message.toString()
                    )
                }
                is Resource.Loading -> showOnLoading()
                is Resource.Success -> {
                    showOnSuccess()
                    val data = response.data as MutableList<*>
                    homeBadgeDisplay(data.size)
                }
            }
        })
        //Add wifi item
        mainViewModel.getEnrollWifiStatus()
            .observe(this, Observer { it ->
                when (val response = it as Resource<*>) {
                    is Resource.Error -> {
                        showOnError()
                        notificationViewModel.prepareMessage(
                            true,
                            false,
                            false,
                            response.message.toString()
                        )
                    }
                    is Resource.Loading -> showOnLoading()
                    is Resource.Success -> {
                        showOnSuccess()
                        val message = response.data as String
                        val sb = StringBuilder().append(message).append(" added successfully")
                        notificationViewModel.prepareMessage(false, false, true, sb.toString())
                        val permissionsPreference =
                            UserPreferences.getAppPermissionPreference(this)
                        if (permissionsPreference.getBoolean(
                                APP_PUSH_NOTIFICATION_PERMISSION,
                                false
                            )
                        ) {
                            val pushMessage =
                                "New Wifi Item Added by " + mainViewModel.getAuthUserDisplayName
                            val pushDescription = "location"
                            PushNotification(
                                NotificationData(message, pushMessage, pushDescription),
                                SUBSCRIPTION_TOPIC_ENROLLMENT_EVENTS
                            ).also {
                                sendNotifications(it)
                            }
                        }

                    }
                }
            })
        mainViewModel.getWifiUpdateStatus().observe(this, Observer {
            when (val response = it as Resource<*>) {
                is Resource.Error -> {
                    showOnError()
                    notificationViewModel.prepareMessage(
                        true,
                        false,
                        false,
                        response.message.toString()
                    )
                }
                is Resource.Loading -> showOnLoading()
                is Resource.Success -> {
                    showOnSuccess()
                    val message = response.data as String
                    val sb = StringBuilder().append(message).append(" updated successfully")
                    notificationViewModel.prepareMessage(false, false, true, sb.toString())
                    val permissionsPreference = UserPreferences.getAppPermissionPreference(this)
                    if (permissionsPreference.getBoolean(APP_PUSH_NOTIFICATION_PERMISSION, false)) {
                        val pushMessage =
                            "Wifi Item modify by " + mainViewModel.getAuthUserDisplayName
                        val pushDescription = "location"
                        PushNotification(
                            NotificationData(message, pushMessage, pushDescription),
                            SUBSCRIPTION_TOPIC_ENROLLMENT_EVENTS
                        ).also {
                            sendNotifications(it)
                        }

                    }

                }
            }
        })
        homeViewModel.getDeletedItemTrace().observe(this, Observer {
            when (val response = it as Resource<*>) {
                is Resource.Error -> {
                    showOnError()
                    notificationViewModel.prepareMessage(
                        true,
                        false,
                        false,
                        response.message.toString()
                    )
                }
                is Resource.Loading -> showOnLoading()
                is Resource.Success -> {
                    showOnSuccess()
                    val message = response.data as String
                    val permissionsPreference = UserPreferences.getAppPermissionPreference(this)
                    if (permissionsPreference.getBoolean(APP_PUSH_NOTIFICATION_PERMISSION, false)) {
                        val pushMessage =
                            "Wifi Item deleted by " + mainViewModel.getAuthUserDisplayName
                        val pushDescription = "location"
                        PushNotification(
                            NotificationData(message, pushMessage, pushDescription),
                            SUBSCRIPTION_TOPIC_ENROLLMENT_EVENTS
                        ).also {
                            sendNotifications(it)
                        }

                    }
                }
            }
        })

        profileViewModel.getAuthUserData().observe(this, Observer {
            when (val response = it as Resource<*>) {
                is Resource.Success -> {
                    showOnSuccess()
                    UserPreferences.userAuthData(this, response.data as FirebaseUser)
                }
                is Resource.Error -> {
                    showOnError()
                    notificationViewModel.prepareMessage(
                        true,
                        false,
                        false,
                        response.message.toString()
                    )
                }
                is Resource.Loading -> showOnLoading()
            }
        })
    }


    private fun showProgressBar(isToShow: Boolean) {
        if (isToShow) {
            binding.apply { lyProgressbar.visibility = View.VISIBLE }
        } else {
            binding.apply { lyProgressbar.visibility = View.GONE }
        }
    }

    private fun homeBadgeDisplay(count: Int) {
        navView.getOrCreateBadge(R.id.navigation_home).number = count
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val appPermissionPreferences = UserPreferences.getAppPermissionPreference(this)
        val userPermissionPreferences = UserPreferences.getUserPermissionPreference(this)
        val appPermission = appPermissionPreferences.getBoolean(Constants.APP_PERMISSION, false)
        val userPermission =
            userPermissionPreferences.getBoolean(Constants.APP_USER_PERMISSION, false)
        if (!appPermission || !userPermission) {
            menuInflater.inflate(R.menu.menu_app_denied, menu)
        } else {
            menuInflater.inflate(R.menu.main, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_out -> {
                navigateToConfirmSignOut()
            }
            R.id.action_admin -> {
                navigateToAdminPanel()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToAdminPanel() {
        val permissionPreference = UserPreferences.getUserPermissionPreference(this)
        val adminAccess = permissionPreference.getBoolean(Constants.APP_ACCESS_ADMIN, false)
        if (adminAccess) {
            Intent(this@MainActivity, PanelActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.from_left, R.anim.to_right)
            }
        } else {
            navController = findNavController(R.id.nav_host_fragment_activity_main)
            navController!!.navigate(R.id.adminPrivilegeFragment)
        }
    }

    private fun navigateToConfirmSignOut() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.signOutBottomSheetFragment)
    }

    private fun showOnLoading() {
        showProgressBar(true)
    }

    private fun showOnError() {
        showProgressBar(false)
    }

    private fun showOnSuccess() {
        showProgressBar(false)
    }


    override fun onDestroy() {
        super.onDestroy()

    }


    private fun sendNotifications(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (!response.isSuccessful) {
                    val map = mutableMapOf<String, Any>()
                    map["isError"] = true
                    map["message"] = response.message()
                    notificationViewModel.setNotify(map)
                } else Log.i(TAG, "sendNotifications: SENT")
            } catch (e: Exception) {
                val map = mutableMapOf<String, Any>()
                map["isError"] = true
                map["message"] = e.message.toString()
                notificationViewModel.setNotify(map)
            }
        }

}