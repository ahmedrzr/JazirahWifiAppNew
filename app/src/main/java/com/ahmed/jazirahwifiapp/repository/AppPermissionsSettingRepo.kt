package com.ahmed.jazirahwifiapp.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.WifiApplication
import com.ahmed.jazirahwifiapp.model.AppSetting
import com.ahmed.jazirahwifiapp.utils.Constants.APP_PERMISSION_CODE
import com.ahmed.jazirahwifiapp.utils.Constants.DB_APP_ACCESS_SETTINGS
import com.ahmed.jazirahwifiapp.utils.DataMsgHandler
import com.ahmed.jazirahwifiapp.utils.Resource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AppPermissionsSettingRepo(val application: Application) {
    private val firebaseAppSettingsRef = Firebase.firestore.collection(DB_APP_ACCESS_SETTINGS)
    private val updatedAppSettings: MutableLiveData<Resource<String>> = MutableLiveData()
    private val dataMsgHandler = DataMsgHandler()

    fun getUpdatedAppPermissionStatus() = updatedAppSettings
    fun updateAppPermissionSettings(data: Map<String, Any>, documentId: String) =
        CoroutineScope(Dispatchers.IO).launch {
            if ((application as WifiApplication).hasInternetConnection()) {

                try {
                    updatedAppSettings.postValue(Resource.Loading(null))
                    firebaseAppSettingsRef.document(documentId).update(data).await()
                    updatedAppSettings.postValue(Resource.Success(application.getString(R.string.app_setting_setting_update)))
                    fetchAppPermissionSettings()
                } catch (e: Exception) {
                    updatedAppSettings.postValue(Resource.Error(e.message.toString(), null))
                }
            } else updatedAppSettings.postValue(Resource.Success(application.getString(R.string.no_internet)))
        }

    fun getAppPermissions() = dataMsgHandler.getLiveDataHandler()
    fun fetchAppPermissionSettings() =
        CoroutineScope(Dispatchers.IO).launch {
            if ((application as WifiApplication).hasInternetConnection()) {
                try {
                    dataMsgHandler.setLiveDataHandler(Resource.Loading(null))
                    val query = firebaseAppSettingsRef.get().await()
                    var obj: AppSetting? = null
                    query?.let {
                        obj = it.documents[0].toObject(AppSetting::class.java)
                        obj!!.id = it.documents[0].id
                    }
                    dataMsgHandler.setLiveDataHandler(Resource.Success(obj))
                } catch (e: Exception) {
                    dataMsgHandler.setLiveDataHandler(Resource.Error(e.message.toString(), null))
                }
            } else {
                dataMsgHandler.setLiveDataHandler(
                    Resource.Error(application.getString(R.string.no_internet), null)
                )
            }
        }
    fun fetchAppPermissionSettingsRealTime() {
        if ((application as WifiApplication).hasInternetConnection()) {
            dataMsgHandler.setLiveDataHandler(Resource.Loading(null))
            firebaseAppSettingsRef.addSnapshotListener { value, error ->
                if (error != null) {
                    dataMsgHandler.setLiveDataHandler(Resource.Error(error.message.toString(), null))
                    return@addSnapshotListener
                }
                var appSettingDoc:AppSetting? =null
                value?.let {
                    appSettingDoc = it.documents[0].toObject(AppSetting::class.java)
                    appSettingDoc?.id   = it.documents[0].id

                }
                dataMsgHandler.setLiveDataHandler(Resource.Success(appSettingDoc))
            }
        }else dataMsgHandler.setLiveDataHandler(
            Resource.Error(application.getString(R.string.no_internet), null)
        )
    }
}