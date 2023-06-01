package com.example.projectorcasting.casting.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.example.projectorcasting.R
import com.example.projectorcasting.casting.activities.ExpandedControlsActivity
import com.example.projectorcasting.casting.queue.QueueDataProvider
import com.google.android.gms.cast.*
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.android.gms.common.images.WebImage
import org.json.JSONObject
import java.io.File
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder

object Utils {

    private const val TAG: String = "Utils"
    const val PRELOAD_TIME_S: Int = 20

    const val IMAGE: Int = 1
    const val VIDEO: Int = 2
    const val AUDIO: Int = 1

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
    fun buildMediaInfo(path: String, thumb: String, type: Int): MediaInfo? {

        /** Here we are setting the web server url for our
         *  media files.
         */
        val sampleVideoStream =
            "http://${CastHelper.deviceIpAddress}:9999/${path}"
        val sampleVideoSubtitle = "this is demo video"
//            "http://${deviceIpAddress}:9999/${edt_subtitle.text}"

        Log.d("MainActivity", "buildMediaInfo A13 : >>$sampleVideoStream")

        val imageUrl1 =
            "http://${CastHelper.deviceIpAddress}:9999/${thumb}"
        val imageUrl2 =
            "http://${CastHelper.deviceIpAddress}:9999/${thumb}"


        /** (Optional) Setting a subtitle track, You can add more subtitle
         *  track by using this builder. */

        return when (type) {
            VIDEO -> mediaInfoForVideo(sampleVideoStream, sampleVideoSubtitle, imageUrl1, imageUrl2)
            IMAGE -> mediaInfoForImage(imageUrl1, imageUrl2)
            AUDIO -> mediaInfoForAudio(sampleVideoStream, imageUrl1, imageUrl2)
            else -> mediaInfoForVideo(sampleVideoStream, sampleVideoSubtitle, imageUrl1, imageUrl2)
        }

    }

    private fun mediaInfoForVideo(
        sampleVideoStream: String,
        sampleVideoSubtitle: String, imageUrl1: String, imageUrl2: String
    ): MediaInfo {
        val movieMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE)
        setMediaData(movieMetadata, imageUrl1, imageUrl2)

        val mediaTrack = MediaTrack.Builder(1, MediaTrack.TYPE_TEXT)
            .setName("English")
            .setSubtype(MediaTrack.SUBTYPE_SUBTITLES)
            .setContentId(sampleVideoSubtitle)
            .setLanguage("en-US")
            .build()

        return MediaInfo.Builder(sampleVideoStream)
            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
            .setContentType("videos/mp4")
            .setMetadata(movieMetadata)
            .setStreamDuration(42 * 1000) // 5:33 means 333 seconds
            .setMediaTracks(listOf(mediaTrack)) // (Optional) Set list of subtitles.
            .build()
    }

    private fun mediaInfoForAudio(
        sampleVideoStream: String,
        imageUrl1: String,
        imageUrl2: String
    ): MediaInfo {
        val movieMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK)
        setMediaData(movieMetadata, imageUrl1, imageUrl2)

        return MediaInfo.Builder(sampleVideoStream)
            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
            .setContentType("audios/mp3")
            .setMetadata(movieMetadata)
            .setStreamDuration(42 * 1000) // 5:33 means 333 seconds
//            .setMediaTracks(listOf(mediaTrack)) // (Optional) Set list of subtitles.
            .build()
    }

    private fun mediaInfoForImage(imageUrl1: String, imageUrl2: String): MediaInfo {
        val movieMetadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_PHOTO)
        setMediaData(movieMetadata, imageUrl1, imageUrl2)

        return MediaInfo.Builder(imageUrl1)
            .setStreamType(MediaInfo.STREAM_TYPE_NONE)
            .setContentType("image/jpeg")
            .setMetadata(movieMetadata)
            .setStreamDuration(0) // 5:33 means 333 seconds
