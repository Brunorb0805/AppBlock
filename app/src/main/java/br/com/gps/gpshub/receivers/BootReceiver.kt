package br.com.gps.gpshub.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import br.com.gps.gpshub.other.BackgroundManager
import br.com.gps.gpshub.other.Constants
import br.com.gps.gpshub.other.SharedPrefUtil

import br.com.gps.gpshub.services.LockService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var backgroundManager: BackgroundManager

    override fun onReceive(context: Context?, intent: Intent?) {

        if(intent != null && intent.action?.equals(Intent.ACTION_BOOT_COMPLETED) == true) {
            Log.d("BootReceiver", "Boot service....")

//        backgroundManager.startService(LoadAppListService::class.java)
            if (SharedPrefUtil.instance.getBoolean(Constants.LOCK_STATE, false)) {
                backgroundManager.startService(LockService::class.java)
                backgroundManager.startAlarmManager()
            }
        }
    }
}
