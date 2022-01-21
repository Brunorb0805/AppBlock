package br.com.gps.gpshub.data.local.datasource.apps

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import br.com.gps.gpshub.data.Result
import br.com.gps.gpshub.data.Result.Error
import br.com.gps.gpshub.data.Result.Success
import br.com.gps.gpshub.data.local.dao.AppsDao
import br.com.gps.gpshub.model.entity.Apps
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AppsLocalDataSource internal constructor(
    private val appsDao: AppsDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : IAppsDataSource {

    override fun observeAppsFav(): LiveData<Result<List<Apps>>> {
        return appsDao.observeFavApps().map {
            Success(it)
        }
    }

    override fun observeApps(): LiveData<Result<List<Apps>>> {
        return appsDao.observeApps().map {
            Success(it)
        }
    }

    override suspend fun selectDeviceApps(): Result<List<Apps>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(appsDao.getDeviceTasks())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun deleteApps(listApps: List<Apps>) = withContext(ioDispatcher) {
        appsDao.deleteApps(listApps)
    }

    override suspend fun isLock(packageName: String): Boolean = appsDao.isLock(packageName)

    override suspend fun insertApps(listApps: List<Apps>) = withContext(ioDispatcher) {
        appsDao.insertApps(listApps)
    }

    override suspend fun updateLocked(packageName: String, isLocked: Boolean) =
        withContext(ioDispatcher) {
            appsDao.updateLocked(packageName, isLocked)
        }

    override suspend fun updateFavourite(packageName: String, isFav: Boolean) {
        appsDao.updateFavourite(packageName, isFav)
    }

}
