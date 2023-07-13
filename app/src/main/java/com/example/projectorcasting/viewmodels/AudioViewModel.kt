package com.example.projectorcasting.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.utils.PromptHelper
import javax.inject.Inject
import kotlin.reflect.KFunction2

class AudioViewModel @Inject constructor() : ViewModel() {

    fun showConnectionPrompt(
        context: Context?,
        actionPerform: KFunction2<Boolean, CastModel?, Unit>,
        isConnect: Boolean,
        castModel: CastModel?
    ) {
        PromptHelper.showConnectionPrompt(
            context,
            actionPerform,
            isConnect,
            castModel,
            ""
        )
    }


}