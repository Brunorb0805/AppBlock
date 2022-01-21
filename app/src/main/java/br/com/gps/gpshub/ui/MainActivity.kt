package br.com.gps.gpshub.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import br.com.gps.gpshub.R
import br.com.gps.gpshub.databinding.ActivityMainBinding
import br.com.gps.gpshub.extensions.*
import br.com.gps.gpshub.other.BackgroundManager
import br.com.gps.gpshub.other.Constants
import br.com.gps.gpshub.other.SharedPrefUtil
import br.com.gps.gpshub.services.LockService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private val binding get() = _binding

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var backgroundManager: BackgroundManager


    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var requestPackageUsagePermissionLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionInfoAppLauncher: ActivityResultLauncher<Intent>

    private lateinit var appBarConfiguration: AppBarConfiguration

    //    private lateinit var navController: NavController
    private val navController by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)!!
            .findNavController()
    }


    private val requestPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** SplashScreen padrao do android */
        installSplashScreen()

        /** Usado para esperar o carregamento dos aplicativos para iniciar a view em "setup()" */
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check if the initial data is ready.
                    return if (!mainViewModel.isLoading.value) {
                        // The content is ready; start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        setup()
                        true
                    } else {
                        // The content is not ready; suspend.
                        false
                    }
                }
            }
        )

        //Inicia os listener de laucher das telas de permissoes
        setupPermissionsLaunchers()
    }

//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }

    private fun setup() {
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        startBlock()
        startLockService()

        setupNavigationViewListener()

        checkPermissionGranted()
    }

    private fun startLockService() {
        if (SharedPrefUtil.instance.getBoolean(Constants.LOCK_IS_FIRST_LOCK, true)) {
            SharedPrefUtil.instance.putBoolean(Constants.LOCK_STATE, true)
            SharedPrefUtil.instance.putBoolean(Constants.LOCK_IS_FIRST_LOCK, false)
        }

        if (SharedPrefUtil.instance.getBoolean(Constants.LOCK_STATE)) {
            if (!backgroundManager.isServiceRunning(LockService::class.java)) {
                backgroundManager.apply {
                    startService(LockService::class.java)
                    startAlarmManager()
                }
            }
        }
    }

    private fun setupNavigationViewListener() {

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.settingFragment),
            binding.drawerLayout
        )

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.apply {
            setupActionBarWithNavController(
                navController,
                appBarConfiguration
            )
        }

        // Da a habilidade de navegar de volta na seta do toolbar
        binding.appBarMain.toolbar.setNavigationOnClickListener {
            navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        }

        // Liga a navegação com menu do Navigation lateral
        binding.navView.setupWithNavController(navController)

        binding.navView.setNavigationItemSelectedListener { dest ->
            binding.drawerLayout.closeDrawers()

            when (dest.itemId) {
                R.id.navClearCache -> {}
                R.id.navExit -> logout()
                R.id.navSync -> {}
                else -> {
                    NavigationUI.onNavDestinationSelected(dest, navController)
                }
            }
            true
        }
    }

    private fun setupPermissionsLaunchers() {
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionResult ->
            val permissionsIdentified = permissionResult.all { it.key in requestPermissions }
            val permissionsGrant = permissionResult.all { it.value == true }

            if (permissionsIdentified && permissionsGrant) {
                toast(getString(R.string.permission_granted_message))
                checkPackageUsageStatsPermission()
            } else {
                //Verifica se o usuario nao negou e bloqueou a requisicao de permissoes
                val permissionsGrantRationale = permissionResult
                    .filter { it.value == false }
                    .all { shouldShowRequestPermissionRationale(it.key) }

                if (permissionsGrantRationale) {
                    //Se NAO negou, tenta novamente
                    showWhyPermissionsAreNeededDialog { checkPermissionGranted() }
                } else {
                    //Se SIM, abre a tela de configuracoes do app
                    showWhyPermissionsAreNeededDialog { goToInfoAppSettings() }
                }
            }
        }

        requestPackageUsagePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (!hasPackageUsageStatsPermission()) {
                    showPackageUsageStatsPermissionDialog()
                }
