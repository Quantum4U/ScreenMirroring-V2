package com.example.projectorcasting.ui.activities

import android.Manifest
import android.annotation.TargetApi
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
import android.os.PersistableBundle
import android.provider.Settings
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
import com.example.projectorcasting.R
import com.example.projectorcasting.ui.utils.AppUtils
import com.example.projectorcasting.viewmodels.DashboardViewModel
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
open class BaseActivity @Inject constructor() : AppCompatActivity() {

    private val dashboardViewModel: DashboardViewModel? by viewModels()

    var alertDialogProgressBar: Dialog? = null
    private var dialog: Dialog? = null

    private val REQUEST_CODE = 100

    //casting variables
    private var mSessionManagerListener: SessionManagerListener<CastSession>? = null
    private var mCastSession: CastSession? = null
    private var mCastContext: CastContext? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

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


