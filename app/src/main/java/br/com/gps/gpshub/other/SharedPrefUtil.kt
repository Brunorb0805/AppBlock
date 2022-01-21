package br.com.gps.gpshub.other

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager


class SharedPrefUtil {

    private lateinit var mPref: SharedPreferences


    companion object {

        private var mInstance: SharedPrefUtil? = null

        @get:Synchronized
        val instance: SharedPrefUtil
            get() {
                synchronized(SharedPrefUtil::class.java) {
                    if (mInstance == null) {
                        mInstance = SharedPrefUtil()
                    }
                }

                return mInstance!!
            }
    }


    fun init(context: Application) {
        mPref = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun putString(key: String?, value: String?) {
        val editor = mPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun putLong(key: String?, value: Long) {
        val editor = mPref.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun putInt(key: String?, value: Int) {
        val editor = mPref.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun putBoolean(key: String?, value: Boolean) {
        val editor = mPref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String?): Boolean {
        return mPref.getBoolean(key, false)
    }

    fun getBoolean(key: String?, def: Boolean): Boolean {
        return mPref.getBoolean(key, def)
    }

    fun getString(key: String?): String? {
        return mPref.getString(key, "")
    }

    fun getString(key: String?, def: String?): String? {
        return mPref.getString(key, def)
    }

    fun getLong(key: String?): Long {
        return mPref.getLong(key, 0L)
    }

    fun getLong(key: String?, defInt: Long): Long {
        return mPref.getLong(key, defInt)
    }

    fun getInt(key: String?): Int {
        return mPref.getInt(key, 0)
    }

    fun getInt(key: String?, defInt: Int): Long {
        return mPref.getInt(key, defInt).toLong()
    }

    fun contains(key: String?): Boolean {
        return mPref.contains(key)
    }

    fun remove(key: String?) {
        val editor = mPref.edit()
        editor.remove(key)
        editor.apply()
    }

    fun clear() {
        val editor = mPref.edit()
        editor.clear()
        editor.apply()
    }

}