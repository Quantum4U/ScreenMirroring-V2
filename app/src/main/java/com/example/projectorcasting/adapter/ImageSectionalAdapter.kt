package com.example.projectorcasting.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectorcasting.R
import com.example.projectorcasting.models.MediaData
import kotlin.reflect.KFunction1

class ImageSectionalAdapter(
    private val context: Context,
    private var mediaMap: LinkedHashMap<String, List<MediaData>>?,
    private val itemClick: KFunction1<MediaData, Unit>
) : SectionedRecyclerViewAdapter<RecyclerView.ViewHolder>() {

    private val sectionKeysList = mediaMap?.keys
    private val VIEW_TYPE_HEADER = -2

    fun refreshList(mediaList: LinkedHashMap<String, List<MediaData>>?) {
        mediaMap = mediaList?.let { LinkedHashMap(it) }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_media_header_list, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_item_layout, parent, false)
            ItemViewHolder(view)
        }
    }

    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val txtHeader: TextView? = itemView.findViewById(R.id.tv_date)
    }

    inner class ItemViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val card: RelativeLayout? = itemView.findViewById(R.id.cv_parent)
        val imgFile: ImageView? = itemView.findViewById(R.id.iv_image)

    }

    override fun getSectionCount(): Int {
        return sectionKeysList?.size ?: 0
    }

    override fun getItemCount(section: Int): Int {
        val key = sectionKeysList?.elementAt(section)
        val list = mediaMap?.get(key)

        return list?.size ?: 0
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?, section: Int) {
        val holderHeader = holder as ViewHolder
        val key = sectionKeysList?.elementAt(section)
        holderHeader.txtHeader?.text = key.toString()
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder?, section: Int, relativePosition: Int, absolutePosition: Int
    ) {
        val holderItem = holder as ItemViewHolder

        val key = sectionKeysList?.elementAt(section)
        val list = mediaMap?.get(key)
        val media = list?.get(relativePosition)


        holderItem.imgFile?.let { Glide.with(context).load(media?.path).into(it) }

        holderItem.card?.setOnClickListener {
            media?.let { it1 -> itemClick(it1) }
        }

    }
}