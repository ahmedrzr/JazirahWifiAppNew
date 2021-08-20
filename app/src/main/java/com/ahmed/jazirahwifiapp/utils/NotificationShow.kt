package com.ahmed.jazirahwifiapp.utils

import android.os.Bundle
import androidx.navigation.NavController

class NotificationShow {
    companion object {
        fun showNotification(
            navController: NavController,
            destinationId: Int,
            isError: Boolean,
            message: String
        ) {
            val bundle = Bundle().apply {
                this.putBoolean("isError", isError)
                this.putString("message", message)
            }
            navController.navigate(destinationId, bundle)
        }
    }
}