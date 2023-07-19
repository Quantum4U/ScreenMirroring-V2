package com.example.projectorcasting.casting.provider

import android.content.Context
import android.util.Log
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.example.projectorcasting.casting.activities.ExpandedControlsActivity
import com.example.projectorcasting.prefrences.AppPreference
import com.google.android.gms.cast.LaunchOptions
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider
import com.google.android.gms.cast.framework.media.CastMediaOptions
import com.google.android.gms.cast.framework.media.ImageHints
import com.google.android.gms.cast.framework.media.ImagePicker
import com.google.android.gms.cast.framework.media.NotificationOptions
import com.google.android.gms.common.images.WebImage

@Suppress("UNUSED")
class CastOptionsProvider : OptionsProvider {

    /** Sample uses default receiver application Id.
     *
     *  If you want to customize receiver device you need to register at Developer Console and
     *  replace the following [CastOptions.Builder.setReceiverApplicationId] with yours.
     *
     *  @see <a href="http://cast.google.com/publish">Cast Developers Console</a> to register for custom receiver Id.
     */

    private var appPreference:AppPreference?=null

    override fun getCastOptions(ctx: Context): CastOptions {
        /**
         * This will also show a notification in device.
         */
        appPreference = AppPreference(ctx)

        Log.d("CastOptionsProvider", "getCastOptions A13 : >>"+appPreference?.isImageCasting())

        val notificationOptions = NotificationOptions.Builder()
            .setTargetActivityClassName(ExpandedControlsActivity::class.java.name)
            .build()

        val mediaOptions = CastMediaOptions.Builder()
            .setImagePicker(ImagePickerImpl())
            .setNotificationOptions(notificationOptions)
            .setExpandedControllerActivityClassName(ExpandedControlsActivity::class.java.name)
            .build()

        val launchOptions = LaunchOptions.Builder()
            .setAndroidReceiverCompatible(true)
            .build()

        return CastOptions.Builder()
            .setLaunchOptions(launchOptions)
            .setReceiverApplicationId(ctx.getString(R.string.cast_app_id))
            .setCastMediaOptions(mediaOptions)
            .build()
    }

    override fun getAdditionalSessionProviders(p0: Context): MutableList<SessionProvider>? {
        return null
    }

    private class ImagePickerImpl : ImagePicker() {
        override fun onPickImage(mediaMetadata: MediaMetadata?, hints: ImageHints): WebImage? {
            val type = hints.type
            Log.d("ImagePickerImpl", "onPickImage A13 : 00>>"+mediaMetadata?.hasImages())
            if (!mediaMetadata!!.hasImages()) {
                return null
            }
            val images = mediaMetadata.images
            Log.d("ImagePickerImpl", "onPickImage A13 : 11>>"+images+"//"+images.size)
            return if (images.size == 1) {
                images[0]
            } else {
                Log.d("ImagePickerImpl", "onPickImage A13 : 22>>$type")
                if (type == IMAGE_TYPE_MEDIA_ROUTE_CONTROLLER_DIALOG_BACKGROUND) {
                    images[0]
                } else {
                    images[1]
                }
            }
        }
    }
}