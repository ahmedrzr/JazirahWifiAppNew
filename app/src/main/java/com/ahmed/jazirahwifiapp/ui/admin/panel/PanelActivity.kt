package com.ahmed.jazirahwifiapp.ui.admin.panel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ahmed.jazirahwifiapp.databinding.ActivityPanelBinding
import com.ahmed.jazirahwifiapp.model.AppSetting
import com.ahmed.jazirahwifiapp.model.Permission
import com.ahmed.jazirahwifiapp.ui.admin.panel.app.AppPermissionViewModel
import com.ahmed.jazirahwifiapp.ui.admin.panel.edit.AppUserPermissionViewModel
import com.ahmed.jazirahwifiapp.ui.admin.panel.edit.WifiEditViewModel
import com.ahmed.jazirahwifiapp.ui.admin.panel.users.UsersViewModel
import com.ahmed.jazirahwifiapp.ui.admin.panel.wifis.WifiViewModel
import com.ahmed.jazirahwifiapp.utils.Resource
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import io.grpc.internal.LogExceptionRunnable
import kotlinx.android.synthetic.main.layout_progress_bar.view.*
import kotlinx.android.synthetic.main.nav_header_admin.*
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.ui.NavigationUI
import androidx.transition.Explode
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.ui.NotificationViewModel
import com.ahmed.jazirahwifiapp.ui.login.LoginActivity
import com.ahmed.jazirahwifiapp.ui.main.MainActivity
import com.ahmed.jazirahwifiapp.utils.Constants
import com.ahmed.jazirahwifiapp.utils.UserPreferences
import kotlinx.android.synthetic.main.nav_header_admin.view.*



class PanelActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val TAG = "PanelActivityLog"
    private var _binding: ActivityPanelBinding? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val binding get() = _binding
    private lateinit var appPermissionViewModel: AppPermissionViewModel
    private lateinit var panelViewModel: PanelViewModel
    private lateinit var wifiPermissionViewModel: UsersViewModel
    private lateinit var wifiViewModel: WifiViewModel
    private lateinit var appUserPermissionViewModel: AppUserPermissionViewModel
    private lateinit var wifiEditViewModel: WifiEditViewModel
    private lateinit var notificationViewModel: NotificationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPanelBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.appBarAdmin?.toolbar)
        val drawerLayout: DrawerLayout? = binding?.drawerLayout
        val navView: NavigationView? = binding?.navView
        navView?.setNavigationItemSelectedListener(this@PanelActivity)
        val navController = findNavController(R.id.nav_host_fragment_content_admin)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_app, R.id.nav_user, R.id.nav_wifi, R.id.nav_sign_out
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView?.setupWithNavController(navController)

        appPermissionViewModel = ViewModelProvider(this).get(AppPermissionViewModel::class.java)
        panelViewModel = ViewModelProvider(this).get(PanelViewModel::class.java)
        wifiPermissionViewModel = ViewModelProvider(this).get(UsersViewModel::class.java)
        wifiViewModel = ViewModelProvider(this).get(WifiViewModel::class.java)
        appUserPermissionViewModel =
            ViewModelProvider(this).get(AppUserPermissionViewModel::class.java)
        wifiEditViewModel = ViewModelProvider(this).get(WifiEditViewModel::class.java)
        notificationViewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)

        navView?.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Intent(this, MainActivity::class.java).also {
                        startActivity(it)
                        finish()
                        overridePendingTransition(R.anim.from_right, R.anim.to_left);
                    }
                }
                R.id.nav_app -> {
                    navController.navigate(R.id.nav_app)
                }
                R.id.nav_user -> {
                    navController.navigate(R.id.nav_user)
                }
                R.id.nav_wifi -> {
                    navController.navigate(R.id.nav_wifi)
                }
                R.id.nav_sign_out -> {
                    navController.navigate(R.id.signOutBottomSheetFragment2)
                }
            }
            drawerLayout?.closeDrawer(GravityCompat.START)
            true
        }
        updateProfileData(navView)
        observers()
    }

    private fun updateProfileData(navView: NavigationView?) {
        val userPreference = UserPreferences.getAuthUserPreference(this)
        val displayName = userPreference.getString(Constants.APP_AUTH_USER_DISPLAY_NAME, "")
        val email = userPreference.getString(Constants.APP_AUTH_USER_EMAIL, "")
        binding.apply {
            navView?.getHeaderView(0)?.navDisplayName?.text = displayName
            navView?.getHeaderView(0)?.navEmail?.text = email
        }
    }


    private fun observers() {
        notificationViewModel.getNotify().observe(this, Observer { map ->
            val navController = findNavController(R.id.nav_host_fragment_content_admin)
            notificationViewModel.showNotification(
                navController,
                R.id.notificationDialogFragment2,
                map
            )
        })
        appPermissionViewModel.getAppPermissionSettings().observe(this, Observer {
            when (val response = it as Resource<*>) {
                is Resource.Loading -> showOnLoading()
                is Resource.Error -> {
                    showOnError()
                    notificationViewModel.prepareMessage(
                        true,
                        false,
                        false,
                        response.message.toString()
                    )
                }
                is Resource.Success -> {
                    showOnSuccess()
                }
            }
        })
        appPermissionViewModel.getUpdatedAppPermissionStatus().observe(this, Observer {
            when (val response = it as Resource<*>) {
                is Resource.Loading -> showOnLoading()
                is Resource.Error -> {
                    showOnError()
                    notificationViewModel.prepareMessage(
                        true, false, false, response.message.toString()
                    )
                }
                is Resource.Success -> {
                    showOnSuccess()
                    notificationViewModel.prepareMessage(
                        false, false, true, response.data.toString()
                    )
                }
            }
        })
        wifiPermissionViewModel.getUsers().observe(this, Observer { response ->
            when (response) {
                is Resource.Loading -> showOnLoading()
                is Resource.Error -> {
                    showOnError()
                    notificationViewModel.prepareMessage(
                        true, false, false, response.message.toString()
                    )
                }
                is Resource.Success -> {
                    showOnSuccess()
                }
            }
        })
        appUserPermissionViewModel.getUpdatePermissionStatus().observe(this, Observer {
            when (val response = it as Resource<*>) {
                is Resource.Loading -> showOnLoading()
                is Resource.Error -> {
                    showOnError()
                    notificationViewModel.prepareMessage(
                        true, false, false, response.message.toString()
                    )
                }
                is Resource.Success -> {
                    showOnSuccess()
                    notificationViewModel.prepareMessage(
                        false, false, true, response.data.toString()
                    )
                }
            }
        })
        wifiEditViewModel.getWifiUpdateStatus().observe(this, Observer {
            when (val response = it as Resource<*>) {
                is Resource.Loading -> showOnLoading()
                is Resource.Error -> {
                    showOnError()
                    notificationViewModel.prepareMessage(
                        true, false, false, response.message.toString()
                    )
                }
                is Resource.Success -> {
                    showOnSuccess()
                    notificationViewModel.prepareMessage(
                        false, false, true, response.data.toString()
                    )
                }
            }
        })
        wifiViewModel.getWifiDeletedStatus().observe(this, Observer {
            when (val response = it as Resource<*>) {
                is Resource.Loading -> showOnLoading()
                is Resource.Error -> {
                    showOnError()
                    notificationViewModel.prepareMessage(
                        true, false, false, response.message.toString()
                    )
                }
                is Resource.Success -> {
                    showOnSuccess()
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_admin)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showProgressLoading(showLoading: Boolean) {
        if (showLoading)
            binding?.appBarAdmin?.lyContentAdmin?.topContainer?.lyProgressbar?.visibility =
                View.VISIBLE
        else binding?.appBarAdmin?.lyContentAdmin?.topContainer?.lyProgressbar?.visibility =
            View.GONE
    }

    private fun showOnLoading() {
        showProgressLoading(true)
    }

    private fun showOnError() {
        showProgressLoading(false)
    }

    private fun showOnSuccess() {
        showProgressLoading(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.from_right, R.anim.to_left)
    }
}