package br.com.gps.gpshub.data.local.datasource.apps

import androidx.lifecycle.LiveData
import br.com.gps.gpshub.data.Result
import br.com.gps.gpshub.model.entity.Apps


interface IAppsDataSource {

    fun observeApps(): LiveData<Result<List<Apps>>>

    fun observeAppsFav(): LiveData<Result<List<Apps>>>

    suspend fun selectDeviceApps(): Result<List<Apps>>

    suspend fun isLock(packageName: String) : Boolean

    suspend fun insertApps(listApps: List<Apps>)

    suspend fun deleteApps(listApps: List<Apps>)

    suspend fun updateLocked(packageName: String, isLocked: Boolean)

    suspend fun updateFavourite(packageName: String, isFav: Boolean)

}