package br.com.gps.gpshub.data

import br.com.gps.gpshub.model.DeviceAppDataSupport
import br.com.gps.gpshub.model.FavAppDataSupport
import android.content.ContentValues
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import br.com.gps.gpshub.R
import br.com.gps.gpshub.model.entity.Apps
import br.com.gps.gpshub.other.Constants
import org.litepal.crud.DataSupport
import java.util.*


class AppsRepositoryManager(private val packageManager: PackageManager) {

//    fun selectApps(selectCondition: SelectCondition): MutableLiveData<List<DeviceAppDataSupport>> {
//        val mutableLiveData = MutableLiveData<List<DeviceAppDataSupport>>(mutableListOf())
//
//        val result = when (selectCondition) {
//            SelectCondition.ALL -> DataSupport.findAll(DeviceAppDataSupport::class.java)
//            SelectCondition.DEVICE -> DataSupport.where("not isLocalApp")
//                .find(DeviceAppDataSupport::class.java)
//            SelectCondition.FAVOURITE -> DataSupport.where("isFavouriteApp")
//                .find(DeviceAppDataSupport::class.java)
//        }
//
//        result.sortedWith(appsComparator)
//
//        mutableLiveData.value = result.sortedWith(appsComparator)
//
//        return mutableLiveData
//    }


    fun initFavouriteApps() {
        val favAppsDb = mutableListOf<FavAppDataSupport>()

        val favAppsPackagesMap = mutableMapOf<String, Boolean>().apply {
            put(Constants.APP_CALL_NAME, true)
            put(Constants.APP_CHECKLIST_NAME, true)
            put(Constants.APP_PATROL_NAME, true)
            put(Constants.APP_TALK_ME_NAME, true)
            put("br.com.gestaoonlineadm", false)
            put("br.com.gpssa.gpsseg", false)
            put("com.application.gps.gpsi", false)
            put("br.com.gpssa.app.ptc", false)
            put("com.application.gpst_teste.gpst_teste", false)
            put("br.com.inhaus.gpsp", false)
            put("com.gps.supervigilantes", false)
            put("sgo.br.issworl", false)
            put("br.com.gpssa.app.gpsapontamento", false)
        }

        val favAppsIconMap = mutableMapOf<String, Int>().apply {
            put(Constants.APP_CALL_NAME, R.drawable.ic_call)
            put(Constants.APP_CHECKLIST_NAME, R.drawable.ic_checklist)
            put(Constants.APP_PATROL_NAME, R.drawable.ic_patrol)
            put(Constants.APP_TALK_ME_NAME, R.drawable.ic_talk)
        }

        /** FavouritesApps */ // Mostra na tela inicial
        // GO Supervisor    "br.com.gestaoonlineadm"
        // GPS seg          "br.com.gpssa.gpsseg"
        // GPSi             "com.application.gps.gpsi"
        // GPSvc            "br.com.gpssa.app.ptc"
        // GPSt             "com.application.gpst_teste.gpst_teste"
        // GPSp             "br.com.inhaus.gpsp"
        // Zello            "com.loudtalks"
        // Super Vig        "com.gps.supervigilantes"
        // SGO +            "sgo.br.issworl"

        /** SystemApp */      // Nao mostra na tela, mas deve ter como travar

        for (packageName in favAppsPackagesMap) {
            val icon = favAppsIconMap[packageName.key] ?: 0
            val favApp = FavAppDataSupport(packageName.key, packageName.value, icon)
            favAppsDb.add(favApp)
        }

        DataSupport.deleteAll(FavAppDataSupport::class.java)
        DataSupport.saveAll(favAppsDb)
    }


//    //    @Throws(PackageManager.NameNotFoundException::class)
//    @Suppress("DEPRECATION") //PackageManager.MATCH_UNINSTALLED_PACKAGES
//    fun initDatabaseApps(resolveInfos: List<ResolveInfo>) {
//        var appsList = mutableListOf<DeviceAppDataSupport>()
//
//        /** Inicializa os fake apps do sistema(Ronda, Checklist...) */
////        initLocalApps()  Tirado para fazer o uso da "initDatabaseApps" em outros lugares
//
//        /** Inicializa os apps do celular */
//        for (resolveInfo in resolveInfos) {
//            if (resolveInfo.activityInfo.packageName != Constants.APP_PACKAGE_NAME) {
//
//                //Verifica se app eh favorito
//                val isfaviterApp = isHasFavouriteAppInfo(resolveInfo.activityInfo.packageName)
//                //Cria objeto App do tipo DataSupport
//                val deviceApp = DeviceAppDataSupport(resolveInfo.activityInfo.packageName)
//
//                val uninstallPackage = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N)
//                    PackageManager.GET_UNINSTALLED_PACKAGES else PackageManager.MATCH_UNINSTALLED_PACKAGES
//                //Pega as informacoes do aplicativo
//                val applicationInfo: ApplicationInfo = packageManager.getApplicationInfo(
//                    deviceApp.packageName!!, uninstallPackage
//                )
//
//                //Verifica se o aplicativo eh do sistema e ativa a flag caso positivo
//                if (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
//                    deviceApp.isSysApp = true
//                    deviceApp.topTitle = "System Applications"
//                } else {
//                    deviceApp.isSysApp = false
//                    deviceApp.topTitle = "User Application"
//                }
//                //Busca nome do aplicativo
//                val name: String = packageManager.getApplicationLabel(applicationInfo).toString()
//
//                //Inputa os valores no objeto
//                deviceApp.apply {
//                    appName = name
//                    isFavouriteApp = isfaviterApp
//                    /** Verificar se posteriormente vai dar erro ao inserir outros apps como favoritos */
//                    isLocked = !isfaviterApp
////                    appInfo = applicationInfo //Nao é salvo no banco, pode ignorar
//                }
//
//                //Insere no lista
//                appsList.add(deviceApp)
//            }
//        }
//        //Verifica se existe duplicatas e exclui
//        appsList = clearRepeatApps(appsList)
//        //Salva no banco
//        DataSupport.saveAll(appsList)
//    }

//    fun isSetUnLock(packageName: String?): Boolean {
//        val lockInfos: List<DeviceAppDataSupport> =
//            DataSupport.where("packageName = ?", packageName).find(DeviceAppDataSupport::class.java)
//        for (commLockInfo in lockInfos) {
//            if (commLockInfo.isSetUnlock) {
//                return true
//            }
//        }
//        return false
//    }

