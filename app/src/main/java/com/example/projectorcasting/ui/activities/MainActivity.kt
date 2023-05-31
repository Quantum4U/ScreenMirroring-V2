package com.example.projectorcasting.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import com.example.projectorcasting.R
import com.example.projectorcasting.databinding.ActivityMainBinding
import com.kpstv.tinyhttpd.BuildConfig
import dagger.hilt.android.AndroidEntryPoint
import io.github.dkbai.tinyhttpd.nanohttpd.webserver.SimpleWebServer

@AndroidEntryPoint
class MainActivity : BaseActivity() , View.OnClickListener{

    private lateinit var binding: ActivityMainBinding

    private var navController: NavController? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var drawerLayout: DrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** Required to initialize the server */
        SimpleWebServer.init(this, BuildConfig.DEBUG)

        drawerLayout = binding.drawerLayout
//        val navView: NavigationView = binding.navView

         navController = findNavController(R.id.nav_host_fragment_content_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
//            ), drawerLayout
//        )

        navController?.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.nav_dash) {
                drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)

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
//                logGAEvents(AnalyticsConstant.GA_Menu_RateUs)
//                PromptHander().rateUsDialog(true, this)
                closeDrawer()
            }

            R.id.menuShare -> {
//                logGAEvents(AnalyticsConstant.GA_Menu_ShareApp)
//                Utils().shareUrl(this)
                closeDrawer()
            }

            R.id.menuMoreApps -> {
//                logGAEvents(AnalyticsConstant.GA_Menu_MoreApps)
//                Utils().moreApps(this)
                closeDrawer()
            }

            R.id.menuFeedBack -> {
//                logGAEvents(AnalyticsConstant.GA_Menu_ShareFeedback)
//                Utils().sendFeedback(this)
                closeDrawer()
            }

            R.id.menuaboutus -> {
//                logGAEvents(AnalyticsConstant.GA_Menu_AboutUs)
//                AHandler.getInstance().showAboutUs(this)
                closeDrawer()
            }

            R.id.menuexit -> {
//                logGAEvents(AnalyticsConstant.GA_Menu_AboutUs)
//                AHandler.getInstance().showAboutUs(this)
                closeDrawer()
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
//            AHandler.getInstance().v2ManageAppExit(this,binding.appBarMain.rootLayout)
            finish()
        }
    }

    private fun navigateAccordingMapper(keyValue: String?) {
//        if (intent != null) {
//
//            val value: String? = keyValue ?: intent.getStringExtra(MapperUtils.keyValue)
//            val type = this.intent.getStringExtra(MapperUtils.keyType)
//
//            if (type != null && value != null) {
//                when (value) {
//                    MapperUtils.DASHBOARD_GALLERY -> openGallery()
//                    MapperUtils.DASHBOARD_TEXT -> navController?.navigate(R.id.navigation_text)
//                    MapperUtils.DASHBOARD_WEBPAGE -> navController?.navigate(R.id.navigation_webpage)
//                    MapperUtils.DASHBOARD_MAIL -> navController?.navigate(R.id.navigation_email)
//                    MapperUtils.DASHBOARD_PDF -> openPdfPicker()
//                }
//            }
//        }
    }

    override fun onResume() {
        super.onResume()
//        inAppUpdateManager?.checkNewAppVersionState()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == InAppUpdateManager.REQ_CODE_VERSION_UPDATE) {
//            if (resultCode != RESULT_OK) {
//                inAppUpdateManager?.unregisterInstallStateUpdListener()
//                onUpdateNotAvailable()
//            }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()

//        try {
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
//        } catch (e: java.lang.Exception) {
//        }
    }

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
//            val type = intent.getStringExtra(MapperUtils.keyType)
//            val value = intent.getStringExtra(MapperUtils.keyValue)
//            if (type != null && value != null) {
//                callingForMapper(type, value)
//            }

        }
    }

    private fun callingForMapper(type: String?, value: String?) {
//        try {
//            if (type != null && value != null) {
////                Log.d("EXitPageWithType", "Checking ExitPage On Mapper Calling..$type  $value");
//                if (type.equals("deeplink", ignoreCase = true)) {
//                    when (value) {
//                        MapperUtils.gcmMoreApp,
//                        MapperUtils.gcmRateApp,
//                        MapperUtils.gcmRemoveAds,
//                        MapperUtils.gcmFeedbackApp,
//                        MapperUtils.gcmShareApp,
//                        MapperUtils.gcmForceAppUpdate -> AHandler.getInstance()
//                            .callingForDeeplinking(this, value)
//                        else -> navigateAccordingMapper(value)
//                    }
//                }
//            }
//        } catch (e: java.lang.Exception) {
//            //            println("AHandler.callingForMapper excep " + e.message)
//        }
    }



}