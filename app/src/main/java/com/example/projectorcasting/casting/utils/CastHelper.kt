package com.example.projectorcasting.casting.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.mediarouter.media.MediaRouter
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.example.projectorcasting.R
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.casting.service.WebService
import com.example.projectorcasting.models.MediaData
import com.google.android.gms.cast.CastDevice
import java.io.File
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction1

object CastHelper {

    var deviceIpAddress: String? = null
    private const val CHROMECAST_SIGNATURE = "cast.media.CastMediaRouteProviderService"

    fun getAvailableDevices(mMediaRouter: MediaRouter): ArrayList<CastModel> {
        val routes = mMediaRouter.routes

        var devices = ArrayList<CastModel>()

        for (routeInfo in routes) {
            Log.d("CastHelper", "getAvailableDevices A13 : >>"+routeInfo)
            val device = CastDevice.getFromBundle(routeInfo.extras)
            if (device != null) {
                devices.add(CastModel(routeInfo, device))
            }
        }


        return devices
    }

    fun getCastEnabled(
        mMediaRouter: MediaRouter,
        castingEnabledCallback: KFunction2<Boolean, CastDevice?, Unit>
    ) {
        var isConnected = false
        var device: CastDevice? = null
        for (route in mMediaRouter.routes) {
            Log.d("CastHelper", "getCastEnabled A13 : >>"+isCastDevice(route))
            if (isCastDevice(route)) {
                if (route.connectionState == MediaRouter.RouteInfo.CONNECTION_STATE_CONNECTED) {
                    Log.d("CastHelper", "getCastEnabled A13 : "+route.name)
                    device = CastDevice.getFromBundle(route.extras)
                    Log.d("CastHelper", "getCastEnabled A13 : "+device)
                    isConnected = true
                    break
                } else {
                    isConnected = false
                }
            }
        }

        castingEnabledCallback(isConnected, device)
    }

    private fun isCastDevice(routeInfo: MediaRouter.RouteInfo): Boolean {
        return routeInfo.id.contains(CHROMECAST_SIGNATURE)
    }

    fun playMedia(
        context: Context?,
        mediaData: MediaData?,
        path: String,
        thumb: String,
        type: Int,
        checkForQueue: KFunction1<Int, Unit>
    ) {
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

        Utils.showQueuePopup(context,Utils.buildMediaInfo(mediaData,path, thumb, type),checkForQueue)
    }
}