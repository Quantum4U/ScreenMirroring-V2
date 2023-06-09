package com.example.projectorcasting.casting.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.projectorcasting.R
import com.example.projectorcasting.casting.activities.ExpandedControlsActivity
import com.example.projectorcasting.casting.queue.QueueDataProvider
import com.example.projectorcasting.models.MediaData
import com.google.android.gms.cast.*
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.android.gms.common.images.WebImage
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONObject
import java.io.File
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.reflect.KFunction1

object Utils {

    private const val TAG: String = "Utils"
    const val PRELOAD_TIME_S: Int = 20

    const val IMAGE: Int = 1
    const val VIDEO: Int = 2
    const val AUDIO: Int = 3

    fun findIPAddress(context: Context): String? {
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        try {
            return if (wifiManager.connectionInfo != null) {
                val wifiInfo = wifiManager.connectionInfo
                InetAddress.getByAddress(
                    ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                        .putInt(wifiInfo.ipAddress)
                        .array()
                ).hostAddress
            } else
                null
        } catch (e: Exception) {
            Log.e(Utils::class.java.name, "Error finding IpAddress: ${e.message}", e)
        }
        return null
    }

    /**
     * This method will return a [MediaInfo] which contains information
     * about the media file and media subtitle which will be used for
     * playing in the cast device.
     */
    fun buildMediaInfo(mediaData: MediaData?, path: String, thumb: String, type: Int): MediaInfo? {

        /** Here we are setting the web server url for our
         *  media files.
         */
        val sampleVideoStream =
            "http://${CastHelper.deviceIpAddress}:9999/${path}"
        val sampleVideoSubtitle = ""
//            "http://${deviceIpAddress}:9999/${edt_subtitle.text}"

        Log.d("MainActivity", "buildMediaInfo A13 : >>$sampleVideoStream")

        val imageUrl1 =
            "http://${CastHelper.deviceIpAddress}:9999/${thumb}"
        val imageUrl2 =
            "http://${CastHelper.deviceIpAddress}:9999/${thumb}"


        /** (Optional) Setting a subtitle track, You can add more subtitle
         *  track by using this builder. */

        return when (type) {
            VIDEO -> mediaInfoForVideo(mediaData,sampleVideoStream, sampleVideoSubtitle, imageUrl1, imageUrl2)
            IMAGE -> mediaInfoForImage(mediaData,imageUrl1, imageUrl2)
            AUDIO -> mediaInfoForAudio(mediaData,sampleVideoStream, sampleVideoSubtitle,imageUrl1, imageUrl2)
            else -> mediaInfoForVideo(mediaData,sampleVideoStream, sampleVideoSubtitle, imageUrl1, imageUrl2)
        }

    }

    private fun mediaInfoForVideo(mediaData: MediaData?,
        sampleVideoStream: String,
        sampleVideoSubtitle: String, imageUrl1: String, imageUrl2: String
    ): MediaInfo {
        val movieMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE)
        setMediaData(mediaData,movieMetadata, imageUrl1, imageUrl2)

        //for subtitles
//        val mediaTrack = MediaTrack.Builder(1, MediaTrack.TYPE_TEXT)
//            .setName("English")
//            .setSubtype(MediaTrack.SUBTYPE_SUBTITLES)
//            .setContentId(sampleVideoSubtitle)
//            .setLanguage("en-US")
//            .build()

        return MediaInfo.Builder(sampleVideoStream)
            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
            .setContentType("videos/mp4")
            .setMetadata(movieMetadata)
            .setStreamDuration(42 * 1000) // 5:33 means 333 seconds
//            .setMediaTracks(listOf(mediaTrack)) // (Optional) Set list of subtitles.
            .build()
    }

    private fun mediaInfoForAudio(mediaData: MediaData?,
        sampleVideoStream: String,sampleVideoSubtitle: String,
        imageUrl1: String,
        imageUrl2: String
    ): MediaInfo {
        val movieMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK)
        setMediaData(mediaData,movieMetadata, imageUrl1, imageUrl2)

        Log.d("Utils", "setMediaData A13 :>> "+movieMetadata.getString(MediaMetadata.KEY_SUBTITLE))

        //for subtitles
//        val mediaTrack = MediaTrack.Builder(1, MediaTrack.TYPE_TEXT)
//            .setName("English")
//            .setSubtype(MediaTrack.SUBTYPE_SUBTITLES)
//            .setContentId(mediaData?.duration)
//            .setLanguage("en-US")
//            .build()

        return MediaInfo.Builder(sampleVideoStream)
            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
            .setContentType("audios/mp3")
            .setMetadata(movieMetadata)
            .setStreamDuration(42 * 1000) // 5:33 means 333 seconds
