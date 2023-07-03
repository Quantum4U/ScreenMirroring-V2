package io.github.dkbai.tinyhttpd.nanohttpd.core.util

object PathSingleton {

    private var imagePath: List<String>? = null
    private var videoPath: List<String>? = null
    private var audioPath: List<String>? = null

    fun setImagePath(list: List<String>?) {
        this.imagePath = list?.let { ArrayList(it) }
    }

    fun getImagePath(): List<String>? {
        return imagePath
    }

    fun setVideoPath(list: List<String>?) {
        this.videoPath = list?.let { ArrayList(it) }
    }

    fun getVideoPath(): List<String>? {
        return videoPath
    }

    fun setAudioPath(list: List<String>?) {
        this.audioPath = list?.let { ArrayList(it) }
    }

    fun getAudioPath(): List<String>? {
        return audioPath
    }
}