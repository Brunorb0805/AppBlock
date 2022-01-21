package br.com.gps.gpshub.other

import br.com.gps.gpshub.BuildConfig


object Constants {

    // Constants do App
    const val APP_PACKAGE_NAME = BuildConfig.APPLICATION_ID
    const val APP_CALL_NAME = "Telefone"
    const val APP_CHECKLIST_NAME = "Checklist"
    const val APP_PATROL_NAME = "Ronda"
    const val APP_TALK_ME_NAME = "Fale Comigo"

    // Database
    const val DB_NAME = "hub_db"

    // Notification
    const val NOTIFICATION_CHANNEL_ID = "gpshub_channel"
    const val NOTIFICATION_CHANNEL_NAME = "GpsHub Lock Channel"
    const val NOTIFICATION_ID = 1


    // Service Lock
    const val LOCK_STATE = "lockState"
    const val LOCK_PACKAGE_NAME = "lockPackageName"
    const val LOCK_FROM = "lockFrom"
    const val LOCK_FROM_FINISH = "lockFromFinish"

    //SharedPrefs Keys
    const val LOCK_IS_INIT_FAVOURITE = "lockIsInitFavorite"         //boolean
    const val LOCK_IS_INIT_DB = "lockIsInitDb"                      //boolean
    const val LOCK_AUTO_SCREEN = "lockAutoScreen"                   //boolean
    const val LOCK_AUTO_SCREEN_TIME = "lockAutoScreenTime"          //boolean
    const val LOCK_IS_FIRST_LOCK = "isFirstLock"                    //boolean
    const val LOCK_LAST_LOAD_PKG_NAME = "lastLoadPackageName"       //string
    const val LOCK_CURR_MILLISECONDS = "lockCurrMilliseconds"       //long
    const val LOCK_APART_MILLISECONDS = "lockAutoScreenTimeValue"   //int


    // App Keys
    const val KEY_TYPE = "type"
    const val KEY_LOCK_SERVICE = "lockService"
    const val KEY_START_LOCK_SERVICE = "startLockServiceFromAlarmManager"


//    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"


}