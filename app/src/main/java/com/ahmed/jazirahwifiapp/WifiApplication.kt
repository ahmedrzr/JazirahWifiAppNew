package com.ahmed.jazirahwifiapp

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.os.Build

class WifiApplication : Application() {

    fun hasInternetConnection(): Boolean {
        val connectivityManager = getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capability = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capability.hasTransport(TRANSPORT_WIFI) -> true
            capability.hasTransport(TRANSPORT_CELLULAR) -> true
            capability.hasTransport(TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}