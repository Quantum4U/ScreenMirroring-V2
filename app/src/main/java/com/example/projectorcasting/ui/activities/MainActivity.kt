package com.example.projectorcasting.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.projectorcasting.AnalyticsConstant
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.example.projectorcasting.casting.utils.Utils
import com.example.projectorcasting.utils.AppUtils
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import engine.app.adshandler.AHandler
import engine.app.adshandler.PromptHander
import engine.app.analytics.logGAEvents
import engine.app.fcm.MapperUtils
import engine.app.inapp.InAppUpdateManager
import engine.app.listener.InAppUpdateListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity(), View.OnClickListener, InAppUpdateListener {

    private lateinit var binding: ActivityMainBinding

    private var navController: NavController? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var drawerLayout: DrawerLayout? = null

    private var isImagePageBtnClicked = false
    private var isVideoPageBtnClicked = false
    private var isAudioPageBtnClicked = false
    private var checkForPermission = false

    private var inAppUpdateManager: InAppUpdateManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (inAppUpdateManager == null) inAppUpdateManager = InAppUpdateManager(this)
        inAppUpdateManager?.checkForAppUpdate(this)

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter("Exit_Mapper_For_App"))


        drawerLayout = binding.drawerLayout
        navController = findNavController(R.id.nav_host_fragment_content_main)

        navController?.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.nav_dash) {
                drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            if (destination.id == R.id.nav_image_preview) {
                statusBarColor(true, window)
            } else {
                statusBarColor(false, window)
            }
        }

        binding.menuRateUs.setOnClickListener(this)
        binding.menuShare.setOnClickListener(this)
        binding.menuMoreApps.setOnClickListener(this)
        binding.menuFeedBack.setOnClickListener(this)
        binding.menuaboutus.setOnClickListener(this)
        binding.menuexit.setOnClickListener(this)

        navigateAccordingMapper(null)

        showBottomBannerAds(binding.appBarMain.contentMain.adsbanner, this)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onClick(view: View?) {
        when (view?.id) {

            R.id.menuRateUs -> {
                logGAEvents(AnalyticsConstant.GA_Menu_RateUs)
                PromptHander().rateUsDialog(true, this)
                closeDrawer()
            }

            R.id.menuShare -> {
                logGAEvents(AnalyticsConstant.GA_Menu_ShareApp)
                engine.app.serviceprovider.Utils().shareUrl(this)
                closeDrawer()
            }

            R.id.menuMoreApps -> {
                logGAEvents(AnalyticsConstant.GA_Menu_MoreApps)
                engine.app.serviceprovider.Utils().moreApps(this)
                closeDrawer()
            }

            R.id.menuFeedBack -> {
                logGAEvents(AnalyticsConstant.GA_Menu_Feedback)
                engine.app.serviceprovider.Utils().sendFeedback(this)
                closeDrawer()
            }

            R.id.menuaboutus -> {
                logGAEvents(AnalyticsConstant.GA_Menu_AboutUs)
                AHandler.getInstance().showAboutUs(this)
                closeDrawer()
            }

            R.id.menuexit -> {
                logGAEvents(AnalyticsConstant.GA_Menu_Exit)
                closeDrawer()
                exitApp()
            }
        }
    }

    fun openDrawer() {
        drawerLayout?.openDrawer(GravityCompat.START)
    }

    private fun closeDrawer() {
        if (drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            exitApp()
        }
    }

    private fun exitApp() {
        AHandler.getInstance().v2ManageAppExit(this,binding.appBarMain.rootLayout)
    }

    private fun navigateAccordingMapper(keyValue: String?) {
        if (intent != null) {

            val value: String? = keyValue ?: intent.getStringExtra(MapperUtils.keyValue)
            val type = this.intent.getStringExtra(MapperUtils.keyType)

            if (type != null && value != null) {
                when (value) {
                    MapperUtils.DL_IMAGES -> openImagePage()
                    MapperUtils.DL_VIDEOS -> openVideoPage()
                    MapperUtils.DL_AUDIOS -> openAudioPage()
                }
            }
        }
    }

    fun openImagePage() {
        if (!checkStoragePermission(Utils.IMAGE)) {
            checkForPermission = true
            isImagePageBtnClicked = true
            verifyPermissions(Utils.IMAGE)
        } else {
            navController?.navigate(R.id.nav_image)
            showFullAds(this)
        }
    }

    fun openVideoPage() {
        if (!checkStoragePermission(Utils.VIDEO)) {
            checkForPermission = true
            isVideoPageBtnClicked = true
            verifyPermissions(Utils.VIDEO)
        } else {
            navController?.navigate(R.id.nav_video)
            showFullAds(this)
        }
    }

    fun openAudioPage() {
        if (!checkStoragePermission(Utils.AUDIO)) {
            checkForPermission = true
            isAudioPageBtnClicked = true
            verifyPermissions(Utils.AUDIO)
        } else {
            navController?.navigate(R.id.nav_audio)
            showFullAds(this)
        }
    }

    override fun onResume() {
        super.onResume()
//        inAppUpdateManager?.checkNewAppVersionState()

        if (checkForPermission) {
            checkForPermission = false

            if (isImagePageBtnClicked && checkStoragePermission(Utils.IMAGE)) {
                isImagePageBtnClicked = false
                openImagePage()
            }

            if (isVideoPageBtnClicked && checkStoragePermission(Utils.VIDEO)) {
                isVideoPageBtnClicked = false
                openVideoPage()
            }

            if (isAudioPageBtnClicked && checkStoragePermission(Utils.AUDIO)) {
                isAudioPageBtnClicked = false
                openAudioPage()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == InAppUpdateManager.REQ_CODE_VERSION_UPDATE) {
            if (resultCode != RESULT_OK) {
                inAppUpdateManager?.unregisterInstallStateUpdListener()
                onUpdateNotAvailable()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        } catch (e: java.lang.Exception) {
        }
    }

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val type = intent.getStringExtra(MapperUtils.keyType)
            val value = intent.getStringExtra(MapperUtils.keyValue)
            if (type != null && value != null) {
                callingForMapper(type, value)
            }
        }
    }

    private fun callingForMapper(type: String?, value: String?) {
        try {
            if (type != null && value != null) {
                if (type.equals("deeplink", ignoreCase = true)) {
                    when (value) {
                        MapperUtils.gcmMoreApp,
                        MapperUtils.gcmRateApp,
                        MapperUtils.gcmRemoveAds,
                        MapperUtils.gcmFeedbackApp,
                        MapperUtils.gcmShareApp,
                        MapperUtils.gcmForceAppUpdate -> AHandler.getInstance()
                            .callingForDeeplinking(this, value)
                        else -> navigateAccordingMapper(value)
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            //            println("AHandler.callingForMapper excep " + e.message)
        }
    }

    override fun onUpdateAvailable() {
//        TODO("Not yet implemented")
    }

    override fun onUpdateNotAvailable() {
        AHandler.getInstance().v2CallonAppLaunch(this)
    }


}