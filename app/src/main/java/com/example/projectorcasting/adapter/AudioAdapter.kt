package com.example.projectorcasting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectorcasting.models.MediaData
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import kotlin.reflect.KFunction1

class AudioAdapter(private val itemClick: KFunction1<MediaData, Unit>) :
    RecyclerView.Adapter<AudioAdapter.ViewHolder>() {

    private var audioList: ArrayList<MediaData>? = null

    fun refreshList(mediaList: List<MediaData>?) {
        audioList = mediaList?.let { ArrayList(it) }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AudioAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.audio_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioAdapter.ViewHolder, position: Int) {
        audioList?.get(position).let {  holder.updateData(it)}
    }

    override fun getItemCount(): Int {
        return audioList?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var card: CardView = itemView.findViewById(R.id.cv_parent) as CardView
        private var albumName: TextView = itemView.findViewById(R.id.tv_album_name) as TextView
        private var albumDuration: TextView = itemView.findViewById(R.id.tv_album_duration) as TextView
        private var albumDate: TextView = itemView.findViewById(R.id.tv_album_date) as TextView

        fun updateData(item: MediaData?) {

            albumName.text = item?.file?.name
            albumDate.text = item?.date
            albumDuration.text = " | "+item?.duration

            card.setOnClickListener {
                item?.let { it1 -> itemClick(it1) }
            }
        }
    }

    fun getAudioList(): ArrayList<MediaData>? {
        return audioList
    }
}