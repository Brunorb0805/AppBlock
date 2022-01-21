package br.com.gps.gpshub.other

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import br.com.gps.gpshub.R

@RequiresApi(api = Build.VERSION_CODES.O)
object NotificationUtil {

    private const val NOTIFICATION_CHANNEL_ID = "10101"
    private const val NOTIFICATION_CHANNEL_NAME = "GpsHub Channel"


    private lateinit var notificationManager: NotificationManager


    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createNotification(context: Service, title: String, message: String) {
        notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        /** Cria canal de notificacao */
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
//        notificationChannel.enableVibration(true)
        notificationChannel.description = NOTIFICATION_CHANNEL_NAME
        // serão exibidas se o telefone der suporte a essas coisas
//        notificationChannel.enableLights(true)
        // criar o canal com as propriedades definidas
        notificationManager.createNotificationChannel(notificationChannel)


        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
//            .setContentIntent(notificationPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(false)


        context.startForeground(145, builder.build())
    }

}
