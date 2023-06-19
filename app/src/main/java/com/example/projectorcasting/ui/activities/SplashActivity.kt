package com.example.projectorcasting.ui.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.projectorcasting.AnalyticsConstant.GA_Splash_Start
import com.example.projectorcasting.casting.utils.Utils
import com.example.projectorcasting.engine.AppMapperConstant
import com.example.projectorcasting.engine.TransLaunchFullAdsActivity
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.databinding.LayoutSplashActivityBinding
import engine.app.adapter.BillingListAdapterNew
import engine.app.adshandler.AHandler
import engine.app.analytics.logGAEvents
import engine.app.fcm.GCMPreferences
import engine.app.fcm.MapperUtils
import engine.app.listener.OnBannerAdsIdLoaded
import engine.app.listener.OnCacheFullAdLoaded
import engine.app.server.v2.DataHubConstant
import engine.app.server.v2.Slave
import engine.app.utils.EngineConstant
import java.sql.DriverManager

class SplashActivity : BaseActivity(), OnBannerAdsIdLoaded {

    private var mPreference: GCMPreferences? = null
    private var handler: Handler? = null
    private var appLaunch = false
    private var isBannerLoaded = false
    private var reloadsLoaded = false
    private var isFirsthandLoaded = false
    private var firstLaunchHandler: Handler? = null
    private var checkBox: CheckBox? = null

    private var binding: LayoutSplashActivityBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutSplashActivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        doImageFetchingWork()

        checkBox = findViewById(app.pnd.adshandler.R.id.privacy_checkbox)
        if (mPreference == null) mPreference = GCMPreferences(this)

        if (mPreference?.firsttimeString == "true") {
            mPreference?.setFirstTime(true)
        } else {
            mPreference?.setFirstTime(false)
        }

        appLaunch = false

        AHandler.getInstance().v2CallOnSplash(this, object : OnCacheFullAdLoaded {
            override fun onCacheFullAd() {
                Log.d(ContentValues.TAG, "onCacheFullAd: hi ads callinwaaa 001 cache")
                openDashboardThroughFullAdsCaching()
            }

            override fun onCacheFullAdFailed() {
                Log.d(ContentValues.TAG, "onCacheFullAd: hi ads callinwaaa 002 fail")
                openDashboardThroughFullAdsCaching()
            }
        })

        binding?.layoutStart?.setOnClickListener {
            logGAEvents(GA_Splash_Start)
            if (checkBox?.isChecked == true) {
                onInAppWithAdsClick()
            } else {
                Toast.makeText(this, getString(R.string.accept_privacy_policy), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        if (mPreference?.isFirsttime == true /*|| !getCallDoRadoConditions()*/) {
//imageView.setAnimation(animation);
        } else {
            binding?.layoutStart?.visibility = View.GONE
            // textClickBox.setVisibility(View.GONE);
            handler = Handler(Looper.getMainLooper())
            handler?.postDelayed(runnable, 10000)
        }

        mPreference?.isFirsttime?.let {
            engine.app.serviceprovider.Utils().showPrivacyPolicy(
                this, binding?.layoutTnc, it /*|| !getCallDoRadoConditions()*/
            )
        }

        if (engine.app.serviceprovider.Utils.isNetworkConnected(this) && !Slave.hasPurchased(this)) {
            binding?.adsbanner?.addView(AHandler.getInstance().getBannerFooter(this, this))
        }


        if (mPreference?.isFirsttime == true) {
            checkLetStartButton()
        }
    }

    private fun checkLetStartButton() {
        firstLaunchHandler = Handler(Looper.getMainLooper())
        firstLaunchHandler?.postDelayed(firstLaunchRunnable, 10000)
    }

    override fun onBannerFailToLoad() {
        openDashboardThroughBannerLoaded()
    }

    override fun loadandshowBannerAds() {
        openDashboardThroughBannerLoaded()
    }

    private fun openDashboardThroughFullAdsCaching() {
        Log.d(ContentValues.TAG, "onCacheFullAd: hi ads callinwaaa 008 ")
        reloadsLoaded = true
        if (mPreference?.isFirsttime == true && isBannerLoaded) {
            binding?.layoutStart?.visibility = View.VISIBLE
            //            textClickBox.setVisibility(View.VISIBLE);
            try {
                if (firstLaunchHandler != null) {
                    firstLaunchHandler?.removeCallbacks(firstLaunchRunnable)
                }
            } catch (e: Exception) {
                DriverManager.println("exception splash 1 \$e")
            }
        }
        if (!mPreference?.isFirsttime!! && isBannerLoaded) {
            try {
                openDashboardThroughLaunchFullAdsLoaded()
            } catch (e: Exception) {
                DriverManager.println("exception splash 1 \$e")
            }
        }
    }

    private fun onInAppWithAdsClick() {
        launchApp()
        mPreference?.setFirstTime(false)
        mPreference?.firsttimeString = "false"
    }

    private var billingActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 199) {
            openDashboard()
        }
    }

