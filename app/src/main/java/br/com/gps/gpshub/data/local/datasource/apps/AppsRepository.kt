package br.com.gps.gpshub.data.local.datasource.apps

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import br.com.gps.gpshub.GPSHubApplication
import br.com.gps.gpshub.R
import br.com.gps.gpshub.data.Result
import br.com.gps.gpshub.model.FavAppDataSupport
import br.com.gps.gpshub.model.entity.Apps
import br.com.gps.gpshub.other.Constants
import br.com.gps.gpshub.other.FileUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.litepal.crud.DataSupport


class AppsRepository(
    private val context: Context,
//    private val packageManager: PackageManager,
    private val appsLocalDataSource: IAppsDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : IAppsRepository {


    override suspend fun initFavouriteApps() {
//        val favAppsDb = mutableListOf<FavAppDataSupport>()
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

    override suspend fun initDatabaseApps(resolveInfos: List<ResolveInfo>) {
        val appsList = mutableListOf<Apps>()


        val packageManager = context.packageManager

        /** Inicializa os fake apps do sistema(Ronda, Checklist...) */
//        initLocalApps()  Tirado para fazer o uso da "initDatabaseApps" em outros lugares

        /** Inicializa os apps do celular */
        for (resolveInfo in resolveInfos) {
            if (resolveInfo.activityInfo.packageName != Constants.APP_PACKAGE_NAME) {

                //Verifica se app eh favorito
                val isfaviterApp = isHasFavouriteAppInfo(resolveInfo.activityInfo.packageName)
                //Cria objeto App do tipo DataSupport
                val deviceApp = Apps(resolveInfo.activityInfo.packageName)

                val uninstallPackage = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N)
                    PackageManager.GET_UNINSTALLED_PACKAGES else PackageManager.MATCH_UNINSTALLED_PACKAGES
                //Pega as informacoes do aplicativo
                val applicationInfo: ApplicationInfo = packageManager.getApplicationInfo(
                    deviceApp.packageName, uninstallPackage
                )

                //Verifica se o aplicativo eh do sistema e ativa a flag caso positivo
                if (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                    deviceApp.isSysApp = true
                    deviceApp.topTitle = "System Applications"
                } else {
                    deviceApp.isSysApp = false
                    deviceApp.topTitle = "User Application"
                }
                //Busca nome do aplicativo
                val name: String = packageManager.getApplicationLabel(applicationInfo).toString()
                val icon: Drawable = packageManager.getApplicationIcon(applicationInfo)
    
                val path = FileUtil.saveOnFile(context, icon.toBitmap(), name.replace(" ", "_"))

                //Inputa os valores no objeto
                deviceApp.apply {
                    appName = name
                    iconDrawable = path
                    isFavouriteApp = isfaviterApp
                    /** Verificar se posteriormente vai dar erro ao inserir outros apps como favoritos */
                    isLocked = !isfaviterApp
//                    appInfo = applicationInfo //Nao é salvo no banco, pode ignorar
                }

                //Insere no lista
                appsList.add(deviceApp)
            }
        }
//        //Verifica se existe duplicatas e exclui
//        appsList = clearRepeatApps(appsList)
//        //Salva no banco
//        DataSupport.saveAll(appsList)
        appsLocalDataSource.insertApps(appsList)
    }

    suspend fun initLocalApps() {
        val localApps = DataSupport.where("isLocalApp").find(FavAppDataSupport::class.java)
        val appsList = mutableListOf<Apps>()

        for (apps in localApps) {
            val deviceApp = Apps(apps.packageName)

            deviceApp.apply {
                appName = apps.packageName
                icon = apps.icon
                isFavouriteApp = true
                isLocalApp = true
            }

            appsList.add(deviceApp)
        }
//        //Verifica se existe duplicatas e exclui
//        appsList = clearRepeatApps(appsList)
//        //Salva no banco
//        DataSupport.saveAll(appsList)
        appsLocalDataSource.insertApps(appsList.distinct())
    }

    override fun observeAppsFav(): LiveData<Result<List<Apps>>> =
        appsLocalDataSource.observeAppsFav()

    override fun observeApps(): LiveData<Result<List<Apps>>> =
        appsLocalDataSource.observeApps()


    override suspend fun selectDevicesApp(): Result<List<Apps>> {
        return appsLocalDataSource.selectDeviceApps()
    }

    override fun isLock(packageName: String): Boolean {
        var b = false
        CoroutineScope(ioDispatcher).launch {
            b = appsLocalDataSource.isLock(packageName)
        }
        return b
    }

    override suspend fun insertApps(listApps: List<Apps>) {
        appsLocalDataSource.insertApps(listApps)
    }

    override suspend fun deleteApps(listApps: List<Apps>) {
        appsLocalDataSource.deleteApps(listApps)
    }

    override suspend fun updateLocked(packageName: String, isLocked: Boolean) =
        appsLocalDataSource.updateLocked(packageName, isLocked)

    override fun updateFavourite(packageName: String, isFav: Boolean) {
        CoroutineScope(ioDispatcher).launch {
            appsLocalDataSource.updateFavourite(packageName, isFav)
        }
    }


    private fun isHasFavouriteAppInfo(packageName: String): Boolean {
        val favApps: List<FavAppDataSupport> =
            DataSupport.where("packageName = ?", packageName).find(FavAppDataSupport::class.java)

        return favApps.isNotEmpty()
    }


    fun updateLockedS(packageName: String, isLocked: Boolean) {
        CoroutineScope(ioDispatcher).launch {
            appsLocalDataSource.updateLocked(packageName, isLocked)
        }
    }


}
