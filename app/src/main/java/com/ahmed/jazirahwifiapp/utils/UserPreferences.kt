package com.ahmed.jazirahwifiapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.ahmed.jazirahwifiapp.model.Permission
import com.ahmed.jazirahwifiapp.utils.Constants.APP_ACCESS_ADMIN
import com.ahmed.jazirahwifiapp.utils.Constants.APP_AUTH_USER_DISPLAY_NAME
import com.ahmed.jazirahwifiapp.utils.Constants.APP_AUTH_USER_EMAIL
import com.ahmed.jazirahwifiapp.utils.Constants.APP_IS_EDIT_VIEW_ALLOWED
import com.ahmed.jazirahwifiapp.utils.Constants.APP_PERMISSION
import com.ahmed.jazirahwifiapp.utils.Constants.APP_PUSH_NOTIFICATION_PERMISSION
import com.ahmed.jazirahwifiapp.utils.Constants.APP_USER_PERMISSION
import com.ahmed.jazirahwifiapp.utils.Constants.APP_WIFI_ACCESS_CODE
import com.ahmed.jazirahwifiapp.utils.Constants.SHARED_PREFERENCE_APP
import com.ahmed.jazirahwifiapp.utils.Constants.SHARED_PREFERENCE_AUTH_USER
import com.ahmed.jazirahwifiapp.utils.Constants.SHARED_PREFERENCE_USER_PERMISSION
import com.google.firebase.auth.FirebaseUser

object UserPreferences {
    private lateinit var userAuthPreferences: SharedPreferences
    private lateinit var appPermissionPreferences: SharedPreferences
    private lateinit var userPermissionPreferences: SharedPreferences
    fun userAuthData(
        context: Context, authUser: FirebaseUser
    ) {
        userAuthPreferences =
            context.getSharedPreferences(SHARED_PREFERENCE_AUTH_USER, Context.MODE_PRIVATE)
        val editor = userAuthPreferences.edit()
        editor.clear()
        editor.putString(APP_AUTH_USER_EMAIL, authUser.email)
        editor.putString(APP_AUTH_USER_DISPLAY_NAME, authUser.displayName)
        editor.apply()
    }

    fun appPermissionData(
        context: Context, appPermission: Boolean, appPushNotificationPermission: Boolean
    ) {
        appPermissionPreferences =
            context.getSharedPreferences(SHARED_PREFERENCE_APP, Context.MODE_PRIVATE)
        val editor = appPermissionPreferences.edit()
        editor.clear()
        editor.putBoolean(APP_PERMISSION, appPermission)
        editor.putBoolean(APP_PUSH_NOTIFICATION_PERMISSION, appPushNotificationPermission)
        editor.apply()
    }

    fun userPermissionData(
        context: Context, permission: Permission
    ) {
        userPermissionPreferences =
            context.getSharedPreferences(SHARED_PREFERENCE_USER_PERMISSION, Context.MODE_PRIVATE)
        val editor = userPermissionPreferences.edit()
        editor.clear()
        editor.putBoolean(APP_USER_PERMISSION, permission.appAccess)
        editor.putInt(APP_WIFI_ACCESS_CODE, permission.wifiAccessCode)
        editor.putBoolean(APP_IS_EDIT_VIEW_ALLOWED, permission.isEditViewAllowed)
        editor.putBoolean(APP_ACCESS_ADMIN, permission.admin)
        editor.apply()
    }

    fun getAuthUserPreference(context: Context): SharedPreferences =
        context.getSharedPreferences(
            Constants.SHARED_PREFERENCE_AUTH_USER,
            Context.MODE_PRIVATE
        )

    fun getAppPermissionPreference(context: Context): SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFERENCE_APP, Context.MODE_PRIVATE)

    fun getUserPermissionPreference(context: Context): SharedPreferences =
        context.getSharedPreferences(
            Constants.SHARED_PREFERENCE_USER_PERMISSION,
            Context.MODE_PRIVATE
        )

}
