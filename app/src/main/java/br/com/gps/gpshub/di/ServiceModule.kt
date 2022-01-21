package br.com.gps.gpshub.di

import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.core.app.NotificationCompat
import br.com.gps.gpshub.R
import br.com.gps.gpshub.other.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped


@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {


    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext context: Context,
    ) = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("GPS Hub")
        .setContentText("Bloqueio de app executando")


    @ServiceScoped
    @Provides
    fun provideUsageStatsManager(@ApplicationContext context: Context): UsageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

//    @ServiceScoped
//    @Provides
//    fun provideActivityPendingIntent(
//        @ApplicationContext context: Context
//    ) =
//        PendingIntent.getActivity(
//            context,
//            0,
//            Intent(context, MainActivity::class.java),
////                .apply {
////                    action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
////                },
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
}
