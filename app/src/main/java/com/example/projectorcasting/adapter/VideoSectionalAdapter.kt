package com.example.projectorcasting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectorcasting.R
import com.example.projectorcasting.models.MediaData
import kotlin.reflect.KFunction1


class VideoSectionalAdapter(
    private val context: Context,
    private var mediaMap: HashMap<String, List<MediaData>>?,
    private val itemClick: KFunction1<MediaData, Unit>
) : SectionedRecyclerViewAdapter<RecyclerView.ViewHolder>() {

    private val sectionKeysList = mediaMap?.keys
    private val VIEW_TYPE_HEADER = -2

    fun refreshList(mediaList: HashMap<String, List<MediaData>>?) {
        mediaMap = mediaList?.let { HashMap(it) }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_media_header_list, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.media_item_vertical_layout, parent, false)
            ItemViewHolder(view)
        }
    }

    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val txtHeader: TextView? = itemView.findViewById(R.id.tv_date)
    }

    inner class ItemViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val card: CardView? = itemView.findViewById(R.id.cv_parent)
        val imgFile: ImageView? = itemView.findViewById(R.id.iv_album_icon)
        val albumName: TextView? = itemView.findViewById(R.id.tv_album_name)
        val albumDuration: TextView? = itemView.findViewById(R.id.tv_album_duration)
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


        if (media?.bitmap != null) {
            holderItem.imgFile?.setImageBitmap(media.bitmap)
        } else {
            holderItem.imgFile?.let {
                Glide.with(context).load(media?.file).into(it)
            }
        }

        holderItem.albumName?.text = media?.file?.name
        holderItem.albumDuration?.text = media?.duration

        holderItem.card?.setOnClickListener {
            media?.let { it1 -> itemClick(it1) }
        }

    }
}