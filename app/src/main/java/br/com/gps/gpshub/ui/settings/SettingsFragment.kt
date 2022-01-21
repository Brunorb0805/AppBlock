package br.com.gps.gpshub.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import br.com.gps.gpshub.R
import br.com.gps.gpshub.extensions.toast
import br.com.gps.gpshub.other.BackgroundManager
import br.com.gps.gpshub.other.Constants
import br.com.gps.gpshub.other.SharedPrefUtil
import br.com.gps.gpshub.services.LockService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var mBackgroundManager: BackgroundManager


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences, key: String) {
        val preference: Preference? = findPreference(key)

        when (key) {
            getString(R.string.id_theme) -> {
                (preference as ListPreference).value.let {
                    AppCompatDelegate.setDefaultNightMode(it.toInt())
                    requireActivity().recreate()
                }
            }

            getString(R.string.id_lock) -> {
                (preference as SwitchPreferenceCompat).isChecked.let {
                    if (it) {
                        mBackgroundManager.stopService(LockService::class.java)
                        mBackgroundManager.startService(LockService::class.java)
                        mBackgroundManager.startAlarmManager()
                    } else {
                        mBackgroundManager.stopService(LockService::class.java)
                        mBackgroundManager.stopAlarmManager()
                    }
                }
            }

            getString(R.string.id_block_with_screen) -> {
                toast(
                    if ((preference as SwitchPreferenceCompat).isChecked)
                        getString(R.string.generic_active)
                    else getString(R.string.generic_disable)
                )
            }

            getString(R.string.id_block_time_value) -> {
                (preference as ListPreference).let {
                    toast(it.value)

                    SharedPrefUtil.instance.putBoolean(
                        Constants.LOCK_AUTO_SCREEN_TIME,
                        it.entry != "Imediatamente"
                    )
                }
            }
        }
    }
}
