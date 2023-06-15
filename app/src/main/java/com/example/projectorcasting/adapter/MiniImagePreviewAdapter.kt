package com.example.projectorcasting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectorcasting.R
import com.example.projectorcasting.models.MediaData
import kotlin.reflect.KFunction2

class MiniImagePreviewAdapter(
    private val mediaList: List<MediaData>,
    private val itemClick: KFunction2<MediaData, Int, Unit>
) :
    RecyclerView.Adapter<MiniImagePreviewAdapter.ViewHolder>() {

    private var ctx: Context? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        ctx = parent.context
        val view = LayoutInflater.from(ctx)
            .inflate(R.layout.mini_preview_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.updateData(mediaList[position],position)
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var card: RelativeLayout = itemView.findViewById(R.id.rl_parent) as RelativeLayout
        private var icon: ImageView = itemView.findViewById(R.id.iv_preview) as ImageView

        fun updateData(item: MediaData, position: Int) {


            ctx?.let { Glide.with(it).load(item.path).into(icon) }

            card.setOnClickListener {
                itemClick(item,position)
            }
        }

        fun getViewHolderContainer(): View {
            return itemView
        }
    }
}
