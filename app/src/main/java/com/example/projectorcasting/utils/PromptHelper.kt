package com.example.projectorcasting.utils

import android.content.Context
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.projectorcasting.R
import com.example.projectorcasting.casting.model.CastModel
import com.google.android.material.bottomsheet.BottomSheetDialog
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
}