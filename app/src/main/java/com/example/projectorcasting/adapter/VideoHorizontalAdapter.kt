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
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.utils.AppConstants
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import kotlin.reflect.KFunction1

class VideoHorizontalAdapter(
    private val mediaList: List<MediaData>,
    private val itemClick: KFunction1<MediaData, Unit>
) :
    RecyclerView.Adapter<VideoHorizontalAdapter.ViewHolder>() {

    private var ctx: Context? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        ctx = parent.context
        val view = LayoutInflater.from(ctx)
            .inflate(R.layout.video_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.updateData(mediaList[position])
    }

    override fun getItemCount(): Int {
        return if (mediaList.size < 4)
            mediaList.size
        else
            AppConstants.MAX_HORIZONTAL_ITEM
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var card: CardView = itemView.findViewById(R.id.cv_parent) as CardView
        private var icon: ImageView = itemView.findViewById(R.id.iv_album_icon) as ImageView
        private var albumName: TextView = itemView.findViewById(R.id.tv_album_name) as TextView
        private var albumDuration: TextView =
            itemView.findViewById(R.id.tv_album_duration) as TextView
        private var albumDate: TextView = itemView.findViewById(R.id.tv_album_date) as TextView

        fun updateData(item: MediaData) {

            if (item.bitmap != null) {
//                icon.setImageBitmap(item.bitmap)
                ctx?.let { Glide.with(it).load(item.bitmap).placeholder(R.drawable.ic_video_placeholder).into(icon) }
            } else {
                ctx?.let { Glide.with(it).load(item.file).placeholder(R.drawable.ic_video_placeholder).into(icon) }
            }

            albumName.text = item.file?.name
            albumDate.text = item.date
            albumDuration.text = item.duration

            card.setOnClickListener {
                itemClick(item)
            }
        }
    }
}
