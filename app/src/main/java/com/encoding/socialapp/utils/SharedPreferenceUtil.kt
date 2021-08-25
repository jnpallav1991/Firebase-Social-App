package com.encoding.socialapp.utils

import android.content.Context
import android.content.SharedPreferences


/**
 * This is used for storing data to SharedPreferences
 */
class SharedPreferenceUtil(val context: Context) {

    private val PREFS_NAME = "social"
    private val sharedPref: SharedPreferences? = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun getEditor(): SharedPreferences.Editor {
        return sharedPref!!.edit()
    }

    fun setString(KEY_NAME: String, text: String) {
        getEditor().putString(KEY_NAME, text).commit()
    }

    fun setInteger(KEY_NAME: String, value: Int) {
        getEditor().putInt(KEY_NAME, value).commit()
    }

    fun setBoolean(KEY_NAME: String, status: Boolean) {
        getEditor().putBoolean(KEY_NAME, status).commit()
    }

    fun getString(KEY_NAME: String, defaultValue: String): String? {
        return sharedPref!!.getString(KEY_NAME, defaultValue)
    }

    fun getInteger(KEY_NAME: String, defaultValue: Int): Int {
        return sharedPref!!.getInt(KEY_NAME, defaultValue)
    }

    fun getBoolean(KEY_NAME: String, defaultValue: Boolean): Boolean {
        return sharedPref!!.getBoolean(KEY_NAME, defaultValue)
    }

    fun clearSharedPreference() {
        getEditor().clear().commit()
    }

    fun removeValue(KEY_NAME: String) {
        getEditor().remove(KEY_NAME).commit()
    }
}