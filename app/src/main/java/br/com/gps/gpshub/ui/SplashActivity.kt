package br.com.gps.gpshub.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import br.com.gps.gpshub.R
import br.com.gps.gpshub.databinding.ActivitySplashBinding
import br.com.gps.gpshub.extensions.*
import dagger.hilt.android.AndroidEntryPoint


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var _binding: ActivitySplashBinding
    private val binding get() = _binding

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var requestPackageUsagePermissionLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionInfoAppLauncher: ActivityResultLauncher<Intent>


    private val requestPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                val delay = 500L // MAXIMO POSSIVEL PELA API 1s ou 1000L ANDROID 12
                override fun onPreDraw(): Boolean {
                    Thread.sleep(delay)
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    return true
                }
            }
        )

        initSetup()
    }


    private fun initSetup() {
        setupAnimation()
        setupPermissionsLaunchers()
    }

    private fun setupAnimation() {
        binding.iconGpsImageView
            .showAnimation(duration = 3000) {
                checkPermissionGranted()
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
                if (hasPackageUsageStatsPermission()) {
                    goToMain()

                } else showPackageUsageStatsPermissionDialog()
            }

        requestPermissionInfoAppLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                checkPermissionGranted()
            }

    }

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
        } else {
            goToMain()
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

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
