package com.example.projectorcasting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.example.projectorcasting.models.FolderModel
import kotlin.reflect.KFunction1

class FolderSelectionAdapter(
    private val folderList: List<FolderModel>,
    private val itemClick: KFunction1<FolderModel, Unit>
) :
    RecyclerView.Adapter<FolderSelectionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.folder_item_layout, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.updateData(folderList[position])
    }

    override fun getItemCount(): Int {
        return folderList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var card: RelativeLayout = itemView.findViewById(R.id.rl_parent) as RelativeLayout
        private var folderName: TextView = itemView.findViewById(R.id.tv_folder_name) as TextView

        fun updateData(item: FolderModel) {


            folderName.text = item.folderName

            card.setOnClickListener {
                itemClick(item)
            }
        }
    }
}