//                .setMediaTracks(listOf(mediaTrack)) // (Optional) Set list of subtitles.
            .build()

    }

    private fun setMediaData(
        movieMetadata: MediaMetadata,
        imageUrl1: String,
        imageUrl2: String
    ) {
        movieMetadata.putString(MediaMetadata.KEY_TITLE, "Alcoholia") // Set title for video
        movieMetadata.putString(
            MediaMetadata.KEY_SUBTITLE,
            "Google developers"
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
    fun showQueuePopup(context: Context?, view: View?, mediaInfo: MediaInfo?) {
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
        val provider: QueueDataProvider? = QueueDataProvider.Companion.getInstance(context)
        val popup = PopupMenu((context)!!, (view)!!)
        popup.menuInflater.inflate(
            if (provider!!.isQueueDetached || provider!!.count == 0) R.menu.detached_popup_add_to_queue else R.menu.popup_add_to_queue,
            popup.menu
        )
        val clickListener: PopupMenu.OnMenuItemClickListener =
            object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                    val queueItem: MediaQueueItem =
                        MediaQueueItem.Builder((mediaInfo)!!).setAutoplay(
                            true
                        ).setPreloadTime(PRELOAD_TIME_S.toDouble()).build()
                    val newItemArray: Array<MediaQueueItem> = arrayOf(queueItem)
                    var toastMessage: String? = null
                    if (provider?.count == 0) {
                        remoteMediaClient.queueLoad(
                            newItemArray, 0,
                            MediaStatus.REPEAT_MODE_REPEAT_OFF, JSONObject()
                        )
                    } else {
                        val currentId: Int = provider!!.currentItemId
                        if (menuItem.itemId == R.id.action_play_now) {
                            remoteMediaClient.queueInsertAndPlayItem(
                                queueItem,
                                currentId,
                                JSONObject()
                            )
                        } else if (menuItem.itemId == R.id.action_play_next) {
                            val currentPosition: Int = provider!!.getPositionByItemId(currentId)
                            if (currentPosition == provider!!.count - 1) {
                                //we are adding to the end of queue
                                remoteMediaClient.queueAppendItem(queueItem, JSONObject())
                            } else {
                                val nextItem: MediaQueueItem? =
                                    provider.getItem(currentPosition + 1)
                                if (nextItem != null) {
                                    val nextItemId: Int = nextItem.itemId
                                    remoteMediaClient.queueInsertItems(
                                        newItemArray,
                                        nextItemId,
                                        JSONObject()
                                    )
                                } else {
                                    //remote queue is not ready with item; try again.
                                    return false
                                }
                            }
                            toastMessage = context.getString(
                                R.string.queue_item_added_to_play_next
                            )
                        } else if (menuItem.itemId == R.id.action_add_to_queue) {
                            remoteMediaClient.queueAppendItem(queueItem, JSONObject())
                            toastMessage = context.getString(R.string.queue_item_added_to_queue)
                        } else {
                            return false
                        }
                    }
                    if (menuItem.itemId == R.id.action_play_now) {
                        val intent: Intent = Intent(context, ExpandedControlsActivity::class.java)
                        context.startActivity(intent)
                    }
                    if (!TextUtils.isEmpty(toastMessage)) {
                        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                    }
                    return true
                }
            }
        popup.setOnMenuItemClickListener(clickListener)
        popup.show()
    }

    private val queryUri = MediaStore.Files.getContentUri("external")
    private const val imgselection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
    private val projection = arrayOf(
        MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.MIME_TYPE,
        MediaStore.Images.Media.BUCKET_ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    )

    fun getAllGalleryImages(context: Context): ArrayList<File>? {

        var list: ArrayList<File>? = arrayListOf()

        try {
            context.contentResolver.query(
                queryUri,
                projection,
                imgselection,
                null,
                MediaStore.Images.Media.DATE_TAKEN + " DESC"
            ).use { galCursor ->
                Log.d("Utils", "getAllGalleryImages A13 : >> 33")
                if (galCursor != null) {
                    Log.d("Utils", "getAllGalleryImages A13 : >> 44")
                    while (galCursor.moveToNext()) {
//                        val id = galCursor.getString(0)
//                        val name = galCursor.getString(1)
                        val path = galCursor.getString(2)
                        val file = File(path)
//                        val date = galCursor.getString(3)
//                        val galData = GalleryData(id, name, path, date)

                        list?.add(file)

                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()

        }

        Log.d("Utils", "getAllGalleryImages A13 : >> exception" + list?.size)

        return list
    }
}