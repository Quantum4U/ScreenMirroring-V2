package com.example.projectorcasting.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.utils.PromptHelper
import kotlin.reflect.KFunction2

class DashboardViewModel : ViewModel() {

    fun showConnectionPrompt(
        context: Context?,
        actionPerform: KFunction2<Boolean, CastModel?, Unit>,
        isConnect: Boolean,
        castModel: CastModel?
    ) {
        PromptHelper.showConnectionPrompt(context, actionPerform, isConnect, castModel)
    }
}