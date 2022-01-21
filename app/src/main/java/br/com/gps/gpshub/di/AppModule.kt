package br.com.gps.gpshub.di

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Room
import br.com.gps.gpshub.data.local.GpsHubDatabase
import br.com.gps.gpshub.data.local.datasource.apps.AppsLocalDataSource
import br.com.gps.gpshub.data.local.datasource.apps.AppsRepository
import br.com.gps.gpshub.data.local.datasource.apps.IAppsDataSource
import br.com.gps.gpshub.other.BackgroundManager
import br.com.gps.gpshub.other.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //    @Qualifier
//    @Retention(AnnotationRetention.RUNTIME)
//    annotation class RemoteTasksDataSource
//
    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class LocalAppsDataSource
//
//    @Singleton
//    @RemoteTasksDataSource
//    @Provides
//    fun provideTasksRemoteDataSource(): TasksDataSource {
//        return TasksRemoteDataSource
//    }
//

//    @Singleton
//    @Provides
//    fun provideSharedPreferences(app: Application) =
//        app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
//
//    @Singleton
//    @Provides
//    fun provideName(sharedPreferences: SharedPreferences) =
//        sharedPreferences.getString(KEY_NAME, "") ?: ""


    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): GpsHubDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            GpsHubDatabase::class.java,
            Constants.DB_NAME
        ).build()
    }


    @Singleton
    @LocalAppsDataSource
    @Provides
    fun provideAppsLocalDataSource(
        database: GpsHubDatabase,
        ioDispatcher: CoroutineDispatcher
    ): IAppsDataSource {
        return AppsLocalDataSource(
            database.appsDao(), ioDispatcher
        )
    }


    @Singleton
    @Provides
    fun provideAppsRepository(
        @ApplicationContext context: Context,
//        packageManager: PackageManager,
        @AppModule.LocalAppsDataSource appsLocalDataSource: IAppsDataSource,
        ioDispatcher: CoroutineDispatcher
    ) = AppsRepository(context, appsLocalDataSource, ioDispatcher)


//    @Singleton
//    @Provides
//    fun provideAppsRepositoryManager(packageManager: PackageManager) =
//        AppsRepositoryManager(packageManager)


    @Singleton
    @Provides
    fun provideBackgroundManager(@ApplicationContext context: Context): BackgroundManager =
        BackgroundManager(context.applicationContext)


    @Singleton
    @Provides
    fun providePackageManager(@ApplicationContext context: Context): PackageManager =
        context.applicationContext.packageManager


    @ServiceScoped
    @Provides
    fun provideActivityManager(@ApplicationContext context: Context): ActivityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager


    @Singleton
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
