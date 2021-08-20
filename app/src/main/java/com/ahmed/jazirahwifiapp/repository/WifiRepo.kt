package com.ahmed.jazirahwifiapp.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.WifiApplication
import com.ahmed.jazirahwifiapp.model.Wifi
import com.ahmed.jazirahwifiapp.utils.Constants
import com.ahmed.jazirahwifiapp.utils.DataMsgHandler
import com.ahmed.jazirahwifiapp.utils.Resource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WifiRepo(val application: Application) {
    private val TAG = "WifiRepo"
    private val wifiCollectionRef = Firebase.firestore.collection(Constants.DB_NAME)
    private val dataMsgHandler = DataMsgHandler()
    private var wifiCollections: MutableList<Wifi> = mutableListOf()
    private var wifiAccessCodeUpdateState: MutableLiveData<Resource<String>> = MutableLiveData()
    private var wifiEnrollmentData: MutableLiveData<Resource<String>> = MutableLiveData()
    private var wifiUpdateData: MutableLiveData<Resource<String>> = MutableLiveData()
    private var wifiDeletedData: MutableLiveData<Resource<String>> = MutableLiveData()

    fun getWifiCollections() = dataMsgHandler.getLiveDataHandler()
    fun subscribeToRealTimeData(appWifiAccessCode: Int) {
        dataMsgHandler.setLiveDataHandler(Resource.Loading(null))
        wifiCollectionRef.whereArrayContains("permission", appWifiAccessCode)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    dataMsgHandler.setLiveDataHandler(
                        Resource.Error(
                            error.message.toString(),
                            null
                        )
                    )
                    return@addSnapshotListener
                }
                wifiCollections.clear()
                value?.let {
                    try {
                        value.documents.mapTo(wifiCollections) {
                            val obj = it.toObject(Wifi::class.java)
                            obj!!.id = it.id
                            obj
                        }
                        dataMsgHandler.setLiveDataHandler(Resource.Success(wifiCollections))
                    } catch (e: Exception) {
                        dataMsgHandler.setLiveDataHandler(
                            Resource.Error(
                                e.message.toString(),
                                null
                            )
                        )
                    }
                }
            }
    }

    fun queryRealTimeData() {
        if ((application as WifiApplication).hasInternetConnection()) {
            dataMsgHandler.setLiveDataHandler(Resource.Loading(null))
            wifiCollectionRef
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        dataMsgHandler.setLiveDataHandler(
                            Resource.Error(
                                error.message.toString(),
                                null
                            )
                        )
                        return@addSnapshotListener
                    }
                    wifiCollections.clear()
                    value?.let {
                        try {
                            value.documents.mapTo(wifiCollections) {
                                val obj = it.toObject(Wifi::class.java)
                                obj!!.id = it.id
                                obj
                            }
                            dataMsgHandler.setLiveDataHandler(Resource.Success(wifiCollections))
                        } catch (e: Exception) {
                            dataMsgHandler.setLiveDataHandler(
                                Resource.Error(
                                    e.message.toString(),
                                    null
                                )
                            )
                        }
                    }
                }
        } else
            dataMsgHandler.setLiveDataHandler(
                Resource.Error(
                    application.getString(R.string.no_internet),
                    null
                )
            )
    }

    fun getDeletedWifiItemTrace() = wifiDeletedData
    fun deleteWifiFromFirebase(documentId: String, wifiName: String) {
        if ((application as WifiApplication).hasInternetConnection()) {
            CoroutineScope(Dispatchers.IO).launch {
                wifiDeletedData.postValue(Resource.Loading(null))
                try {
                    wifiCollectionRef.document(documentId).delete().await()
                    wifiDeletedData.postValue(Resource.Success(wifiName))

                } catch (e: Exception) {
                    wifiDeletedData.postValue(
                        Resource.Error(
                            e.message.toString(),
                            null
                        )
                    )
                }
            }
        } else
            wifiDeletedData.postValue(
                Resource.Error(
                    application.getString(R.string.no_internet),
                    null
                )
            )
    }

    // Enroll new wifi item
    fun getEnrolledStatus() = wifiEnrollmentData
    fun enrollWifi(map: Map<String, Any>) {
        if ((application as WifiApplication).hasInternetConnection()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    wifiEnrollmentData.postValue(Resource.Loading(null))
                    wifiCollectionRef.add(map).await()
                    val name = map["wifiName"]
                    wifiEnrollmentData.postValue(
                        Resource.Success(name.toString())
                    )
                } catch (e: Exception) {
                    wifiEnrollmentData.postValue(
                        Resource.Error(
                            e.message.toString(),
                            null
                        )
                    )
                }
            }
        } else wifiEnrollmentData.postValue(
            Resource.Error(
                application.getString(R.string.no_internet),
                null
            )
        )
    }

    //update wifi item
    fun getUpdatedWifiStatus() = wifiUpdateData
    fun updateWifiItem(documentId: String, map: Map<String, Any>, wifiName: String) {
        if ((application as WifiApplication).hasInternetConnection()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    wifiUpdateData.postValue(Resource.Loading(null))
                    wifiCollectionRef.document(documentId).update(map).await()
                    wifiUpdateData.postValue(
                        Resource.Success(wifiName)
                    )
                } catch (e: Exception) {
                    wifiUpdateData.postValue(
                        Resource.Error(
                            e.message.toString(),
                            null
                        )
                    )
                }
            }
        } else wifiUpdateData.postValue(
            Resource.Error(
                application.getString(R.string.no_internet),
                null
            )
        )
    }

    fun getWifiAccessCodeUpdateStatus() = wifiAccessCodeUpdateState
    fun updateWifiItemMultiple(list: Map<Int, Any>, wifiAccessCode: Int) {
        if ((application as WifiApplication).hasInternetConnection()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    wifiAccessCodeUpdateState.postValue(Resource.Loading(null))
                    for (map in list) {
                        val wifi = map.value as Wifi
                        wifiCollectionRef.document(wifi.id)
                            .update("permission", FieldValue.arrayUnion(wifiAccessCode))
                            .await()
                    }
                    queryRealTimeData()
                    wifiAccessCodeUpdateState.postValue(
                        Resource.Success(
                            "Updated"
                        )
                    )
                } catch (e: Exception) {
                    wifiAccessCodeUpdateState.postValue(
                        Resource.Error(
                            e.message.toString(),
                            null
                        )
                    )
                }
            }
        } else wifiAccessCodeUpdateState.postValue(
            Resource.Error(
                application.getString(R.string.no_internet),
                null
            )
        )
    }
}