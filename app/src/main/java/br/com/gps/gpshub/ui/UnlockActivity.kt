package br.com.gps.gpshub.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.gps.gpshub.R
import br.com.gps.gpshub.data.local.datasource.apps.AppsRepository
import br.com.gps.gpshub.databinding.ActivityUnlockBinding
import br.com.gps.gpshub.extensions.disableClick
import br.com.gps.gpshub.extensions.hideKeyboard
import br.com.gps.gpshub.extensions.shake
import br.com.gps.gpshub.extensions.snake
import br.com.gps.gpshub.other.Constants
import br.com.gps.gpshub.other.SharedPrefUtil
import br.com.gps.gpshub.services.LockService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class UnlockActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityUnlockBinding
    private val binding: ActivityUnlockBinding get() = _binding

    lateinit var pkgName: String
    private var actionFrom: String? = null
    private var numberOfAttempts = 3

    private var mGestureUnlockReceiver: GestureUnlockReceiver? = null

//    @Inject
//    lateinit var mPackageManager: PackageManager

    @Inject
    lateinit var repository: AppsRepository


    companion object {
        const val FINISH_UNLOCK_THIS_APP = "finish_unlock_this_app"

        fun getInstance(context: Context, packageName: String): Intent {
            return Intent(context, UnlockActivity::class.java).apply {
                putExtra(Constants.LOCK_PACKAGE_NAME, packageName)
                putExtra(Constants.LOCK_FROM, Constants.LOCK_FROM_FINISH)

                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityUnlockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mGestureUnlockReceiver)
    }

    override fun onBackPressed() {
        if (actionFrom == Constants.LOCK_FROM_FINISH) {
            goHome(this)
//        } else if (actionFrom == Constants.LOCK_FROM_LOCK_MAIN_ACITVITY) {
//            finish()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }


    private fun setup() {
        setupExtras()
        setupView()
        setupListener()
        setupReceiver()
    }

    private fun setupExtras() {
        pkgName = intent.getStringExtra(Constants.LOCK_PACKAGE_NAME) ?: ""
        actionFrom = intent.getStringExtra(Constants.LOCK_FROM)
    }

    @Suppress("DEPRECATION")
    private fun setupView() {
        try {
            val uninstallPackage = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N)
                PackageManager.GET_UNINSTALLED_PACKAGES else PackageManager.MATCH_UNINSTALLED_PACKAGES
            val appInfo = packageManager.getApplicationInfo(pkgName, uninstallPackage)
            val iconDrawable = packageManager.getApplicationIcon(appInfo)
            val appLabel = packageManager.getApplicationLabel(appInfo).toString()
            binding.iconAppImageView.setImageDrawable(iconDrawable)
            binding.nameAppTextView.text = appLabel

        } catch (e: Exception) {
        }
    }

    private fun setupListener() {
        binding.unlockButton.setOnClickListener {
            it.disableClick()
            unlockApp()
        }
    }

    private fun setupReceiver() {
        mGestureUnlockReceiver = GestureUnlockReceiver()
        val filter = IntentFilter()
        //  filter.addAction(UnLockMenuPopWindow.UPDATE_LOCK_VIEW);
        //  filter.addAction(UnLockMenuPopWindow.UPDATE_LOCK_VIEW);
        filter.addAction(FINISH_UNLOCK_THIS_APP)
        registerReceiver(mGestureUnlockReceiver, filter)
    }

    private fun checkPassword(): Boolean {
        return binding.passawordTextView.text.toString().length == 5
    }

    private fun goHome(activity: AppCompatActivity) {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        activity.startActivity(homeIntent)
        activity.finish()
    }

    private fun unlockApp() {
        if (checkPassword()) {
            SharedPrefUtil.instance.apply {
                putLong(Constants.LOCK_CURR_MILLISECONDS, System.currentTimeMillis())
                putString(Constants.LOCK_LAST_LOAD_PKG_NAME, pkgName)
            }

            //Envia a hora do desbloqueio e nome do app para o serviço
            val intent = Intent(LockService.UNLOCK_ACTION)
            intent.putExtra(LockService.LOCK_SERVICE_LASTTIME, System.currentTimeMillis())
            intent.putExtra(LockService.LOCK_SERVICE_LASTAPP, pkgName)
            sendBroadcast(intent)

            repository.updateLockedS(pkgName, false)

            finish()

        } else {
            if (--numberOfAttempts > 0) {
                hideKeyboard()
//            vibrate(500L)
                binding.passawordTextView.setText("")
                binding.passawordLayout.shake {}
                snake(
                    binding.passawordLayout,
                    getString(R.string.invalid_password_message, numberOfAttempts)
                )

            } else {
                goHome(this)
            }
        }
    }

    private inner class GestureUnlockReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            //            if (action.equals(UnLockMenuPopWindow.UPDATE_LOCK_VIEW)) {
//                mLockPatternView.initRes();
//            } else
            if (action == FINISH_UNLOCK_THIS_APP) {
                finish()
            }
        }
    }
}
