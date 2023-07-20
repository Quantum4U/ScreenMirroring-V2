package com.example.projectorcasting.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.models.MediaData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import engine.app.serviceprovider.Utils.isShowRewardedAds
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3

object PromptHelper {
    fun showConnectionPrompt(
        context: Context?,
        actionPerform: KFunction2<Boolean, CastModel?, Unit>,
        isConnect: Boolean,
        castModel: CastModel?,
        connectedName: String
    ) {
        val sheetDialog = context?.let { BottomSheetDialog(it, R.style.BottomSheetDialog) }
        sheetDialog?.setContentView(R.layout.connecton_prompt_layout)
        val title: TextView? = sheetDialog?.findViewById(R.id.tv_heading)
        val action: TextView? = sheetDialog?.findViewById(R.id.tv_action)
        val cancel: TextView? = sheetDialog?.findViewById(R.id.tv_cancel)

        val cardView: RelativeLayout? = sheetDialog?.findViewById(R.id.rl_root)
        cardView?.setBackgroundResource(R.drawable.sheet_dialog_bg)

        val name = castModel?.castDevice?.modelName

        if (isConnect){
            if (name != null)
                title?.text = context?.getString(R.string.connect_to,name)
            else
                title?.text = context?.getString(R.string.connect_to,connectedName)

            action?.text = context?.getString(R.string.connect)
            context?.resources?.getColor(R.color.text_green)?.let { action?.setTextColor(it) }
        }else{
            if (name != null)
                title?.text = context?.getString(R.string.connect_to,name)
            else
                title?.text = context?.getString(R.string.connect_to,connectedName)
            action?.text = context?.getString(R.string.disconnect)
            context?.resources?.getColor(R.color.text_red)?.let { action?.setTextColor(it) }
        }

        action?.setOnClickListener {
            actionPerform(isConnect,castModel)
            sheetDialog.cancel()
        }

        cancel?.setOnClickListener {
            sheetDialog.cancel()
        }
        sheetDialog?.show()
    }

    fun showCastingPrompt(context: Context?, actionPerform: KFunction2<Boolean, MediaData?, Unit>, isConnect: Boolean, mediaData: MediaData?) {
        val sheetDialog = context?.let { BottomSheetDialog(it, R.style.BottomSheetDialog) }
        sheetDialog?.setContentView(R.layout.casting_prompt_layout)
        val title: TextView? = sheetDialog?.findViewById(R.id.tv_heading)
        val cancel: TextView? = sheetDialog?.findViewById(R.id.tv_cancel)
        val castTv: LinearLayout? = sheetDialog?.findViewById(R.id.ll_cast_tv)
        val castOtherDevice: LinearLayout? = sheetDialog?.findViewById(R.id.ll_cast_other_device)

        val cardView: RelativeLayout? = sheetDialog?.findViewById(R.id.rl_root)
        cardView?.setBackgroundResource(R.drawable.sheet_dialog_bg)

//        if (!isConnect){
//            title?.text = context?.getString(R.string.connect_to,name)
//        }else{
//            title?.text = context?.getString(R.string.disconnect_to,name)
//        }

        castTv?.setOnClickListener {
            actionPerform(true,mediaData)
            sheetDialog.cancel()
        }

        castOtherDevice?.setOnClickListener {
            actionPerform(false,mediaData)
            sheetDialog.cancel()
        }

        cancel?.setOnClickListener {
            sheetDialog.cancel()
        }
        sheetDialog?.show()
    }

    fun showInappBindPrompt(context: Context?, actionPerform: KFunction3<Boolean, Boolean, MediaData?, Unit>,isCastDeviceClick: Boolean, mediaData: MediaData?) {
        val sheetDialog = context?.let { AlertDialog.Builder(it,R.style.CustomAlertDialog) }
        val view = LayoutInflater.from(context).inflate(R.layout.inapp_bind_prompt_layout,null)
        sheetDialog?.setView(view)
//        sheetDialog?.setContentView(R.layout.inapp_bind_prompt_layout)
        val iconTop: ImageView? = view?.findViewById(R.id.iv_prompt_icon)
        val lottie: LottieAnimationView? = view?.findViewById(R.id.lottie)
        val cross: ImageView? = view?.findViewById(R.id.iv_cross)
        val subHeading: TextView? = view?.findViewById(R.id.tv_sub_heading)
        val freeTrial: RelativeLayout? = view?.findViewById(R.id.rl_watch)
        val goPremium: TextView? = view?.findViewById(R.id.tv_premium)

        val cardView: RelativeLayout? = view?.findViewById(R.id.rl_root)

        val alert = sheetDialog?.create()

//        if (!isConnect){
//            title?.text = context?.getString(R.string.connect_to,name)
//        }else{
//            title?.text = context?.getString(R.string.disconnect_to,name)
//        }

        if (isShowRewardedAds(context as Activity?)){
            freeTrial?.visibility = View.VISIBLE
        }else{
            freeTrial?.visibility = View.GONE
        }

        if (isCastDeviceClick){
            iconTop?.visibility = View.VISIBLE
            lottie?.visibility = View.GONE
            subHeading?.text = context?.getString(R.string.pro_feature_sub_heading1)
        }else{
            iconTop?.visibility = View.GONE
            lottie?.visibility = View.VISIBLE
            subHeading?.text = context?.getString(R.string.pro_feature_sub_heading2)
        }

        goPremium?.setOnClickListener {
            actionPerform(true,isCastDeviceClick,mediaData)
            alert?.cancel()
        }

        freeTrial?.setOnClickListener {
            actionPerform(false,isCastDeviceClick,mediaData)
            alert?.cancel()
        }

        cross?.setOnClickListener {
            alert?.cancel()
        }

        alert?.show()
    }
}