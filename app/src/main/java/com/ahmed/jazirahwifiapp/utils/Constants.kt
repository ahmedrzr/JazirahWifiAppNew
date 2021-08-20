package com.ahmed.jazirahwifiapp.utils

object Constants {
    const val DB_NAME = "wifis"
    const val DB_AUTH = "auth_users"
    const val DB_APP_ACCESS_SETTINGS ="APP_ACCESS_SETTINGS"
    const val SHARED_PREFERENCE_AUTH_USER = "USER"
    const val SHARED_PREFERENCE_APP = "APP"
    const val SHARED_PREFERENCE_USER_PERMISSION = "PERMISSION"


    //APP SETTINGS
    const val WAITING_TIME = 3000L
    const val APP_PERMISSION_CODE = "appPermission"
    const val APP_USER_PERMISSION = "APP_USER_PERMISSION"
    const val APP_WIFI_ACCESS_CODE = "APP_WIFI_ACCESS_CODE"
    const val APP_IS_EDIT_VIEW_ALLOWED = "APP_IS_EDIT_VIEW_ALLOWED"
    const val APP_ACCESS_ADMIN = "APP_ACCESS_ADMIN"
    const val APP_AUTH_USER_EMAIL = "AUTH_USER_EMAIL"
    const val APP_AUTH_USER_DISPLAY_NAME = "AUTH_USER_DISPLAY_NAME"
    const val APP_PERMISSION = "APP_PERMISSION"
    const val APP_PUSH_NOTIFICATION_PERMISSION = "APP_PUSH_NOTIFICATION_PERMISSION"
    const val DIALOG_NOTIFICATION_DELAY = 2000L

    //FIREBASE PUSH NOTIFICATION
    const val BASE_URL = "https://fcm.googleapis.com"
    const val SERVER_KEY =
        "AAAAPIrQjtI:APA91bGfJgwCqDM6ubsBrO3iBB5SvDbtU-w3ioQDnDT6tY5zosGnYFy5XQF3-KD6cCgg_I4xKalk5HLMfLytP0J7TYZb4WOr6IFazdHEBnyVj-UxDf2OV0UghCY1q7v4rxdo1ARqYpyj"
    const val CONTENT_TYPE = "application/json"
    const val SUBSCRIPTION_TOPIC_ENROLLMENT_EVENTS = "/topics/ENROLLMENT_EVENTS"


}