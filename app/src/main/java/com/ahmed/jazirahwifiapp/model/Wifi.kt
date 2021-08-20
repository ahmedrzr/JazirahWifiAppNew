package com.ahmed.jazirahwifiapp.model


import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlin.collections.ArrayList

@Parcelize
data class Wifi(
    @SerializedName("wifi_Name")
    val wifiName: String = "",
    @SerializedName("wifi_Password")
    val wifiPassword: String? = "",
    @SerializedName("security_type")
    val securityType: Int = 3,
    @SerializedName("connection_type")
    val connectionType: Int = 0,
    @SerializedName("ip_address")
    val ipAddress: String = "0.0.0.0",
    @SerializedName("router_login_id")
    val routerLoginId: String = "",
    @SerializedName("router_login_password")
    val routerLoginPassword: String = "",
    @SerializedName("device_location")
    val deviceLocation: String = "",
    @SerializedName("visibility")
    val visibility: Boolean = false,
    @SerializedName("permission")
    var permission: ArrayList<Int>? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
    @get:Exclude var id: String = "",
    @get:Exclude var isSelected: Boolean = false
) : Parcelable {
}
