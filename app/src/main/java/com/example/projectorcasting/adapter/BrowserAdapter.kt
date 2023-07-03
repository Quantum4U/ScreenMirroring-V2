package com.example.projectorcasting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectorcasting.models.BrowserModel
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R

class BrowserAdapter(private val list: List<BrowserModel>,):
    RecyclerView.Adapter<BrowserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.browser_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.updateData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var icon: ImageView = itemView.findViewById(R.id.icon) as ImageView
        private var textView: TextView = itemView.findViewById(R.id.textview) as TextView

        fun updateData(item: BrowserModel) {
            icon.setImageDrawable(item.drawable)
            textView.text = item.text
        }
    }
}
