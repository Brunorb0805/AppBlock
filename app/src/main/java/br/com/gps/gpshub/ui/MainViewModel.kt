package br.com.gps.gpshub.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.gps.gpshub.data.Result
import br.com.gps.gpshub.data.local.datasource.apps.AppsRepository
import br.com.gps.gpshub.model.entity.Apps
import br.com.gps.gpshub.other.Constants
import br.com.gps.gpshub.other.SharedPrefUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val mPackageManager: PackageManager,
    private val repository: AppsRepository
) : ViewModel() {


    private val _isLoadingApps = MutableStateFlow(true)
    val isLoading = _isLoadingApps.asStateFlow()


    init {
        viewModelScope.launch {
            Log.d("StartAppsService", "Run StartAppsService")
            loadApps()
            _isLoadingApps.value = false
        }
    }


    private suspend fun loadApps() {
        val isInitFavourite: Boolean =
            SharedPrefUtil.instance.getBoolean(Constants.LOCK_IS_INIT_FAVOURITE, false)
        val isInitDb: Boolean =
            SharedPrefUtil.instance.getBoolean(Constants.LOCK_IS_INIT_DB, false)
        if (!isInitFavourite) {
            SharedPrefUtil.instance.putBoolean(Constants.LOCK_IS_INIT_FAVOURITE, true)
            repository.initFavouriteApps()
        }

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfos: List<ResolveInfo> = mPackageManager.queryIntentActivities(intent, 0)


        if (isInitDb) {
            val appList = mutableListOf<ResolveInfo>()
            val dbList: List<Apps> = repository.selectDevicesApp().let { result ->
                if (result is Result.Success) result.data else mutableListOf()
            }

            for (resolveInfo in resolveInfos) {
                if (resolveInfo.activityInfo.packageName != Constants.APP_PACKAGE_NAME) {
                    appList.add(resolveInfo)
                }
            }

            if (appList.size > dbList.size) {
                val resList = mutableListOf<ResolveInfo>()
                val hashMap = mutableMapOf<String, Apps>()

                for (info in dbList) {
                    hashMap[info.packageName] = info
                }
                for (info in appList) {
                    if (!hashMap.containsKey(info.activityInfo.packageName)) {
                        resList.add(info)
                    }
                }
                try {
                    if (resList.size != 0) repository.initDatabaseApps(resList)
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }

            } else if (appList.size < dbList.size) {
                val commList = mutableListOf<Apps>()
                val hashMap = mutableMapOf<String, ResolveInfo>()

                for (info in appList) {
                    hashMap[info.activityInfo.packageName] = info
                }
                for (info in dbList) {
                    if (!hashMap.containsKey(info.packageName)) {
                        commList.add(info)
                    }
                }
                if (commList.size != 0) repository.deleteApps(commList)
            }

        } else {
            SharedPrefUtil.instance.putBoolean(Constants.LOCK_IS_INIT_DB, true)
            try {
                repository.initLocalApps()
                repository.initDatabaseApps(resolveInfos)

            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
    }

}
