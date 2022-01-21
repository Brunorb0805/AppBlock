package br.com.gps.gpshub.services


//@AndroidEntryPoint
//@Suppress("DEPRECATION")
class StartAppsService { //}: IntentService(StartAppsService::javaClass.name) {

//    @Inject
//    lateinit var mPackageManager: PackageManager
//
//    @Inject
//    lateinit var repository: AppsRepository

//    override fun onCreate() {
//        super.onCreate()
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationUtil.createNotification(
//                this,
//                "GPSHub Lock",
//                "Carregando Lock Apps Service"
//            )
//        }
//    }
//
//    override fun onHandleIntent(p0: Intent?) {
//
//        val isInitFavourite: Boolean =
//            SharedPrefUtil.instance.getBoolean(Constants.LOCK_IS_INIT_FAVOURITE, false)
//        val isInitDb: Boolean =
//            SharedPrefUtil.instance.getBoolean(Constants.LOCK_IS_INIT_DB, false)
//        if (!isInitFavourite) {
//            SharedPrefUtil.instance.putBoolean(Constants.LOCK_IS_INIT_FAVOURITE, true)
//            initFavoriteApps()
//        }
//
//        Log.d("StartAppsService", "Run StartAppsService")
//
//    }

    private fun initFavoriteApps() {

    }

//    override fun onStartJob(p0: JobParameters?): Boolean {
//
//    }
//
//    override fun onStopJob(p0: JobParameters?): Boolean {
//
//    }
}