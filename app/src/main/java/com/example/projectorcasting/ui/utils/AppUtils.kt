package com.example.projectorcasting.ui.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat.startActivityForResult

object AppUtils {

    fun openWifiPopUpInApp(activity: Activity) {
        try {
            val wifiManager = activity.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                wifiManager.isWifiEnabled = true
            } else {
                val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
                activity.startActivity(panelIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}