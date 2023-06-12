package com.example.projectorcasting.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.media.ThumbnailUtils
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import com.example.projectorcasting.R
import com.example.projectorcasting.models.FolderModel
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.models.SectionModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


object AppUtils {

    fun createTempImagePath(): File {
        return File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            ".CastingFolder/VideoThumb"
        )
    }

    fun createAudioThumbPath(context: Context?): File {
        return File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            ".CastingFolder/AudioThumb"
        )
//        return File(context?.filesDir, "AudioThumb")
    }

    fun openWifiPopUpInApp(activity: Activity) {
        try {
            val wifiManager =
                activity.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                wifiManager.isWifiEnabled = true
            } else {
                val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
                activity.startActivity(panelIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val queryUri = MediaStore.Files.getContentUri("external")

    private const val imgSelection =
        (MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
    private const val vidSelection =
        (MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
    private const val audioSelection =
        (MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO)

    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.MIME_TYPE,
        MediaStore.Images.Media.BUCKET_ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    )

    private val projectionVideo = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DATA,
        MediaStore.Video.Media.DATE_TAKEN,
        MediaStore.Video.Media.MIME_TYPE,
        MediaStore.Video.Media.BUCKET_ID,
        MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Video.VideoColumns.DURATION
    )

    private val projectionAudio = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DATA,
        MediaStore.Video.Media.DATE_TAKEN,
        MediaStore.Video.Media.MIME_TYPE,
        MediaStore.Video.Media.BUCKET_ID,
        MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Video.VideoColumns.DURATION
    )

    val folderProjection = arrayOf(
        MediaStore.Images.ImageColumns.BUCKET_ID,
        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
        MediaStore.Images.ImageColumns.DATA
    )

    val whereSelection = "${MediaStore.Images.Media.BUCKET_ID} IS NOT NULL"


    fun fetchImages(context: Context?): ArrayList<FolderModel>? {

        var listOfFolder: ArrayList<String>? = arrayListOf()
        var folderMap: ArrayList<FolderModel>? = ArrayList()
        var lastBucketId = ""
        val cursor = context?.contentResolver?.query(
            queryUri,
            projection,
            whereSelection,
            null,
            MediaStore.Images.Media.DATE_MODIFIED + " ASC",
            null
        )

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val bucketId = cursor.getString(
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                )
                var bucketName = cursor.getString(
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                )

                if (bucketName == null) {
                    bucketName = "root"
                }

                if (listOfFolder?.contains(bucketName) == false) {
                    listOfFolder.add(bucketName)
                    val sortedList = getAllGalleryImages(context, bucketId, bucketName)
                    folderMap?.add(
                        FolderModel(
                            bucketId, bucketName, sortedList
                        )
                    )

                }
            }
        }

        Log.d("AppUtils", "fetchImages A13 <<<<: "+MediaListSingleton.getGalleryImageList()?.size)

//        val allList = MediaListSingleton.getGalleryImageList()
//                Collections.sort(allList,
//            Comparator<MediaData> { o1, o2 ->
//                o2.file?.lastModified()?.compareTo(o1.file?.lastModified()!!)!!
//            })
//
//
//        //add all images list
//        folderMap?.add(0,FolderModel("all",context?.getString(R.string.all_photos),
//            allList?.let { getSortedMap(it) }))

        MediaListSingleton.setGalleryImageFolderList(folderMap)

        return folderMap
    }


    fun getAllGalleryImages(
        context: Context, bucketId: String, bucketName: String
    ): ArrayList<SectionModel>? {

        var list: ArrayList<MediaData>? = arrayListOf()

        val searchParams = MediaStore.Images.Media.BUCKET_ID + "=? "

        try {
            context.contentResolver.query(
                queryUri,
                null,
                searchParams,
                arrayOf(bucketId),
                MediaStore.Images.Media.DATE_MODIFIED + " DESC",null
            ).use { galCursor ->
                if (galCursor != null) {
                    while (galCursor.moveToNext()) {
                        val path = galCursor.getString(
                            galCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                        )
                        val file = File(path)
//                        val id = galCursor.getString(5)
//                        val folderName = galCursor.getString(6)

                        Log.d("Utils", "getAllGalleryImages A13 : >> 33" + bucketName)

                        list?.add(MediaData(file, null, null, null, bucketId, bucketName, path))

                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()

        }

//        MediaListSingleton.setGalleryImageSectionedList(list?.let { getSortedMap(it) })
        Log.d("Utils", "getAllGalleryImages A13 : >> exception" + list?.size)


        MediaListSingleton.setGalleryImageList(list)
        return list?.let { getSortedMap(it) }
    }

    fun getAllGalleryVideos(context: Context): ArrayList<MediaData>? {

        var list: ArrayList<MediaData>? = arrayListOf()
        var mapList: ArrayList<MediaData>? = arrayListOf()

        try {
            context.contentResolver.query(
                queryUri,
                projectionVideo,
                vidSelection,
                null,
                MediaStore.Images.Media.DATE_TAKEN + " ASC"
            ).use { galCursor ->
                Log.d("Utils", "getAllGalleryVideos A13 : >> 33")
                if (galCursor != null) {
                    Log.d("Utils", "getAllGalleryVideos A13 : >> 44")
                    while (galCursor.moveToNext()) {
//                        val id = galCursor.getString(0)
//                        val name = galCursor.getString(1)
                        val path = galCursor.getString(2)
                        val file = File(path)
//                        val date = galCursor.getString(3)
//                        val galData = GalleryData(id, name, path, date)

                        mapList?.add(
                            MediaData(
                                file,
                                convertDate(file.lastModified().toString()),
                                getMediaDuration(context, Uri.fromFile(file)),
                                getMediaBitmap(file)
                            )
                        )

                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()

        }

        Log.d("Utils", "getAllGalleryVideos A13 : >> exception" + list?.size)
        Log.d("Utils", "getAllGalleryVideos A13 : >> exception" + mapList?.size)

        Collections.sort(mapList, Comparator<MediaData> { o1, o2 ->
            o2.file?.lastModified()?.compareTo(o1.file?.lastModified()!!)!!
        })

        mapList?.let { list?.addAll(it) }

        for (i in 1..AppConstants.MAX_HORIZONTAL_ITEM) {
            mapList?.removeAt(0)
        }

        MediaListSingleton.setGalleryVideoSectionedList(mapList?.let { getSortedHashMap(it) })
        MediaListSingleton.setGalleryVideoList(list)

        return list
    }

    fun getAllGalleryAudios(context: Context): ArrayList<MediaData>? {

        var count = 1
        var list: ArrayList<MediaData>? = arrayListOf()
        var mapList: ArrayList<MediaData>? = arrayListOf()

        try {
            context.contentResolver.query(
                queryUri,
                projectionAudio,
                audioSelection,
                null,
                MediaStore.Images.Media.DATE_TAKEN + " ASC"
            ).use { galCursor ->
                if (galCursor != null) {
                    while (galCursor.moveToNext()) {
                        val path = galCursor.getString(2)
                        val file = File(path)

//                        if (count <= AppConstants.MAX_HORIZONTAL_ITEM) {
                        list?.add(
                            MediaData(
                                file,
                                convertDate(file.lastModified().toString()),
                                getMediaDuration(context, Uri.fromFile(file)),
                            )
                        )
//                        } else {
//                            mapList?.add(
//                                MediaData(
//                                    file,
//                                    convertDate(file.lastModified().toString()),
//                                    getMediaDuration(context, Uri.fromFile(file)),
//                                    getMediaBitmap(file)
//                                )
//                            )
//                        }
//                        count += 1
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()

        }

        Log.d("Utils", "getAllGalleryVideos A13 : >> exception" + list?.size)
        Log.d("Utils", "getAllGalleryVideos A13 : >> exception" + mapList?.size)

        Collections.sort(list, Comparator<MediaData> { o1, o2 ->
            o2.file?.lastModified()?.compareTo(o1.file?.lastModified()!!)!!
        })

        saveAudioThumb(
            context,
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_audio_placeholder)
        )
        MediaListSingleton.setGalleryAudioList(list)

        return list
    }

    private fun getMediaBitmap(file: File): Bitmap? {
        return ThumbnailUtils.createVideoThumbnail(file.path, MediaStore.Video.Thumbnails.MINI_KIND)
    }

    fun convertDate(dateInMilliseconds: String): String? {
        var s: String? = null
        s = if (isSameDay(System.currentTimeMillis().toString(), dateInMilliseconds)) "Today"
        else DateFormat.format("dd/MM/yyyy", dateInMilliseconds.toLong()).toString()
        if (s !== "Today") {
            val str = s.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var month: String? = null
            val st = str[1]
            if (st == "01") month = "Jan" else if (st == "02") month =
                "Feb" else if (st == "03") month = "March" else if (st == "04") month =
                "April" else if (st == "05") month = "May" else if (st == "06") month =
                "June" else if (st == "07") month = "July" else if (st == "08") month =
                "Aug" else if (st == "09") month = "Sept" else if (st == "10") month =
                "Oct" else if (st == "11") month = "Nov" else if (st == "12") month = "Dec"
            val day = str[0]
            val year = str[2]
            s = "$day $month $year"
        }
        println("CamScannerUtils .isSameDay testValues:hello22 $s")
        return s
    }

    private fun isSameDay(millis1: String, millis2: String): Boolean {
        val isSameDay: Boolean
        println("CamScannerUtils .isSameDay testValues:hello22 $millis2 $millis1")
        val date = Date(millis1.toLong())
        val date1 = Date(millis2.toLong())
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm")
        dateFormat.format(date)
        dateFormat.format(date1)
        println("CamScannerUtils .isSameDay testValues:hello1" + date1.toString() + " date: " + millis2.toLong())
        val cal1: Calendar = Calendar.getInstance()
        val cal2: Calendar = Calendar.getInstance()
        cal1.time = date
        cal2.time = date1
        isSameDay =
            cal1.get(Calendar.YEAR) === cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) === cal2.get(
                Calendar.DAY_OF_YEAR
            )
        return isSameDay
    }

    private fun getMediaDuration(context: Context, uri: Uri): String {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()

        var durInMillis = duration?.toLong()
        var hh = durInMillis?.let { TimeUnit.MILLISECONDS.toHours(it) }
        val hoursMillis = hh?.let { TimeUnit.HOURS.toMillis(it) }
        durInMillis = hoursMillis?.let { durInMillis?.minus(it) }
        var mm = durInMillis?.let { TimeUnit.MILLISECONDS.toMinutes(it) }
        val minutesMillis = mm?.let { TimeUnit.MINUTES.toMillis(it) }
        durInMillis = minutesMillis?.let { durInMillis?.minus(it) }
        var ss = durInMillis?.let { TimeUnit.MILLISECONDS.toSeconds(it) }


//        var standardValue = "1"
//
//        if(hh == 0L)
//            hh = standardValue.toLong()
//
//        if(mm == 0L)
//            mm = standardValue.toLong()
//
//        if(ss == 0L)
//            ss = standardValue.toLong()

        Log.d("AppUtils", "getMediaDuration A13 : >>" + hh + ":" + mm + ":" + ss)

        return "$hh:$mm:$ss"
    }

    private fun getSortedMap(mList: List<MediaData>): ArrayList<SectionModel> {

        val mMap: ArrayList<SectionModel> = ArrayList()
        var mDateHolder = System.currentTimeMillis().toString()
        var mHolderList: MutableList<MediaData> = ArrayList()

        var folderMap: HashMap<String, ArrayList<SectionModel>> = HashMap()
        val folderSectionedList: ArrayList<SectionModel> = ArrayList()
        var folderName = ""
        var mHolderFolderList: MutableList<MediaData> = ArrayList()

        for (i in mList.indices) {
            if (isSameDay(mDateHolder, mList[i].file?.lastModified().toString())) {
                mHolderList.add(mList[i])

            } else {

                if (mHolderList.isNotEmpty()) {
                    mMap.add(SectionModel(convertDate(mDateHolder), mHolderList))
                }
                mHolderList = ArrayList()
                mHolderList.add(mList[i])
                mDateHolder = mList[i].file?.lastModified().toString()

            }
            if (i == mList.size - 1) {
//                mMap[convertDate(mDateHolder).toString()] = mHolderList
                mMap.add(SectionModel(convertDate(mDateHolder), mHolderList))
            }

        }

        println("here is the final size holder" + " " + mHolderList.size)
        return mMap
    }

    private fun getSortedHashMap(mList: List<MediaData>): ArrayList<SectionModel> {


        val mMap: ArrayList<SectionModel> = ArrayList()
        var mDateHolder = System.currentTimeMillis().toString()
        var mHolderList: MutableList<MediaData> = ArrayList()
        for (i in mList.indices) {
            if (isSameDay(mDateHolder, mList[i].file?.lastModified().toString())) {
                mHolderList.add(mList[i])
            } else {
//                mMap[convertDate(mDateHolder).toString()] = mHolderList
                if (mHolderList.isNotEmpty()) mMap.add(
                    SectionModel(
                        convertDate(mDateHolder),
                        mHolderList
                    )
                )
                mHolderList = ArrayList()
                mHolderList.add(mList[i])
                mDateHolder = mList[i].file?.lastModified().toString()
            }
            if (i == mList.size - 1) {
//                mMap[convertDate(mDateHolder).toString()] = mHolderList
                mMap.add(SectionModel(convertDate(mDateHolder), mHolderList))
            }

            println("here is the final size owngallery" + " " + mMap)
        }

        println("here is the final size holder" + " " + mHolderList.size)
        return mMap
    }

    private fun getFilesWithFolderName(map: HashMap<String, List<MediaData>>) {
        val mMap: HashMap<String, HashMap<String, List<MediaData>>>? =
            HashMap<String, HashMap<String, List<MediaData>>>()

        for (e1 in map.entries) {
            var mkey = e1.key
            var mvalue = e1.value
            Log.d("AppUtils", "getFilesWithFolderName A13 : >>" + mkey)
            for (data in mvalue) {

            }
        }

    }

    fun saveTempThumb(bitmap: Bitmap?): File {
        val path = createTempImagePath()

        if (!path.exists()) {
            path.mkdirs()
        }

        val targetFile =
            File(path, System.currentTimeMillis().toString() + AppConstants.TEMP_IMAGE_NAME)
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(targetFile)
            val imageSaved = bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
            Objects.requireNonNull(out).close()
        } catch (e: IOException) {
            e.printStackTrace()
        }


        return targetFile
    }

    fun saveAudioThumb(context: Context?, bitmap: Bitmap?): File {
        val path = createAudioThumbPath(context)

        if (!path.exists()) {
            path.mkdirs()
        }

        val targetFile = File(path, AppConstants.AUDIO_THUMB)

        if (!targetFile.exists()) {
            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(targetFile)
                val imageSaved = bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
                Objects.requireNonNull(out).close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        scanMedia(context)
        return targetFile
    }

    fun deleteTempThumbFile(context: Context?): Boolean {
        var result = true
        val path = createTempImagePath()
        println("")
        if (path != null) {
            if (path.exists()) {
//                if (path.isDirectory) {
//                    for (child in path.listFiles()) {
//                        result = result and deleteTempThumbFile(context)
//                    }
//                    result = result and path.delete() // Delete empty directory.
//                } else if (path.isFile) {
//                    result = result and path.delete()
//                }
                result = path.delete()
                scanMedia(context)
                return result
            }
        } else {
            return false
        }
        return false
    }

    private fun scanMedia(context: Context?) {
        if (context != null) {
            MediaScannerConnection.scanFile(context,
                arrayOf(Environment.getExternalStorageDirectory().toString()),
                null,
                MediaScannerConnection.OnScanCompletedListener { path, uri -> })
        }
    }

}