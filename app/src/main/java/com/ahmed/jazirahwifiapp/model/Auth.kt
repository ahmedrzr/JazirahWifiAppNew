package com.ahmed.jazirahwifiapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
 data class Auth(
  var email: String = "",
  var appPermissionCode: Int = 0,
  var wifiAccessCode: Int = 0,
  @field:JvmField
  var isEditViewAllowed: Boolean = false
 ):Parcelable
