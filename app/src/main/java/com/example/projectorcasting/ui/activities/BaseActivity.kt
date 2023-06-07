package com.example.projectorcasting.ui.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.mediarouter.media.MediaRouter
import androidx.viewbinding.BuildConfig
import com.example.projectorcasting.R
import com.example.projectorcasting.viewmodels.DashboardViewModel
import com.google.android.gms.cast.CastDevice
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.CastState
import com.google.android.gms.cast.framework.SessionManagerListener
import dagger.hilt.android.AndroidEntryPoint
import io.github.dkbai.tinyhttpd.nanohttpd.webserver.SimpleWebServer
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
open class BaseActivity @Inject constructor() : AppCompatActivity() {

    //    abstract fun bindingView(): ViewBinding?
    private val dashboardViewModel: DashboardViewModel? by viewModels()

    var alertDialogProgressBar: Dialog? = null
    private var dialog: Dialog? = null

    private val REQUEST_CODE = 100

    //casting variables
    private var mSessionManagerListener: SessionManagerListener<CastSession>? = null
    private var mCastSession: CastSession? = null
    private var mCastContext: CastContext? = null
    private var mMediaRouter: MediaRouter? = null
    private var isConnected: Boolean? = null
    private var connectedDeviceName: String? = null
    private var castDevice: CastDevice? = null

    private val isCastingEnabledLiveData = MutableLiveData<Int>()
    val data: LiveData<Int> = isCastingEnabledLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView()
        establishCastSession()
    }

    private fun establishCastSession() {
        /** Required to initialize the server */
        SimpleWebServer.init(this, BuildConfig.DEBUG)

        /** Set up cast listener as suggested in official documentation */
        setUpCastListener()

        /** Set up cast context as suggested in official documentation */
        mCastContext = CastContext.getSharedInstance(this)
        mCastSession = mCastContext?.sessionManager?.currentCastSession

        mCastContext?.addCastStateListener { state ->
            /** Show an introductory overlay to notify user that
             *  there is a cast device available to connect.
             */

            if (state == CastState.CONNECTING) {
                showLoader()
            }

            Log.d("BaseActivity", "establishCastSession A13 : >>" + state)
            if (state == CastState.CONNECTED) {
                isConnected = true
                hideLoader()
            } else if (state == CastState.NOT_CONNECTED) {
                isConnected = false
                hideLoader()
            }

            isCastingEnabledLiveData.postValue(state)
        }

        /** Set session manager listener, this listener consist various methods
         *  which will be invoked when something changes in cast like
         *  Start, Resume, End, etc listener.
         */

        mSessionManagerListener?.let {
            mCastContext?.sessionManager?.addSessionManagerListener(it, CastSession::class.java)
        }

        if (mMediaRouter == null)
            mMediaRouter = MediaRouter.getInstance(this)
    }

    private fun setUpCastListener() {
        mSessionManagerListener = object : SessionManagerListener<CastSession> {
            override fun onSessionStarted(session: CastSession, p1: String) {
                onApplicationConnected(session)
            }

            override fun onSessionResumeFailed(p0: CastSession, p1: Int) {
                onApplicationDisconnected()
            }

            override fun onSessionEnded(p0: CastSession, p1: Int) {
                Log.d("MySessionManagerener", "onSessionEnded A13 11: >.")
                onApplicationDisconnected()
            }

            override fun onSessionResumed(session: CastSession, p1: Boolean) {
                onApplicationConnected(session)
            }

            override fun onSessionStartFailed(p0: CastSession, p1: Int) {
                onApplicationDisconnected()
            }

            override fun onSessionSuspended(p0: CastSession, p1: Int) {}

            override fun onSessionStarting(p0: CastSession) {}

            override fun onSessionResuming(p0: CastSession, p1: String) {}

            override fun onSessionEnding(p0: CastSession) {}

            private fun onApplicationConnected(castSession: CastSession) {
                mCastSession = castSession
                Log.d("CastHelper", "getCastEnabled A13 : >>"+ castSession.castDevice.deviceId)
                castDevice = castSession.castDevice
                connectedDeviceName = castSession.castDevice?.modelName
//                invalidateOptionsMenu() // This is required to refresh the activity toolbar to display cast connect button.
            }

            private fun onApplicationDisconnected() {
//                invalidateOptionsMenu() // This is required to refresh the toolbar after disconnect.
            }
        }
    }

    fun getMediaRouter(): MediaRouter? {
        return mMediaRouter
    }

    fun startCasting(routeInfo: MediaRouter.RouteInfo?, device: CastDevice?) {
        castDevice = device
        connectedDeviceName = device?.modelName
        routeInfo?.let { mMediaRouter?.selectRoute(it) }
    }

