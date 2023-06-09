package com.example.projectorcasting.ui.fragments

import android.app.Activity
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.projectorcasting.ui.activities.BaseActivity
import com.example.projectorcasting.viewmodels.DashboardViewModel
import java.io.File

open class BaseFragment(profileFragment: Int) : Fragment(profileFragment) {


    private var mTempFile:File? = null

    fun showLoader() {
        (activity as BaseActivity).showLoader()
    }

    fun hideLoader() {
        (activity as BaseActivity).hideLoader()
    }

    fun isLoaderShowing(): Boolean {
        return (activity as BaseActivity).isLoaderShowing()
    }

    fun showFullAds(activity: Activity?) {
//        AHandler.getInstance().showFullAds(activity, false)
    }

    fun showBottomBannerAds(view: LinearLayout?, activity: Activity?) {
//        view?.addView(AHandler.getInstance().getBannerHeader(activity))
    }

    fun getDashViewModel(): DashboardViewModel? {
        return (activity as BaseActivity?)?.getDashboardViewmodel()
    }

    fun startMirroring(){
        (activity as BaseActivity).startMirroring()
    }

}