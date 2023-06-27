package com.example.projectorcasting.casting.service

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.work.*
import com.example.projectorcasting.casting.utils.CastHelper
import io.github.dkbai.tinyhttpd.nanohttpd.webserver.SimpleWebServer

class WebService(private val context: Context,workerParameters: WorkerParameters) : Worker(context,workerParameters) {
    private val TAG = javaClass.simpleName

    companion object {
        private const val YOUR_PARAM = "YOUR_PARAM"
        const val UNIQUE_WORK_NAME = "CAST_WORK"

        fun buildWorkRequest(yourParameter: String): OneTimeWorkRequest {
            val data = Data.Builder().putString(YOUR_PARAM, yourParameter).build()
            return OneTimeWorkRequestBuilder<WebService>().apply { setInputData(data) }.build()
        }
    }


    override fun doWork(): Result {
        SimpleWebServer.stopServer()
        try {
            /** Running a server on Internal storage.
             *
             * I know the method [Environment.getExternalStorageDirectory] is deprecated
             * but it is needed to start the server in the required path.
             */

            SimpleWebServer.runServer(context,
                arrayOf(
                    "-h",
                    CastHelper.deviceIpAddress,
                    "-p 9999",
                    "-d",
                    Environment.getExternalStorageDirectory().absolutePath
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}", e)
        }

        return Result.success()
    }

    override fun onStopped() {
        SimpleWebServer.stopServer()
        super.onStopped()
    }
}