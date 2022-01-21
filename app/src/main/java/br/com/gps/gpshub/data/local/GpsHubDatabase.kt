package br.com.gps.gpshub.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.gps.gpshub.data.local.dao.AppsDao
import br.com.gps.gpshub.model.entity.Apps


@Database(entities = [Apps::class], version = 1, exportSchema = false)
abstract class GpsHubDatabase : RoomDatabase() {

    abstract fun appsDao(): AppsDao

}
