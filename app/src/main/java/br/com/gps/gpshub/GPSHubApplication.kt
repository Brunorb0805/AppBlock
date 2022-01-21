package br.com.gps.gpshub

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import br.com.gps.gpshub.other.SharedPrefUtil
import dagger.hilt.android.HiltAndroidApp
import org.litepal.LitePal


@HiltAndroidApp
class GPSHubApplication : Application() {


//    companion object {
//        const val TAG ="GPSHubApplication"
//    }


    override fun onCreate() {
        super.onCreate()

        /** Inicializa SharedPreferences */
        SharedPrefUtil.instance.init(this)

        /** Inicializa Banco de Dados da feature AppsLock */
        LitePal.initialize(this)

        /** Inicializa tema do app de acordo com a configuracao */
        SharedPrefUtil.instance.getString(getString(R.string.id_theme), "")?.let {
            if (it.isNotBlank()) AppCompatDelegate.setDefaultNightMode(it.toInt())
        }

//        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener())

    }


//    class AppLifecycleListener : DefaultLifecycleObserver {
//
//        override fun onStart(owner: LifecycleOwner) { // app moved to foreground
//            Log.e(TAG, "Start GPS Hub")
//        }
//
//        override fun onStop(owner: LifecycleOwner) { // app moved to background
//            Log.e(TAG, "Stop GPS Hub")
//        }
//
//        override fun onDestroy(owner: LifecycleOwner) {
//            Log.e(TAG, "Destroy GPS Hub")
//            super.onDestroy(owner)
//        }
//    }

}
