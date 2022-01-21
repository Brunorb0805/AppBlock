package br.com.gps.gpshub.other

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import br.com.gps.gpshub.receivers.LockRestartReceiver
import br.com.gps.gpshub.services.LockService
import javax.inject.Inject


class BackgroundManager @Inject constructor(private val context: Context) {

    companion object {
        private const val period = 15 * 1000 //15 minutes;
    }

    @Suppress("DEPRECATION")
    @SuppressWarnings("deprecation")
    fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun startService(serviceClass: Class<*>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, serviceClass))
        } else context.startService(Intent(context, serviceClass))
    }

    fun startAlarmManager() {
        val intent = Intent(context, LockRestartReceiver::class.java)
        intent.putExtra(Constants.KEY_TYPE, Constants.KEY_START_LOCK_SERVICE)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 95374, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + period] =
            pendingIntent
    }

    fun stopAlarmManager() {
        val intent = Intent(context, LockRestartReceiver::class.java)
        intent.putExtra(Constants.KEY_TYPE, Constants.KEY_START_LOCK_SERVICE)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 95374, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun stopService(serviceClass: Class<*>?) {
        if (isServiceRunning(LockService::class.java))
            context.stopService(
                Intent(context, serviceClass)
            )
    }

}
