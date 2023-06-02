package com.example.projectorcasting.casting.model

import androidx.mediarouter.media.MediaRouter
import com.google.android.gms.cast.CastDevice

data class CastModel(val routeInfo: MediaRouter.RouteInfo? = null, val castDevice: CastDevice? = null)
