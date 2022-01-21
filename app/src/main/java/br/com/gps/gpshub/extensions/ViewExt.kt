package br.com.gps.gpshub.extensions

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.animation.BounceInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import br.com.gps.gpshub.R
import com.google.android.material.textfield.TextInputLayout

@RequiresApi(Build.VERSION_CODES.R)
        /** ESCONDE O TECLADO */
fun View.showKeyboard(view: View) {
    windowInsetsController?.show(WindowInsets.Type.ime())
    view.requestFocus()
}

@RequiresApi(Build.VERSION_CODES.R)
        /** EXIBE O TECLADO */
fun View.hideKeyboard(view: View) {
    windowInsetsController?.hide(WindowInsets.Type.ime())
    view.clearFocus()
}

fun View?.disableClick() {
    this?.let {
        it.isEnabled = false
        Handler(Looper.getMainLooper()).postDelayed({
            it.isEnabled = true
        }, 1500)
    }
}

/** ANIMACAO DE VIBRACAO */
fun TextInputLayout.shake(onEndAction: () -> Unit = {}) {
    val startX = 0f
    val translationX = 35f
    val bounceDuration = 700L

    ObjectAnimator.ofFloat(
        this,
        "translationX",
        startX,
        translationX,
        startX
    ).apply {
        interpolator = BounceInterpolator()
        duration = bounceDuration
        start()
    }.doOnEnd { onEndAction() }
}

/** CONFIGURACAO DOS CUSTOMS ALERT DIALOG */
fun View.setupDialog(
    title: String,
    body: String,
    icon: Int,
    acceptButtonText: String = "Aceitar",
    acceptAction: () -> Unit = {},
    cancelButtonText: String = "Cancelar",
    cancelAction: () -> Unit = {}
) {

    with(this) {
        findViewById<ImageView>(R.id.custom_dialog_icon).setImageResource(icon)
        findViewById<TextView>(R.id.custom_dialog_title).text = title
        findViewById<TextView>(R.id.custom_dialog_message).text = body
        findViewById<Button>(R.id.cancel_button)?.apply {
            text = cancelButtonText
            setOnClickListener {
                cancelAction()
            }
        }
        findViewById<Button>(R.id.accept_button)?.apply {
            text = acceptButtonText
            setOnClickListener {
                acceptAction()
            }
        }
    }
}