//                if (hasPackageUsageStatsPermission()) {
//                    goToMain()
//
//                } else showPackageUsageStatsPermissionDialog()
            }

        requestPermissionInfoAppLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                checkPermissionGranted()
            }
    }

//    private fun startBlock() {
//        (getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager).let {
//            if (it.isLockTaskPermitted(packageName)) {
////                DebugUtil.d(TAG, "isLockTaskPermitted: ALLOWED")
//                startLockTask()
////                return@postDelayed
//            }
//        }
//    }

    private fun checkPermissionGranted() {
        if (shouldRequestPermission(requestPermissions)) {
            requestPermissionLauncher.launch(requestPermissions)
        } else {
            checkPackageUsageStatsPermission()
        }
    }

    private fun checkPackageUsageStatsPermission() {
        /** VERIFICA E ABRE TELA DE PERMISSAO DE 'RASTREIO DE USO DE OUTROS APPS' CASO NAO ESTEJA ATIVO */
        if (!hasPackageUsageStatsPermission() && isNoOption()) {
            //Abre a tela de configuracao para ativar "Acesso de uso" para o App
            showPackageUsageStatsPermissionDialog()
        }
    }

    @SuppressLint("InflateParams")
    private fun showPackageUsageStatsPermissionDialog() {
        val customView =
            layoutInflater.inflate(R.layout.dialog_alert_custom_one_button, null)
        val dialog = createCustomAlertDialog(
            customView = customView,
            customBackgroundId = R.drawable.dialog_alert_custom_rounded_border,
            styleId = R.style.MyDialogStyle
        )

        customView.setupDialog(
            title = getString(R.string.generic_permission),
            body = getString(R.string.permission_package_usage_stats_require_message),
            icon = R.drawable.ic_alert,
            acceptButtonText = getString(R.string.generic_ok),
            acceptAction = {
                goToPackagesUsageStatsSettings()
                dialog.dismiss()
            },
        )

        dialog.show()
    }

    @SuppressLint("InflateParams")
    private fun showWhyPermissionsAreNeededDialog(recursiveFunction: () -> Unit) {
        val customView =
            layoutInflater.inflate(R.layout.dialog_alert_custom_one_button, null)
        val dialog = createCustomAlertDialog(
            customView = customView,
            customBackgroundId = R.drawable.dialog_alert_custom_rounded_border,
            styleId = R.style.MyDialogStyle
        )

        customView.setupDialog(
            title = getString(R.string.generic_attention),
            body = getString(R.string.permission_require_message),
            icon = R.drawable.ic_alert,
            acceptButtonText = getString(R.string.generic_ok),
            acceptAction = {
                dialog.dismiss()
                recursiveFunction()
            },
        )

        dialog.show()
    }

    private fun goToPackagesUsageStatsSettings() {
        requestPackageUsagePermissionLauncher.launch(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }

    private fun goToInfoAppSettings() {
        requestPermissionInfoAppLauncher.launch(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
        )
    }

    private fun logout() {
        /** PROCURA POR OUTRO APP COM CONFIGURACAO LAUNCHER, ABRE ELE E FINALIZA O GPSHub */
        for (resolveInfo in packageManager.queryIntentActivities(
            Intent("android.intent.action.MAIN")
                .addCategory("android.intent.category.HOME"), PackageManager.MATCH_DEFAULT_ONLY
        )) {

            if (packageName != resolveInfo.activityInfo.packageName) {
                val intent = Intent("android.intent.action.MAIN")
                intent.addCategory("android.intent.category.DEFAULT")
                intent.addCategory("android.intent.category.HOME")
                intent.addCategory("android.intent.category.LAUNCHER")
                intent.setClassName(
                    resolveInfo.activityInfo.packageName,
                    resolveInfo.activityInfo.name
                )
                startActivity(intent)
                break
            }
        }
        finish()
    }
}
