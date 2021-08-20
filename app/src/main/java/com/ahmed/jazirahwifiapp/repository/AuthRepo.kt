package com.ahmed.jazirahwifiapp.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.WifiApplication
import com.ahmed.jazirahwifiapp.utils.DataMsgHandler
import com.ahmed.jazirahwifiapp.utils.Resource
import com.ahmed.jazirahwifiapp.utils.Validations
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthRepo(val application: Application) {
    private val TAG = "AuthRepo"
    private var firebaseAuth = FirebaseAuth.getInstance()
    private val authUserData: MutableLiveData<Resource<FirebaseUser?>> = MutableLiveData()
    private val authUserForgotPassword: MutableLiveData<Resource<String?>> = MutableLiveData()
    private val logInStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val dataMsgHandler = DataMsgHandler()


    init {
        if (firebaseAuth.currentUser != null) {
            logInStatus.postValue(true)
            Resource.Success(firebaseAuth.currentUser).let { authUserData.postValue(it) }
        }
    }

    // get auth user
    fun getAuthUser(): MutableLiveData<Resource<FirebaseUser?>> = authUserData

    fun getAuthRepoNotification() = dataMsgHandler.getUpdateUiNotifications()

    fun getForgotPasswordLinkResponse() = authUserForgotPassword

    // SignIn
    fun signInUser(email: String, password: String) {
        if ((application as WifiApplication).hasInternetConnection()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    authUserData.postValue(Resource.Loading(null))
                    val authUser =
                        firebaseAuth.signInWithEmailAndPassword(email.trim(), password.trim())
                            .await()
                    logInStatus.postValue(true)
                    Resource.Success(authUser.user).let { authUserData.postValue(it) }

                } catch (e: Exception) {
                    authUserData.postValue(
                        Resource.Error(
                            e.message.toString(),
                            null
                        )
                    )
                }
            }
        } else
            dataMsgHandler.setUpdateUiNotification(
                false,
                application.getString(R.string.no_internet)
            )
    }

    // Register new user
    fun signUpUser(displayName: String, email: String, password: String) {
        if ((application as WifiApplication).hasInternetConnection()) {
            authUserData.postValue(Resource.Loading(null))
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .await()
                    firebaseAuth.currentUser?.let { user ->
                        val profile = UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName)
                            .build()
                        user.updateProfile(profile).await()
                        logInStatus.postValue(true)
                        authUserData.postValue(Resource.Success(firebaseAuth.currentUser))
                    }
                } catch (e: Exception) {
                    authUserData.postValue(Resource.Error(e.message.toString(), null))
                }
            }
        } else {
            authUserData.postValue(
                Resource.Error(
                    application.getString(R.string.no_internet), null
                )
            )
        }
    }

    //Forgot Password
    fun sendResetPasswordLink(email: String) {

        if ((application as WifiApplication).hasInternetConnection()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    authUserForgotPassword.postValue(Resource.Loading(null))
                    firebaseAuth.sendPasswordResetEmail(email.trim()).await()
                    authUserForgotPassword.postValue(Resource.Success("SUCCESS"))

                } catch (e: Exception) {
                    authUserForgotPassword.postValue(
                        Resource.Error(
                            e.message.toString(),
                            null
                        )
                    )
                }
            }
        } else {
            authUserForgotPassword.postValue(
                Resource.Error(
                    application.getString(R.string.no_internet),
                    null
                )
            )
        }
    }

    //Update user profile
    fun updateAuthUserProfile(
        authEmail: String,
        authPwd: String,
        displayName: String,
        email: String,
        password: String
    ) {
        if ((application as WifiApplication).hasInternetConnection()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    authUserData.postValue(Resource.Loading(null))
                    val credential = EmailAuthProvider
                        .getCredential(authEmail, authPwd)
                    val profile = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()
                    firebaseAuth.currentUser?.reauthenticate(credential)?.await()
                    if (!(firebaseAuth.currentUser?.displayName.equals(displayName)))
                        firebaseAuth.currentUser?.updateProfile(profile)?.await()
                    if (!(firebaseAuth.currentUser?.email.equals(email)))
                        firebaseAuth.currentUser?.updateEmail(email)?.await()
                    if ((password.isNotEmpty()))
                        firebaseAuth.currentUser?.updatePassword(password)?.await()
                    Resource.Success(firebaseAuth.currentUser).let { authUserData.postValue(it) }
                } catch (e: Exception) {
                    authUserData.postValue(Resource.Error(e.message.toString(), null))
                }
            }
        } else dataMsgHandler.setUpdateUiNotification(
            false,
            application.getString(R.string.no_internet)
        )
    }

    // User Session Check
    fun isLoggedOut() = logInStatus

    //SignOut from current session
    fun setLogOut() {
        firebaseAuth.signOut()
        logInStatus.postValue(false)

    }
}