    private fun openDashboardThroughLaunchFullAdsLoaded() {
        Log.d(ContentValues.TAG, "onCacheFullAd: hi ads callinwaaa 006")
        launchApp()
        try {
            if (handler != null) {
                handler?.removeCallbacks(runnable)
            }
        } catch (e: Exception) {
            DriverManager.println("exception splash 1 \$e")
        }
    }

    private fun openDashboardThroughBannerLoaded() {
        Log.d(ContentValues.TAG, "onCacheFullAd: hi ads callinwaaa 005")
        Handler(Looper.getMainLooper()).postDelayed({
            isBannerLoaded = true
            if (mPreference?.isFirsttime == true && reloadsLoaded) {
                binding?.layoutStart?.visibility = View.VISIBLE
                //                    textClickBox.setVisibility(View.VISIBLE);
                try {
                    if (firstLaunchHandler != null) {
                        firstLaunchHandler?.removeCallbacks(firstLaunchRunnable)
                    }
                } catch (e: Exception) {
                    DriverManager.println("exception splash 1 \$e")
                }
            }
            if (!mPreference?.isFirsttime!! && reloadsLoaded) {
                launchApp()
                try {
                    if (handler != null) {
                        handler?.removeCallbacks(runnable)
                    }
                } catch (e: Exception) {
                    DriverManager.println("exception splash 1 \$e")
                }
            }
        }, 1500)
    }

    private fun launchApp() {
        openWithBillingCondition()
    }

    private fun appLaunch(cls: Class<*>) {
        val intent = intent
        val type = intent.getStringExtra(MapperUtils.keyType)
        val value = intent.getStringExtra(MapperUtils.keyValue)
        val packageName = intent.getStringExtra("PackageName")
        println("meenu 123 SplashActivityV3.appLaunch $type $value")
        try {
            if (type != null && value != null) {
                launchAppWithMapper(cls, type, value, packageName)
            } else {
                startActivity(
                    Intent(this@SplashActivity, cls).putExtra(
                        AppMapperConstant.getInstance().FULLADSTYPE,
                        AppMapperConstant.getInstance().Launch_FullAds
                    )
                )
            }
        } catch (e: Exception) {
            startActivity(
                Intent(this@SplashActivity, cls).putExtra(
                    AppMapperConstant.getInstance().FULLADSTYPE,
                    AppMapperConstant.getInstance().Launch_FullAds
                )
            )
        }
    }

    private fun launchAppWithMapper(
        cls: Class<*>, type: String, value: String, packageName: String?
    ) {
        startActivity(
            Intent(this, cls).putExtra(MapperUtils.keyType, type)
                .putExtra(MapperUtils.keyValue, value).putExtra("PackageName", packageName)
                .putExtra(
                    AppMapperConstant.getInstance().FULLADSTYPE,
                    AppMapperConstant.getInstance().Launch_FullAds
                )
        )
    }

    private val runnable = Runnable { launchApp() }

    private val firstLaunchRunnable = Runnable {
        Log.d(ContentValues.TAG, "onCacheFullAd: hi ads callinwaaa 007")
        if (mPreference?.isFirsttime == true || !reloadsLoaded || !isBannerLoaded) {
            isFirsthandLoaded = true
            binding?.layoutStart?.visibility = View.VISIBLE
            //                textClickBox.setVisibility(View.VISIBLE);
        }
    }

    private fun openDashboard() {
        if (!appLaunch) {
            appLaunch = true
            appLaunch(if (Slave.hasPurchased(this)) MainActivity::class.java else TransLaunchFullAdsActivity::class.java)
            finish()
        }
    }

    private fun openBillingActivity() {
        val intent = Intent(this@SplashActivity, BillingListAdapterNew::class.java)
        intent.putExtra("FromSplash", true)
        billingActivityResultLauncher.launch(intent)
    }

    private fun openWithBillingCondition() {

        AHandler.getInstance().parseValueOfETC_3()

        if (Slave.hasPurchased(this)) {
            openDashboard()
        } else {

            try {
                if (AHandler.ShowBillingPage.isEmpty() || AHandler.SplashBillingpageCount == 0 || AHandler.ShowBillingPage == "0") {
                    openDashboard()
                } else {
                    if (mPreference?.isDoNotShow.equals("true") || DataHubConstant.APP_LAUNCH_COUNT > AHandler.SplashBillingpageCount) {
                        openDashboard()
                    } else {
                        openBillingActivity()
                    }
                }
            } catch (e: java.lang.Exception) {
                openDashboard()
            }
        }

    }

    private fun doImageFetchingWork() {
        if (checkStoragePermission(Utils.IMAGE)) {
            getDashboardViewmodel()?.getAllGalleryImages(this)
            getDashboardViewmodel()?.fetchImages(this)
        }

        if (checkStoragePermission(Utils.VIDEO)) {
            getDashboardViewmodel()?.fetchVideoList(this)
        }

        if (checkStoragePermission(Utils.AUDIO)) {
            getDashboardViewmodel()?.fetchAudioList(this)
        }
    }
}