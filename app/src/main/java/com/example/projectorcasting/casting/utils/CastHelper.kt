package com.example.projectorcasting.casting.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.mediarouter.media.MediaRouter
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.example.projectorcasting.R
import com.example.projectorcasting.casting.service.WebService

object CastHelper {

    var deviceIpAddress: String? = null

    fun getAvailableDevices(mMediaRouter: MediaRouter): MutableList<MediaRouter.RouteInfo> {
        return mMediaRouter.routes
    }

    fun playMedia(context: Context?,view: View,path: String,thumb: String,type: Int) {
        /** Find the IpAddress of the device and save it to [deviceIpAddress]
         *  so that Service class can pick it up to create a small http server */
        deviceIpAddress = context?.let { Utils.findIPAddress(it) }

        if (deviceIpAddress == null) {
            Toast.makeText(
                context,
                context?.getString(R.string.connect_to_wifi),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        /** Start a http server. */
        val workManager = context?.let { WorkManager.getInstance(it) }

        val exampleWorkRequest = WebService.buildWorkRequest("")
        workManager?.enqueueUniqueWork(
            WebService.UNIQUE_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            exampleWorkRequest
        )

        Utils.showQueuePopup(context, view, Utils.buildMediaInfo(path,thumb,type))
    }
}