package com.example.projectorcasting.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.models.SectionModel
import kotlin.reflect.KFunction1


class VideoSectionalAdapter(
    private val context: Context,
    private var mediaMap: ArrayList<SectionModel>?,
    private val itemClick: KFunction1<MediaData, Unit>
) : SectionedRecyclerViewAdapter<RecyclerView.ViewHolder>() {

    private var sectionKeysList = mediaMap?.size
    private val VIEW_TYPE_HEADER = -2

    fun refreshList(mediaList: ArrayList<SectionModel>?) {
        sectionKeysList = mediaList?.size
        mediaMap = mediaList?.let { ArrayList(it) }
        Log.d("VideoSectionalAdapter", "filtereList A13 : >> refresh"+mediaMap)
        notifyDataSetChanged()
    }

    fun filtereList(mediaList: ArrayList<SectionModel>?){
        mediaMap = mediaList?.let { ArrayList(it) }
        sectionKeysList = mediaMap?.size
        Log.d("VideoSectionalAdapter", "filtereList A13 : >>"+mediaMap)
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
        return sectionKeysList?: 0
    }

    override fun getItemCount(section: Int): Int {
//        val key = sectionKeysList?.elementAt(section)
        val list = mediaMap?.get(section)?.sectionList

        return list?.size ?: 0
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?, section: Int) {
        val holderHeader = holder as ViewHolder
//        val key = sectionKeysList?.elementAt(section)
        holderHeader.txtHeader?.text = mediaMap?.get(section)?.date
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder?, section: Int, relativePosition: Int, absolutePosition: Int
    ) {
        val holderItem = holder as ItemViewHolder

//        val key = sectionKeysList?.elementAt(section)
        val list = mediaMap?.get(section)?.sectionList
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

    fun getItemList(): ArrayList<SectionModel>? {
        return mediaMap
    }
}