    fun isLockedPackageName(packageName: String?): Boolean {
        val lockInfos = DataSupport.where("packageName = ?", packageName)
            .findFirst(DeviceAppDataSupport::class.java)

        if (lockInfos != null && lockInfos.isLocked) {
            return true
        }

        return false
    }

//    fun updateAppToLock(packageName: String?) {
//        packageName?.let { updateLockStatus(it, true) }
//    }
//
//    fun updateAppToUnlock(packageName: String?) {
//        packageName?.let { updateLockStatus(it, false) }
//    }
//
//    fun updateFavStatus(packageName: String, isFav: Boolean) {
//        val values = ContentValues()
//        values.put("isFavouriteApp", isFav)
//        DataSupport.updateAll(
//            DeviceAppDataSupport::class.java,
//            values,
//            "packageName = ?",
//            packageName
//        )
//    }

//    fun deleteDatabaseApps(commLockInfos: List<DeviceAppDataSupport>) {
//        for (info in commLockInfos) {
//            DataSupport.deleteAll(
//                DeviceAppDataSupport::class.java,
//                "packageName = ?",
//                info.packageName
//            )
//        }
//    }

    fun initLocalApps() {
        val localApps = DataSupport.where("isLocalApp").find(FavAppDataSupport::class.java)
        var appsList = mutableListOf<DeviceAppDataSupport>()

        for (apps in localApps) {
            val deviceApp = DeviceAppDataSupport(apps.packageName)

            deviceApp.apply {
                appName = apps.packageName
                icon = apps.icon
                isFavouriteApp = true
                isLocalApp = true
            }

            appsList.add(deviceApp)
        }
        //Verifica se existe duplicatas e exclui
        appsList = clearRepeatApps(appsList)
        //Salva no banco
        DataSupport.saveAll(appsList)
    }


    private fun updateLockStatus(packageName: String, isLock: Boolean) {
        val values = ContentValues()
        values.put("isLocked", isLock)
        DataSupport.updateAll(
            DeviceAppDataSupport::class.java,
            values,
            "packageName = ?",
            packageName
        )
    }

    private fun isHasFavouriteAppInfo(packageName: String): Boolean {
        val favApps: List<FavAppDataSupport> =
            DataSupport.where("packageName = ?", packageName).find(FavAppDataSupport::class.java)

        return favApps.isNotEmpty()
    }

    private fun clearRepeatApps(appsList: List<DeviceAppDataSupport>): MutableList<DeviceAppDataSupport> {
        val hashMap: HashMap<String, DeviceAppDataSupport> =
            HashMap<String, DeviceAppDataSupport>()
        for (app in appsList) {
            if (!hashMap.containsKey(app.packageName)) {
                hashMap[app.packageName!!] = app
            }
        }
        val commLockInfos = mutableListOf<DeviceAppDataSupport>()
        for ((_, value) in hashMap) {
            commLockInfos.add(value)
        }
        return commLockInfos
    }

    //    private val appsComparator = Comparator<DeviceAppDataSupport> { a, b ->
    private val appsComparator = Comparator<Apps> { a, b ->
        when {
            (a == null && b == null) -> 0
            (a == null) -> -1
            (a.isLocked && !b.isLocked) -> -1
            (!a.isLocked && b.isLocked) -> 1
            (a.isLocalApp && !b.isLocalApp) -> -1
            (!a.isLocalApp && b.isLocalApp) -> 1
            (!a.isLocalApp && a.isFavouriteApp && !b.isLocalApp && !b.isFavouriteApp) -> -1
            (!a.isLocalApp && !a.isFavouriteApp && !b.isLocalApp && b.isFavouriteApp) -> 1
            (a.appName != null && b.appName != null) -> (a.appName!!.compareTo(b.appName!!))

            else -> 0
        }
    }
}
