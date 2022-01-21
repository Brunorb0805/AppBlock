package br.com.gps.gpshub.extensions

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar


/** ESCONDE O TECLADO  */
fun Activity.hideKeyboard(view: View? = window?.decorView?.rootView) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        view?.hideKeyboard(view)
    } else {
        inputMethodManager()?.hideSoftInputFromWindow(view?.applicationWindowToken, 0)
    }
}

/** EXIBE O TECLADO */
fun Activity.showKeyboard(view: View? = currentFocus) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        view?.showKeyboard(view)
    } else {
        view?.let {
            it.postDelayed({
                it.requestFocus()
                inputMethodManager()?.showSoftInput(it, InputMethodManager.SHOW_FORCED)
            }, 100)
        }
    }
}

fun Activity.inputMethodManager() =
    getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager


/** VERIFICA SE A PERMISSÃO FOI CONCEDIDA */
fun Activity.hasPermission(permission: String): Boolean {
    val permissionCheckResult = ContextCompat.checkSelfPermission(this, permission)
    return PackageManager.PERMISSION_GRANTED == permissionCheckResult
}

/** VERIFICA SE DEVE SOLICITAR AS PERMISSÕES NOVAMENTE */
fun Activity.shouldRequestPermission(permissions: Array<String>): Boolean {
    val grantedPermissions = mutableListOf<Boolean>()
    permissions.forEach { permission ->
        grantedPermissions.add(hasPermission(permission))
    }
    return grantedPermissions.any { granted -> !granted }
}

/** VERIFICA SE A PERMISSAO DE USO ESTA CONCEDIDA */
@Suppress("DEPRECATION")
fun Activity.hasPackageUsageStatsPermission(): Boolean {
    return try {
        val packageManager: PackageManager = packageManager
        val info = packageManager.getApplicationInfo(packageName, 0)
        val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
//        appOpsManager.checkOpNoThrow(
//            AppOpsManager.OPSTR_GET_USAGE_STATS,
//            info.uid,
//            info.packageName
//        )
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName
            ) == AppOpsManager.MODE_ALLOWED
        } else {
            appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName
            ) == AppOpsManager.MODE_ALLOWED
        }

    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun Activity.isNoOption(): Boolean {
    val packageManager: PackageManager = packageManager
    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    return list.size > 0
}

///** NAVEGA PARA DESTINO PASSADO */
//fun Activity.navTo(@IdRes dest: Int) =
//    findNavController(R.id.nav_host_fragment_content_main).navigate(dest)

/** CRIAR MENSAGEM TEMPORARIA TOAST */
fun Activity.toast(message: String, length: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, length).show()

fun snake(view: View, msg: String) = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()


/** CRIAR ALERTAS TOTALMENTE CUSTOMIZADOS */
fun Activity.createCustomAlertDialog(
    customView: View,
    cancelable: Boolean = false,
    @StyleRes styleId: Int? = null,
    @DrawableRes customBackgroundId: Int? = null
): AlertDialog {
    // Customizar theme do dialog
    val builder = if (styleId != null) MaterialAlertDialogBuilder(
        ContextThemeWrapper(this, styleId)
    ) else MaterialAlertDialogBuilder(this)

    builder.setView(customView)
    val dialog = builder.create()

    dialog.setCancelable(cancelable)

    // Customizar fundo do dialog
    if (customBackgroundId != null) {
        dialog.window?.setBackgroundDrawableResource(customBackgroundId)
    }
    return dialog
}

///** EXIBIR UM ALERTA DE MENSAGENS FULLSCREEN PERSONALIZADO */
//fun Fragment.showFullscreenAlertDialog(
//    title: String,
//    message: String,
//    positiveButtonLabel: String = getString(android.R.string.ok),
//    positiveButtonClickListener: () -> Unit = {},
//    cancelButtonLabel: String? = null,
//    negativeButtonClickListener: () -> Unit = {},
//    dismissAction: () -> Unit = {},
//) = FullscreenAlertDialog(
//    title = title,
//    message = message,
//    positiveLabel = positiveButtonLabel,
//    positiveAction = positiveButtonClickListener,
//    cancelLabel = cancelButtonLabel,
//    cancelAction = negativeButtonClickListener,
//    dismissAction = dismissAction,
//).also { it.show(parentFragmentManager, it.javaClass.simpleName) }