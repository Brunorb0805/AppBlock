package br.com.gps.gpshub.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import br.com.gps.gpshub.model.entity.Apps


@Dao
interface AppsDao {

    @Query("SELECT * FROM Apps WHERE NOT isLocalApp ORDER BY NOT isLocked, isFavouriteApp, appName")
    fun observeApps() : LiveData<List<Apps>>

    @Query("SELECT * FROM Apps WHERE isFavouriteApp ORDER BY NOT isLocalApp")
    fun observeFavApps() : LiveData<List<Apps>>

    @Query("SELECT * FROM Apps WHERE NOT isLocalApp")
    suspend fun getDeviceTasks(): List<Apps>

    @Query("SELECT isLocked FROM Apps WHERE packageName = :packageName")
    suspend fun isLock(packageName: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(task: List<Apps>)

    @Delete
    suspend fun deleteApps(task: List<Apps>)

    @Query("UPDATE apps SET isLocked = :locked WHERE packageName = :packageName")
    suspend fun updateLocked(packageName: String, locked: Boolean)

    @Query("UPDATE apps SET isFavouriteApp = :fav WHERE packageName = :packageName")
    suspend fun updateFavourite(packageName: String, fav: Boolean)

//    @Query("SELECT * FROM Apps")
//    fun observeAllApps(): LiveData<List<Apps>>

}
