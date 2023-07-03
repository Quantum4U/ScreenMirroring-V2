package com.example.projectorcasting.utils

import android.content.Context
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.example.projectorcasting.adapter.FolderSelectionAdapter
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.models.MediaData
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

object PromptHelper {
    fun showConnectionPrompt(context: Context?, actionPerform: KFunction2<Boolean, CastModel?, Unit>, isConnect: Boolean, castModel: CastModel?) {
        val sheetDialog = context?.let { BottomSheetDialog(it, R.style.BottomSheetDialog) }
        sheetDialog?.setContentView(R.layout.connecton_prompt_layout)
        val title: TextView? = sheetDialog?.findViewById(R.id.tv_heading)
        val action: TextView? = sheetDialog?.findViewById(R.id.tv_action)
        val cancel: TextView? = sheetDialog?.findViewById(R.id.tv_cancel)

        val cardView: RelativeLayout? = sheetDialog?.findViewById(R.id.rl_root)
        cardView?.setBackgroundResource(R.drawable.sheet_dialog_bg)

        val name = castModel?.castDevice?.modelName

        if (isConnect){
            title?.text = context?.getString(R.string.connect_to,name)
            action?.text = context?.getString(R.string.connect)
            context?.resources?.getColor(R.color.text_green)?.let { action?.setTextColor(it) }
        }else{
            title?.text = context?.getString(R.string.disconnect_to,name)
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
}