//            .setMediaTracks(listOf(mediaTrack)) // (Optional) Set list of subtitles.
            .build()
    }

    private fun mediaInfoForImage(mediaData: MediaData?,imageUrl1: String, imageUrl2: String): MediaInfo {
        val movieMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_PHOTO)
        setMediaData(mediaData,movieMetadata, imageUrl1, imageUrl2)

        return MediaInfo.Builder(imageUrl1)
            .setStreamType(MediaInfo.STREAM_TYPE_NONE)
            .setContentType("image/jpeg")
            .setMetadata(movieMetadata)
            .setStreamDuration(0) // 5:33 means 333 seconds
//                .setMediaTracks(listOf(mediaTrack)) // (Optional) Set list of subtitles.
            .build()

    }

    private fun setMediaData(mediaData: MediaData?,
        movieMetadata: MediaMetadata,
        imageUrl1: String,
        imageUrl2: String
    ) {
        movieMetadata.putString(MediaMetadata.KEY_TITLE, mediaData?.file?.name.toString()) // Set title for video
        Log.d("Utils", "setMediaData A13 :>> "+mediaData?.duration.toString())
        movieMetadata.putString(
            MediaMetadata.KEY_SUBTITLE,
            mediaData?.duration.toString()
        ) // Set sub-title for video

//        movieMetadata.putString(MediaMetadata.KEY_ALBUM_TITLE, "My Video")
//        movieMetadata.putString(MediaMetadata.KEY_ALBUM_ARTIST, testImageUrl1)

        movieMetadata.addImage(WebImage(Uri.parse(imageUrl1))) // Required first image (low-res)
        movieMetadata.addImage(WebImage(Uri.parse(imageUrl2))) // Required second image (high-res)
    }

    /**
     * Show a popup to select whether the selected item should play immediately, be added to the
     * end of queue or be added to the queue right after the current item.
     */
    fun showQueuePopup(context: Context?, mediaInfo: MediaInfo?, checkForQueue: KFunction1<Int, Unit>) {
        val castSession: CastSession? =
            context?.let { CastContext.getSharedInstance(it).sessionManager.currentCastSession }
        if (castSession == null || !castSession.isConnected) {
            Log.w(TAG, "showQueuePopup(): not connected to a cast device")
            return
        }
        val remoteMediaClient: RemoteMediaClient? = castSession.remoteMediaClient
        if (remoteMediaClient == null) {
            Log.w(TAG, "showQueuePopup(): null RemoteMediaClient")
            return
        }

        showQueuePrompt(context,mediaInfo,remoteMediaClient,checkForQueue)

//        val provider: QueueDataProvider? = QueueDataProvider.Companion.getInstance(context)
//
//
//        val popup = PopupMenu((context)!!, (view)!!)
//        popup.menuInflater.inflate(
//            if (provider!!.isQueueDetached || provider!!.count == 0) R.menu.detached_popup_add_to_queue else R.menu.popup_add_to_queue,
//            popup.menu
//        )
//        val clickListener: PopupMenu.OnMenuItemClickListener =
//            object : PopupMenu.OnMenuItemClickListener {
//                override fun onMenuItemClick(menuItem: MenuItem): Boolean {
//                    val queueItem: MediaQueueItem =
//                        MediaQueueItem.Builder((mediaInfo)!!).setAutoplay(
//                            true
//                        ).setPreloadTime(PRELOAD_TIME_S.toDouble()).build()
//                    val newItemArray: Array<MediaQueueItem> = arrayOf(queueItem)
//                    var toastMessage: String? = null
//                    if (provider?.count == 0) {
//                        remoteMediaClient.queueLoad(
//                            newItemArray, 0,
//                            MediaStatus.REPEAT_MODE_REPEAT_OFF, JSONObject()
//                        )
//                    } else {
//                        val currentId: Int = provider!!.currentItemId
//                        if (menuItem.itemId == R.id.action_play_now) {
//                            remoteMediaClient.queueInsertAndPlayItem(
//                                queueItem,
//                                currentId,
//                                JSONObject()
//                            )
//                        } else if (menuItem.itemId == R.id.action_play_next) {
//                            val currentPosition: Int = provider!!.getPositionByItemId(currentId)
//                            if (currentPosition == provider!!.count - 1) {
//                                //we are adding to the end of queue
//                                remoteMediaClient.queueAppendItem(queueItem, JSONObject())
//                            } else {
//                                val nextItem: MediaQueueItem? =
//                                    provider.getItem(currentPosition + 1)
//                                if (nextItem != null) {
//                                    val nextItemId: Int = nextItem.itemId
//                                    remoteMediaClient.queueInsertItems(
//                                        newItemArray,
//                                        nextItemId,
//                                        JSONObject()
//                                    )
//                                } else {
//                                    //remote queue is not ready with item; try again.
//                                    return false
//                                }
//                            }
//                            toastMessage = context.getString(
//                                R.string.queue_item_added_to_play_next
//                            )
//                        } else if (menuItem.itemId == R.id.action_add_to_queue) {
//                            remoteMediaClient.queueAppendItem(queueItem, JSONObject())
//                            toastMessage = context.getString(R.string.queue_item_added_to_queue)
//                        } else {
//                            return false
//                        }
//                    }
//                    if (menuItem.itemId == R.id.action_play_now) {
//                        val intent: Intent = Intent(context, ExpandedControlsActivity::class.java)
//                        context.startActivity(intent)
//                    }
//                    if (!TextUtils.isEmpty(toastMessage)) {
//                        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
//                    }
//                    return true
//                }
//            }
//        popup.setOnMenuItemClickListener(clickListener)
//        popup.show()
    }

    private fun showQueuePrompt(
        context: Context?,
        mediaInfo: MediaInfo?,
        remoteMediaClient: RemoteMediaClient?,
        checkForQueue: KFunction1<Int, Unit>
    ) {
        val sheetDialog = context?.let { BottomSheetDialog(it, R.style.BottomSheetDialog) }
        sheetDialog?.setContentView(R.layout.queue_prompt_layout)
        val playNow: TextView? = sheetDialog?.findViewById(R.id.tv_play_now)
        val playNext: TextView? = sheetDialog?.findViewById(R.id.tv_play_next)
        val addToQueue: TextView? = sheetDialog?.findViewById(R.id.tv_add_to_queue)
        val view: View? = sheetDialog?.findViewById(R.id.view)

        val cardView: LinearLayout? = sheetDialog?.findViewById(R.id.rl_root)
        cardView?.setBackgroundResource(R.drawable.sheet_dialog_bg)

        val provider: QueueDataProvider? = QueueDataProvider.Companion.getInstance(context)
        if (provider!!.isQueueDetached || provider.count == 0) {
            playNext?.visibility = View.GONE
            view?.visibility = View.GONE
        } else {
            playNext?.visibility = View.VISIBLE
            view?.visibility = View.VISIBLE
        }

        val queueItem: MediaQueueItem =
            MediaQueueItem.Builder((mediaInfo)!!).setAutoplay(
                true
            ).setPreloadTime(PRELOAD_TIME_S.toDouble()).build()
        val newItemArray: Array<MediaQueueItem> = arrayOf(queueItem)
        var toastMessage: String? = null

        val currentId: Int = provider.currentItemId

        playNow?.setOnClickListener {
            if (provider.count == 0) {
                remoteMediaClient?.queueLoad(
                    newItemArray, 0,
                    MediaStatus.REPEAT_MODE_REPEAT_OFF, JSONObject()
                )
            }else{
                remoteMediaClient?.queueInsertAndPlayItem(
                    queueItem,
                    currentId,
                    JSONObject())
            }

            val intent = Intent(context, ExpandedControlsActivity::class.java)
            context.startActivity(intent)
            sheetDialog.cancel()
        }

        playNext?.setOnClickListener {
            if (provider.count == 0) {
                remoteMediaClient?.queueLoad(
                    newItemArray, 0,
                    MediaStatus.REPEAT_MODE_REPEAT_OFF, JSONObject()
                )
            }else{
                val currentPosition: Int = provider.getPositionByItemId(currentId)
                if (currentPosition == provider.count - 1) {
                    //we are adding to the end of queue
                    remoteMediaClient?.queueAppendItem(queueItem, JSONObject())
                } else {
                    val nextItem: MediaQueueItem? =
                        provider.getItem(currentPosition + 1)
                    if (nextItem != null) {
                        val nextItemId: Int = nextItem.itemId
                        remoteMediaClient?.queueInsertItems(
                            newItemArray,
                            nextItemId,
                            JSONObject()
                        )
                    }
                }
                toastMessage = context.getString(
                    R.string.queue_item_added_to_play_next
                )
                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            }

            sheetDialog.cancel()
        }

        addToQueue?.setOnClickListener {
            if (provider.count == 0) {
                remoteMediaClient?.queueLoad(
                    newItemArray, 0,
                    MediaStatus.REPEAT_MODE_REPEAT_OFF, JSONObject()
                )
            }else{
                remoteMediaClient?.queueAppendItem(queueItem, JSONObject())
                toastMessage = context.getString(R.string.queue_item_added_to_queue)
                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            }

            checkForQueue(provider.count)

            sheetDialog.cancel()
        }

        sheetDialog?.show()

    }

}