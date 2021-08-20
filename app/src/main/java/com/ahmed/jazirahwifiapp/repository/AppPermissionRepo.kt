package com.ahmed.jazirahwifiapp.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.WifiApplication
import com.ahmed.jazirahwifiapp.model.Permission
import com.ahmed.jazirahwifiapp.utils.Constants
import com.ahmed.jazirahwifiapp.utils.DataMsgHandler
import com.ahmed.jazirahwifiapp.utils.Resource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class AppPermissionRepo(val application: Application) {
    private val appPermissionRef = Firebase.firestore.collection(Constants.DB_AUTH)
    private val userPermissions: MutableLiveData<Resource<Permission>> = MutableLiveData()
    private val userPermissionsAll: MutableLiveData<Resource<MutableList<Permission>>> =
        MutableLiveData()
    private val appPermissionNotification: MutableLiveData<Map<String, Any>> = MutableLiveData()
    private val dataMsgHandler = DataMsgHandler()
    private val usersNameLiveData: MutableLiveData<Resource<ArrayList<String>>> = MutableLiveData()
    private var users: MutableList<Permission> = mutableListOf()

    fun getAppPermissions(): MutableLiveData<Resource<Permission>> = userPermissions
    fun queryUser(email: String) {
        if ((application as WifiApplication).hasInternetConnection()) {
            userPermissions.postValue(Resource.Loading(null))
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val query = appPermissionRef.whereEqualTo("email", email)
                        .limit(1)
                        .get()
                        .await()
                    if (query.size() > 0) {
                        query?.let {
                            for (document in it) {
                                val user = document.toObject(Permission::class.java)
                                userPermissions.postValue(Resource.Success(user))
                            }
                        }
                    } else
                        userPermissions.postValue(
                            Resource.Error(
                                application.getString(R.string.database_error_1),
                                null
                            )
                        )
                } catch (e: Exception) {
                    userPermissions.postValue(Resource.Error(e.message.toString(), null))
                }
            }
        } else userPermissions.postValue(
            Resource.Error(
                application.getString(R.string.no_internet),
                null
            )
        )
    }
    fun queryUserRealTime(email: String) {
        if ((application as WifiApplication).hasInternetConnection()) {
            userPermissions.postValue(Resource.Loading(null))
            appPermissionRef.whereEqualTo("email", email).limit(1)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        userPermissions.postValue(Resource.Error(error.message.toString(), null))
                    }
                    value?.let {
                        for (document in it) {
                            val user = document.toObject(Permission::class.java)
                            userPermissions.postValue(Resource.Success(user))
                        }
                    }
                }
        } else userPermissions.postValue(
            Resource.Error(
                application.getString(R.string.no_internet),
                null
            )
        )
    }
    fun getQueryUsers() = userPermissionsAll
    fun queryUsers() {
        if ((application as WifiApplication).hasInternetConnection()) {
            userPermissionsAll.postValue(Resource.Loading(null))
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val querySnapShot = appPermissionRef.get().await()
                    users.clear()
                    querySnapShot.let {
                        querySnapShot?.documents?.mapTo(users) {
                            val obj = it.toObject(Permission::class.java)
                            obj!!.id = it.id
                            obj
                        }
                    }
                    userPermissionsAll.postValue(Resource.Success(users))
                } catch (e: Exception) {
                    userPermissionsAll.postValue(Resource.Error(e.message.toString(), null))
                }
            }
        } else userPermissionsAll.postValue(
            Resource.Error(
                application.getString(R.string.no_internet),
                null
            )
        )
    }

    fun getDefaultPermissionResponse() = appPermissionNotification

    fun addToPermissionCollection(map: Map<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        if ((application as WifiApplication).hasInternetConnection()) {
            try {
                appPermissionRef.add(map).await()
                appPermissionNotification.postValue(dataMsgHandler.prepareMsg(false, "Success"))
            } catch (e: Exception) {
                appPermissionNotification.postValue(
                    dataMsgHandler.prepareMsg(
                        true,
                        e.message.toString()
                    )
                )
            }
        } else appPermissionNotification.postValue(
            dataMsgHandler.prepareMsg(
                true,
                application.getString(R.string.no_internet)
            )
        )
    }

    fun getPermissionUpdateStatus() = dataMsgHandler.getLiveDataHandler()
    fun updateAppPermissions(documentId: String, map: Map<String, Any>) {
        if ((application as WifiApplication).hasInternetConnection()) {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    dataMsgHandler.setLiveDataHandler(Resource.Loading(null))
                    appPermissionRef.document(documentId).update(map).await()
                    dataMsgHandler.setLiveDataHandler(Resource.Success("UPDATED"))
                }

            } catch (e: Exception) {
                dataMsgHandler.setLiveDataHandler(Resource.Error(e.message.toString(), null))
            }
        } else dataMsgHandler.setLiveDataHandler(
            Resource.Error(
                application.getString(R.string.no_internet),
                null
            )
        )
    }

    fun getUserByWifiAccessCode() = usersNameLiveData
    fun queryUserByWifiAccessCode(wifiAccessCodes: ArrayList<Int>) {
        if ((application as WifiApplication).hasInternetConnection()) {
            usersNameLiveData.postValue(Resource.Loading(null))

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val codeDisplayNames = ArrayList<String>()
                    for (code in wifiAccessCodes) {
                        val query = appPermissionRef.whereEqualTo("wifiAccessCode", code)
                            .limit(1)
                            .get()
                            .await()
                        if (query.size() > 0) {
                            query?.let {
                                for (document in it) {
                                    val user = document.toObject(Permission::class.java)
                                    codeDisplayNames.add(user.displayName)
                                }
                            }

                        } else
                            usersNameLiveData.postValue(
                                Resource.Error(
                                    application.getString(R.string.database_error_1),
                                    null
                                )
                            )
                    }
                    usersNameLiveData.postValue(Resource.Success(codeDisplayNames))

                } catch (e: Exception) {
                    usersNameLiveData.postValue(Resource.Error(e.message.toString(), null))
                }
            }
        } else userPermissions.postValue(
            Resource.Error(
                application.getString(R.string.no_internet),
                null
            )
        )
    }

}