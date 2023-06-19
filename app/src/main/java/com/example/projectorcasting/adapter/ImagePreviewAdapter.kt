package com.example.projectorcasting.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.example.projectorcasting.models.MediaData


class ImagePreviewAdapter(private val pathList: List<MediaData>?): PagerAdapter() {

    override fun getCount(): Int {
        return pathList?.size?:0
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as View
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val holder = ViewHolder()
        val ctx = container.context
        val itemView = LayoutInflater.from(ctx).inflate(R.layout.image_preview_item, container, false)

        holder.imageView = itemView?.findViewById(R.id.iv_image_preview) as ImageView
        holder.imageView.let { Glide.with(ctx).load(pathList?.get(position)?.path).into(it) }

        (container as ViewPager).addView(itemView)
        return itemView
    }

    internal class ViewHolder {
        lateinit var imageView: ImageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

    fun getImagePath(pos: Int):String?{
       return pathList?.get(pos)?.path?.split("0/")?.get(1)
    }

    fun getList(): List<MediaData>? {
        return pathList
    }

    fun getItem(position: Int): MediaData? {
        return pathList?.get(position)
    }

}