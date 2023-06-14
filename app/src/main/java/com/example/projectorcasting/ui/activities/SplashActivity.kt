package com.example.projectorcasting.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.projectorcasting.R
import com.example.projectorcasting.casting.utils.Utils

class SplashActivity:BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_splash_activity)

        doImageFetchingWork()

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            finish()
            startActivity(Intent(this,MainActivity::class.java))
        },5000)
    }

    private fun doImageFetchingWork() {
//        showLoader()
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