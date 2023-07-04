package com.example.projectorcasting.ui.fragments

import android.app.Activity
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.mediarouter.media.MediaRouter
import com.example.projectorcasting.ui.activities.BaseActivity
import com.example.projectorcasting.viewmodels.DashboardViewModel
import com.google.android.gms.cast.CastDevice
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import engine.app.adshandler.AHandler
import java.io.File

open class BaseFragment(profileFragment: Int) : Fragment(profileFragment) {

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
        AHandler.getInstance().showFullAds(activity, false)
    }

    fun showBottomBannerAds(view: LinearLayout?, activity: Activity?) {
        view?.addView(AHandler.getInstance().getBannerHeader(activity))
    }

    fun showNativeMedium(view: LinearLayout?, activity: Activity?) {
        view?.addView(AHandler.getInstance().getNativeMedium(activity))
    }

    fun getDashViewModel(): DashboardViewModel? {
        return (activity as BaseActivity?)?.getDashboardViewmodel()
    }

    //mirroring

    fun startMirroring(){
        (activity as BaseActivity).startMirroring()
    }

    //casting

    fun getMediaRouter(): MediaRouter? {
        return (activity as BaseActivity).getMediaRouter()
    }

    fun startCasting(routeInfo: MediaRouter.RouteInfo?,device: CastDevice?){
        (activity as BaseActivity).startCasting(routeInfo,device)
    }

//    fun startCasting(){
//        (activity as BaseActivity).startCasting()
//    }

    fun stopCasting(){
        (activity as BaseActivity).stopCasting()
    }

    fun stopServer(){
        (activity as BaseActivity).stopServer()
    }

    fun isCastingConnected(): Boolean? {
        return (activity as BaseActivity).isCastingConnected()
    }

    fun castingLiveData(): LiveData<Int> {
        return (activity as BaseActivity).data
    }

    fun getConnectedDeviceName(): String? {
        return (activity as BaseActivity).getConnectedDeviceName()
    }

    fun setConnectionInfo(isConnected: Boolean,device: CastDevice?){
        (activity as BaseActivity).setConnectionInfo(isConnected,device)
    }

    fun getConnectedCastDevice(): CastDevice? {
        return (activity as BaseActivity).getConnectedCastDevice()
    }

    fun getCastContext(): CastContext? {
        return (activity as BaseActivity).getCastContext()
    }

    fun getServerValue(): Boolean? {
        return (activity as BaseActivity).getServerValue()
    }

    fun setServerValue(value:Boolean){
        return (activity as BaseActivity).setServerValue(value)
    }
}