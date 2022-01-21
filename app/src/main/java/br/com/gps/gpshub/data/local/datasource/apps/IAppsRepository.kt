package br.com.gps.gpshub.data.local.datasource.apps

import android.content.pm.ResolveInfo
import androidx.lifecycle.LiveData
import br.com.gps.gpshub.data.Result
import br.com.gps.gpshub.model.entity.Apps

interface IAppsRepository {

    suspend fun initFavouriteApps()

    suspend fun initDatabaseApps(resolveInfos: List<ResolveInfo>)

    suspend fun selectDevicesApp() : Result<List<Apps>>

    suspend fun insertApps(listApps: List<Apps>)

    suspend fun deleteApps(listApps: List<Apps>)

    fun isLock(packageName: String) : Boolean

    suspend fun updateLocked(packageName: String, isLocked: Boolean)

    fun updateFavourite(packageName: String, isFav: Boolean)

    fun observeApps(): LiveData<Result<List<Apps>>>

    fun observeAppsFav(): LiveData<Result<List<Apps>>>

}