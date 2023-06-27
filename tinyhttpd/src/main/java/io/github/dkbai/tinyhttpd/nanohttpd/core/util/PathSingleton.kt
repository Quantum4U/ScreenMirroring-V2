package io.github.dkbai.tinyhttpd.nanohttpd.core.util

object PathSingleton {

    private var imagePath: List<String>? = null

    fun setImagePath(list: List<String>?) {
        this.imagePath = list?.let { ArrayList(it) }
    }

    fun getImagePath(): List<String>? {
        return imagePath
    }
}