package com.example.projectorcasting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.projectorcasting.R
import com.example.projectorcasting.casting.model.CastModel
import kotlin.reflect.KFunction2

class ScanDeviceAdapter(
    private val deviceList: List<CastModel>,
    private val itemClick: KFunction2<Boolean,CastModel, Unit>
) :
    RecyclerView.Adapter<ScanDeviceAdapter.ViewHolder>() {

    private var isCastEnabled: Boolean = false
    private var deviceId: String? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.device_item_layout, parent, false)
        return ViewHolder(view)
    }

    fun deviceConnected(isConnected: Boolean, id: String) {
        isCastEnabled = isConnected
        deviceId = id
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.updateData(deviceList[position])
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var card: RelativeLayout = itemView.findViewById(R.id.rl_parent) as RelativeLayout
        private var deviceName: TextView = itemView.findViewById(R.id.tv_device_name) as TextView
        private val activeDot: View = itemView.findViewById(R.id.v_active_dot) as View

        fun updateData(item: CastModel) {

            if (isCastEnabled && deviceId == item.castDevice?.deviceId) {
                activeDot.visibility = View.VISIBLE
            } else {
                activeDot.visibility = View.GONE
            }
            deviceName.text = item.castDevice?.modelName

            card.setOnClickListener {
                itemClick(!activeDot.isVisible,item)
            }
        }
    }
}
