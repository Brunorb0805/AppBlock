package br.com.gps.gpshub.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import br.com.gps.gpshub.other.BackgroundManager
import br.com.gps.gpshub.other.Constants
import br.com.gps.gpshub.other.SharedPrefUtil
import br.com.gps.gpshub.services.LockService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LockRestartReceiver : BroadcastReceiver() {

    @Inject
    lateinit var backgroundManager: BackgroundManager


    override fun onReceive(context: Context, intent: Intent?) {
        val lockState: Boolean = SharedPrefUtil.instance.getBoolean(Constants.LOCK_STATE, true)

        if (intent != null && lockState) {
            val type: String? = intent.getStringExtra(Constants.KEY_TYPE)
            if (type.contentEquals(Constants.KEY_LOCK_SERVICE)) //context.startService(new Intent(context, LockService.class));
                backgroundManager
                    .startService(LockService::class.java) else if (type.contentEquals(Constants.KEY_START_LOCK_SERVICE)) {
                if (backgroundManager.isServiceRunning(LockService::class.java)) {
                    backgroundManager.startService(LockService::class.java)
                }
                //repeat
                backgroundManager.startAlarmManager()
            }
        }
    }
}