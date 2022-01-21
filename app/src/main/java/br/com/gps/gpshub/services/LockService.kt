package br.com.gps.gpshub.services

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import br.com.gps.gpshub.data.local.datasource.apps.AppsRepository
import br.com.gps.gpshub.other.Constants
import br.com.gps.gpshub.other.SharedPrefUtil
import br.com.gps.gpshub.receivers.LockRestartReceiver
import br.com.gps.gpshub.ui.UnlockActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
@Suppress("DEPRECATION")
class LockService : IntentService("LockService") {


    var savePkgName: String? = null
    private var timer: Timer = Timer()

    //private boolean isLockTypeAccessibility;
    private var lastUnlockTimeSeconds: Long = 0
    private var lastUnlockPackageName = ""
    private var lockState = false
    var threadIsTerminate = false

    private var mServiceReceiver: ServiceReceiver? = null


    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var mUsageStatsManager: UsageStatsManager

    @Inject
    lateinit var mPackageManager: PackageManager

    @Inject
    lateinit var repository: AppsRepository


    companion object {
        const val TAG = "LockService"
        const val UNLOCK_ACTION = "UNLOCK_ACTION"
        const val LOCK_SERVICE_LASTTIME = "LOCK_SERVICE_LASTTIME"
        const val LOCK_SERVICE_LASTAPP = "LOCK_SERVICE_LASTAPP"

        var isActionLock = false
    }


    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "create lock service")

        lockState = SharedPrefUtil.instance.getBoolean(Constants.LOCK_STATE)

        mServiceReceiver = ServiceReceiver()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(UNLOCK_ACTION)
        registerReceiver(mServiceReceiver, filter)


        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        threadIsTerminate = true
        startForeground(Constants.NOTIFICATION_ID, baseNotificationBuilder.build())
    }

    override fun onBind(p0: Intent?): IBinder? {

        Log.d(TAG, "bind lock service")

        return null
    }

    override fun onHandleIntent(p0: Intent?) {
        Log.d(TAG, "handler intent lock service")
        runForever()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        threadIsTerminate = false
        timer.cancel()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(Constants.NOTIFICATION_ID)

        lockState = SharedPrefUtil.instance.getBoolean(Constants.LOCK_STATE)

        if (lockState) {
            val restartServiceTask = Intent(applicationContext, this.javaClass)
            restartServiceTask.setPackage(packageName)
            val restartPendingIntent = PendingIntent.getService(
                applicationContext,
                1495,
                restartServiceTask,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
            val myAlarmService = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
            myAlarmService[AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1500] =
                restartPendingIntent
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "destroy lock service")

        threadIsTerminate = false
        timer.cancel()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(Constants.NOTIFICATION_ID)

        lockState = SharedPrefUtil.instance.getBoolean(Constants.LOCK_STATE)
        if (lockState) {
            val intent = Intent(this, LockRestartReceiver::class.java)
            intent.putExtra(Constants.KEY_TYPE, Constants.KEY_LOCK_SERVICE)
            sendBroadcast(intent)
        }

        unregisterReceiver(mServiceReceiver)
    }


    private fun runForever() {

        Log.d(TAG, "run forever lock service")

        while (threadIsTerminate) {

            Log.d(TAG, "runing forever lock service")

            //pega o nome do app em primeiro plano
            val packageName: String = getLauncherTopApp()

            //Verifica se:  A trava está ativa
            //              Nome do Pacote não é vazio
            //              Não é o aplicativo de trava
            if (lockState && !TextUtils.isEmpty(packageName) && !inWhiteList(packageName)) {
                Log.d(TAG, "Trava ativa e pacote válido: $packageName")

                //Verifica se vai travar a tela por tempo (mas no vi funcionando ainda)
                val isLockOffScreenTime: Boolean =
                    SharedPrefUtil.instance.getBoolean(Constants.LOCK_AUTO_SCREEN_TIME, false)

                //Travar o app quando a tela tb é travada
                val isLockOffScreen: Boolean =
                    SharedPrefUtil.instance.getBoolean(Constants.LOCK_AUTO_SCREEN, false)

                //Paga o ultimo app travado
                savePkgName =
                    SharedPrefUtil.instance.getString(Constants.LOCK_LAST_LOAD_PKG_NAME, "")

                Log.d(TAG, "isLockOffScreenTime: $isLockOffScreenTime")
                Log.d(TAG, "isLockOffScreen: $isLockOffScreen")
                Log.d(TAG, "SavePkgName: $savePkgName")

                // Aqui se verifica a trava por tempo, e caso tenha passado o tempo de desbloqueio, trava o app novamente
                if (isLockOffScreenTime && !isLockOffScreen) {
                    val time: Long =
                        SharedPrefUtil.instance.getLong(Constants.LOCK_CURR_MILLISECONDS, 0)
                    val leaverTime: Long =
                        SharedPrefUtil.instance.getString(Constants.LOCK_APART_MILLISECONDS)
                            ?.toLong() ?: 0

                    if (!savePkgName.isNullOrEmpty() /*&& !TextUtils.isEmpty(packageName)*/ && savePkgName != packageName) {
                        if (isLauncher(packageName)) {
//                            val isSetUnLock: Boolean = repository.isSetUnLock(savePkgName)
//                            if (!isSetUnLock) {
                            if (System.currentTimeMillis() - time > leaverTime) {
//                                    mLockInfoManager.lockCommApplication(savePkgName)
                                repository.updateLockedS(savePkgName!!, true)
                            }
//                            }
                        }
                    }
                }

                if (isLockOffScreenTime && isLockOffScreen) {
                    val time: Long =
                        SharedPrefUtil.instance.getLong(Constants.LOCK_CURR_MILLISECONDS, 0)
                    val leaverTime: Long =
                        SharedPrefUtil.instance.getString(Constants.LOCK_APART_MILLISECONDS)
                            ?.toLong() ?: 0

                    if (!savePkgName.isNullOrEmpty() /*&& !TextUtils.isEmpty(packageName)*/ && savePkgName != packageName) {
                        if (isLauncher(packageName)) {
//                            val isSetUnLock: Boolean = repository.isSetUnLock(savePkgName)
//                            if (!isSetUnLock) {
                            if (System.currentTimeMillis() - time > leaverTime) {
//                                    mLockInfoManager.lockCommApplication(savePkgName)
                                repository.updateLockedS(savePkgName!!, true)
                            }
//                            }
                        }
                    }
                }

                if (!isLockOffScreenTime &&
                    isLockOffScreen &&
                    !savePkgName.isNullOrEmpty()
                ) {
                    if (savePkgName != packageName) {
                        isActionLock = false
                        if (isLauncher(packageName)) {
//                            val isSetUnLock: Boolean = repository.isSetUnLock(savePkgName)
//                            if (!isSetUnLock) {
//                                mLockInfoManager.lockCommApplication(savePkgName)
                            repository.updateLockedS(savePkgName!!, true)
//                            }
                        }
                    } else {
                        isActionLock = true
                    }
                }

                if (!isLockOffScreenTime &&
                    !isLockOffScreen &&
                    !savePkgName.isNullOrEmpty() &&
                    savePkgName != packageName
                ) {
                    if (isLauncher(packageName)) {
//                        val isSetUnLock: Boolean = repository.isSetUnLock(savePkgName)
//                        if (!isSetUnLock) {
//                            mLockInfoManager.lockCommApplication(savePkgName)
                        repository.updateLockedS(savePkgName!!, true)

//                        }
                    }
                }

                if (repository.isLock(packageName)) {
                    passwordLock(packageName)
                    continue
                }
            }

            try {
                Thread.sleep(210)
            } catch (ignore: Exception) {
            }
        }
    }

    private fun isLauncher(packageName: String): Boolean =
        getHomes().contains(packageName) || packageName.contains("launcher")


    private fun getLauncherTopApp(): String {
        //isLockTypeAccessibility = SpUtil.getInstance().getBoolean(AppConstants.LOCK_TYPE, false);

        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 10000
        var result = ""
        val event = UsageEvents.Event()
        val usageEvents: UsageEvents = mUsageStatsManager.queryEvents(beginTime, endTime)
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                result = event.packageName
            }
        }
        if (!TextUtils.isEmpty(result)) {
            return result
        }

        return ""
    }

    /** Verifica se voltou para algum aplicativo padrão */
    private fun getHomes(): List<String> {
        val names = mutableListOf<String>()

        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)

        val resolveInfo =
            mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

        for (ri in resolveInfo) {
            names.add(ri.activityInfo.packageName)
        }

        return names
    }

    private fun passwordLock(packageName: String) {
//        LockApplication.getInstance().clearAllActivity()

        startActivity(UnlockActivity.getInstance(this, packageName))
    }

    private fun inWhiteList(packageName: String): Boolean {
        return packageName == Constants.APP_PACKAGE_NAME
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }


    inner class ServiceReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val isLockOffScreen: Boolean =
                SharedPrefUtil.instance.getBoolean(Constants.LOCK_AUTO_SCREEN, false)
            val isLockOffScreenTime: Boolean =
                SharedPrefUtil.instance.getBoolean(Constants.LOCK_AUTO_SCREEN_TIME, false)
            when (action) {
                UNLOCK_ACTION -> {
                    lastUnlockPackageName = intent.getStringExtra(LOCK_SERVICE_LASTAPP) ?: ""
                    lastUnlockTimeSeconds =
                        intent.getLongExtra(LOCK_SERVICE_LASTTIME, lastUnlockTimeSeconds)
                }
                Intent.ACTION_SCREEN_OFF -> {
                    SharedPrefUtil.instance
                        .putLong(Constants.LOCK_CURR_MILLISECONDS, System.currentTimeMillis())
                    if (!isLockOffScreenTime && isLockOffScreen) {
                        val savePkgName: String =
                            SharedPrefUtil.instance.getString(Constants.LOCK_LAST_LOAD_PKG_NAME)
                                ?: ""

                        if (!TextUtils.isEmpty(savePkgName)) {
                            if (isActionLock) {
                                repository.updateLockedS(lastUnlockPackageName, true)
//                                repositoryManager.updateAppToLock(lastUnlockPackageName)
                            }
                        }
                    }
                }
            }
        }
    }
}
