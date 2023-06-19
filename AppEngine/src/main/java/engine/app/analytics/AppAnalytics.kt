package engine.app.analytics

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics

fun Fragment.logGAEvents(eventN: String?) {
    this.context?.logGAEvents(eventN)
}

fun Context.logGAEvents(eventN: String?) {
    logGAEvents(eventN, null, null)
}

fun Context.logGAEvents(eventN: String?, paramN: String?) {
    logGAEvents(eventN, paramN, null)
}

/**
 * Category - event Name
 * Action - Param Name
 * Value - Param Value
 */
fun Context.logGAEvents(eventN: String?, paramN: String?, paramV: String?) {

    var eventName: String = eventN ?: return
    eventName = "${EngineAnalyticsConstant.FireBasePrefix}_$eventName"
    val paramName: String = paramN ?: eventN
    val paramValue: String = paramV ?: paramName

    val bundle = Bundle().apply {
        //Key as Param Name and Value as Param value
        putString(paramName, paramValue)
    }

    FirebaseAnalytics.getInstance(this)
        //key As a Event Name
        .logEvent(eventName, bundle)
}