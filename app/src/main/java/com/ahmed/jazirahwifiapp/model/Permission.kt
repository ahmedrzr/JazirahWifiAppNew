package com.ahmed.jazirahwifiapp.model

import android.os.Parcelable
import android.text.BoringLayout
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Permission(
    var displayName:String = "",
    var email: String = "",
    @field:JvmField
    var appAccess: Boolean = false,
    var wifiAccessCode: Int = 0,
    @field:JvmField
    var isEditViewAllowed: Boolean = false,
    @field:JvmField
    var admin: Boolean = false,
    @get:Exclude var id: String = ""
) : Parcelable