//    fun startCasting() {
//        mMediaRouter?.selectRoute(mMediaRouter?.routes!![1])
//    }

    fun stopCasting() {
        mMediaRouter?.unselect(MediaRouter.UNSELECT_REASON_DISCONNECTED)
        mCastSession?.remoteMediaClient?.stop()
        SimpleWebServer.stopServer()
    }

    fun isCastingConnected(): Boolean? {
        return isConnected
    }

    fun getConnectedDeviceName(): String? {
        return connectedDeviceName
    }

    fun setConnectionInfo(isConnected: Boolean, device: CastDevice?) {
        this.isConnected = isConnected
        this.connectedDeviceName = device?.modelName
        this.castDevice = device
    }

    fun getConnectedCastDevice(): CastDevice? {
        return castDevice
    }

    fun getCastContext(): CastContext? {
        return mCastContext
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
    }

    fun showLoader() {
        try {
            if (alertDialogProgressBar != null) {
                alertDialogProgressBar?.dismiss()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        try {
            alertDialogProgressBar = Dialog(this)
            alertDialogProgressBar?.setContentView(R.layout.progress_dialoag_layout)
            alertDialogProgressBar?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            alertDialogProgressBar?.setCancelable(false)
            alertDialogProgressBar?.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun hideLoader() {
        try {
            if (alertDialogProgressBar != null && isLoaderShowing()) {
                alertDialogProgressBar?.dismiss()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun isLoaderShowing(): Boolean {
        return alertDialogProgressBar?.isShowing == true
    }

    fun checkStoragePermission(): Boolean {
        val readImagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(
                this,
                readImagePermission
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            if (requestCode == REQUEST_CODE) {
                if (!checkStoragePermission()) {
                    var message = ""
                    message = if (!shouldRequestPermissionRationale(permissions)) {
                        resources.getString(R.string.dont_ask_permission_header)
                    } else {
                        resources.getString(R.string.permission_header)
                    }
                    showADialog(message,
                        resources.getString(R.string.grant),
                        resources.getString(R.string.deny),
                        object : ADialogClicked {
                            override fun onPositiveClicked(dialog: DialogInterface?) {
                                if (!shouldRequestPermissionRationale(permissions)) { //go to setting page of app
                                    val intent = Intent()
                                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                    val uri = Uri.fromParts(
                                        "package", packageName,
                                        null
                                    )
                                    intent.data = uri
                                    startActivityForResult(intent, 200)
                                } else {
                                    requestPermissions(permissions, REQUEST_CODE)
                                }
                                dialog?.dismiss()
                            }

                            override fun onNegativeClicked(dialog: DialogInterface?) {
                                dialog?.dismiss()
                            }
                        })
                } else {
//                    onPermissionListener?.onPermissionGranted()
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun shouldRequestPermissionRationale(permissions: Array<String?>): Boolean {
        var showRationale = false
        var i = 0
        val len = permissions.size
        while (i < len) {
            val permission = permissions[i]
            showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission!!)
            if (showRationale) return true
            i++
        }
        return showRationale
    }

    open interface ADialogClicked {
        fun onPositiveClicked(dialog: DialogInterface?)
        fun onNegativeClicked(dialog: DialogInterface?)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    open fun showADialog(
        message: String,
        buttonPositive: String?,
        buttonNegative: String?,
        l: ADialogClicked
    ) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setMessage("" + message)
        builder.setCancelable(true)
        builder.setPositiveButton(buttonPositive) { dialog: DialogInterface?, id: Int ->
            l.onPositiveClicked(dialog)
        }
        builder.setNegativeButton(buttonNegative) { dialog: DialogInterface?, id: Int ->
            l.onNegativeClicked(dialog)
        }
        builder.setOnCancelListener { dialog: DialogInterface? -> }
        val dialog = builder.create()
        try {
            dialog.setCanceledOnTouchOutside(false)
            if (!isFinishing) {
                dialog.show()
                val positiveButton: Button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                positiveButton.setTextColor(resources.getColor(R.color.black, null))
                val negativeButton: Button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                negativeButton.setTextColor(resources.getColor(R.color.black, null))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun verifyPermissions() {
        val readImagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE

        ActivityCompat.requestPermissions(
            this,
            arrayOf(readImagePermission), REQUEST_CODE
        )
    }

    fun hideKeyBoard(view: View?) {
        if (view == null) {
            return
        }
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showFullAds(activity: Activity) {
//        AHandler.getInstance().showFullAds(activity, false)
    }

    fun showBottomBannerAds(view: LinearLayout?, activity: Activity?) {
//        view?.addView(AHandler.getInstance().getBannerHeader(activity))
    }

    fun showNativeMedium(view: LinearLayout?, activity: Activity?) {
//        view?.addView(AHandler.getInstance().getNativeMedium(activity))
    }

//    fun isAdsEnabled(): Boolean {
//        return !Slave.hasPurchased(this)
//    }


    override fun onPause() {
        super.onPause()
    }

    fun getDashboardViewmodel(): DashboardViewModel? {
        return dashboardViewModel
    }

    fun startMirroring() {
//        if (Utils.isNetworkConnected(this)) {
        try {
            startActivity(Intent("android.settings.CAST_SETTINGS"))
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.device_not_supported), Toast.LENGTH_LONG)
                .show()
        }
//        } else {
//            Toast.makeText(this, getString(R.string.required_wifi_network), Toast.LENGTH_SHORT)
//                .show()
//            AppUtils.openWifiPopUpInApp(this)
//        }
    }


}


