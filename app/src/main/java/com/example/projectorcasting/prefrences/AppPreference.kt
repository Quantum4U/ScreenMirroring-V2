package com.example.projectorcasting.prefrences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPreference @Inject constructor(@ApplicationContext context: Context) {

    private var context: Context
    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    companion object {
        private const val KEY_SERVER_OPEN = "KEY_SERVER_OPEN"
    }

    init {
        setPreferences(PreferenceManager.getDefaultSharedPreferences(context))
        editor = getPreferences()!!.edit()
        this.context = context
    }

    private fun getPreferences(): SharedPreferences? {
        return preferences
    }

    private fun setPreferences(preferences: SharedPreferences) {
        this.preferences = preferences
    }


    fun isServerOpen(): Boolean? {
        return getPreferences()?.getBoolean(KEY_SERVER_OPEN, false)
    }

    fun setServerOpen(value: Boolean) {
        editor!!.putBoolean(KEY_SERVER_OPEN, value)
        editor!!.apply()
    }

}