package com.ahmed.jazirahwifiapp.model

import android.os.Parcelable
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppSetting(
    @field:JvmField
    val appPermission: Boolean = false,
    @field:JvmField
    val appPushNotificationPermission: Boolean = false,
    @get:Exclude var id: String = ""
) : Parcelable
