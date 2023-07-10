package com.example.projectorcasting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.models.SectionModel
import kotlin.reflect.KFunction1

class ImageSectionalAdapter(
    private val context: Context,
    private val itemClick: KFunction1<MediaData, Unit>,
    private val onLongClick: KFunction1<Boolean, Unit>,
    private val totalSelectedSize: KFunction1<Int, Unit>
) : SectionedRecyclerViewAdapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_HEADER = -2
    private var mediaMap: ArrayList<SectionModel>? = ArrayList()
    private var mLongClick = false
    private var selectedList: ArrayList<MediaData> = arrayListOf()
    private var selectedSectionList: ArrayList<SectionModel>? = arrayListOf()
    private var count = 0

    fun refreshList(mediaList: List<SectionModel>?) {
        mediaMap = mediaList?.let { ArrayList(it) }
        selectedSectionList = mediaList?.let { ArrayList(it) }
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
        val checkBoxLayout: RelativeLayout? = itemView.findViewById(R.id.rl_checkbox)
        val textSelection: TextView? = itemView.findViewById(R.id.tv_selection)
        val headerCb: CheckBox? = itemView.findViewById(R.id.header_checkbox)
    }

    inner class ItemViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val card: RelativeLayout? = itemView.findViewById(R.id.cv_parent)
        val imgFile: ImageView? = itemView.findViewById(R.id.iv_image)
        val checkBox: CheckBox? = itemView.findViewById(R.id.cb_checkbox)

    }

    override fun getSectionCount(): Int {
        return mediaMap?.size ?: 0
    }

    override fun getItemCount(section: Int): Int {
//        val key = sectionKeysList?.elementAt(section)
        val list = mediaMap?.get(section)?.sectionList

        return list?.size ?: 0
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?, section: Int) {
        val holderHeader = holder as ViewHolder
//        val key = sectionKeysList?.elementAt(section)
        val headerItem = mediaMap?.get(section)
        holderHeader.txtHeader?.text = headerItem?.date

        holderHeader.headerCb?.isChecked = headerItem?.isCheck == true
        if (headerItem?.isCheck == true)
            holderHeader.textSelection?.text = context.getString(R.string.unselect_all)
        else
            holderHeader.textSelection?.text = context.getString(R.string.select_all)

        holderHeader.checkBoxLayout?.setOnClickListener {
            headerItem?.isCheck = !headerItem?.isCheck!!
            holderHeader.headerCb?.isChecked = headerItem.isCheck!!

            selectAll(section, headerItem.sectionList, headerItem.isCheck)

            if (headerItem.isCheck == true)
                holderHeader.textSelection?.text = context.getString(R.string.unselect_all)
            else
                holderHeader.textSelection?.text = context.getString(R.string.select_all)
        }

        if (mLongClick) {
            holderHeader.checkBoxLayout?.visibility = View.VISIBLE
        } else {
            holderHeader.checkBoxLayout?.visibility = View.GONE
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder?, section: Int, relativePosition: Int, absolutePosition: Int
    ) {
        val holderItem = holder as ItemViewHolder

        val list = mediaMap?.get(section)?.sectionList
        val media = list?.get(relativePosition)

        holderItem.checkBox?.isChecked = media?.isCheck == true

        holderItem.imgFile?.let { Glide.with(context).load(media?.path).placeholder(R.drawable.ic_image_placeholder).into(it) }

        holderItem.card?.setOnLongClickListener {
            mLongClick = true
            media?.isCheck = !media?.isCheck!!
            holderItem.checkBox?.isChecked = media.isCheck!!
            removeAddItem(section, media, media.isCheck)
            notifyDataSetChanged()

            onLongClick(mLongClick)
            true
        }

        holderItem.card?.setOnClickListener {
            if (mLongClick) {
                media?.isCheck = !media?.isCheck!!
                holderItem.checkBox?.isChecked = media.isCheck!!

                removeAddItem(section, media, media.isCheck)
            } else {
                media?.let { it1 -> itemClick(it1) }
            }
        }

        holderItem.checkBox?.setOnClickListener {
            media?.isCheck = !media?.isCheck!!
            holderItem.checkBox.isChecked = media.isCheck!!

            removeAddItem(section, media, media.isCheck)
        }

        if (mLongClick) {
            holderItem.checkBox?.visibility = View.VISIBLE
        } else {
            holderItem.checkBox?.visibility = View.GONE
        }
    }

    fun getItemList(): ArrayList<SectionModel>? {
        return mediaMap
    }

    fun isLongClickEnable(): Boolean {
        return mLongClick
    }

    fun disableLongClick() {
        mLongClick = false
        unCheckAllCheckbox(false)
        notifyDataSetChanged()
    }

    private fun unCheckAllCheckbox(isCheck: Boolean) {
        val listSectionSize = mediaMap?.size

        selectedList.clear()
        for (i in 0 until listSectionSize!!) {
            val itemList = mediaMap?.get(i)?.sectionList
            mediaMap?.get(i)?.isCheck = isCheck
            selectedSectionList?.get(i)?.totalSelected = 0
            for (j in 0 until itemList?.size!!) {
                itemList[j].isCheck = isCheck
            }
        }
    }

    private fun selectAll(section: Int, itemList: List<MediaData>?, isCheck: Boolean?) {
        for (media in itemList!!) {
            media.isCheck = isCheck
            removeAddItem(section, media, isCheck)
        }
        notifyDataSetChanged()
    }

    private fun removeAddItem(section: Int, mediaData: MediaData, isCheck: Boolean?) {
        count = selectedSectionList?.get(section)?.totalSelected ?: 0

        if (isCheck == true) {
            if (!selectedList.contains(mediaData)) {
                count++
                selectedList.add(mediaData)
            }
        } else {
            if (selectedList.contains(mediaData)) {
                count--
                selectedList.remove(mediaData)
            }
        }

        selectedSectionList?.get(section)?.totalSelected = count

        val oldState = mediaMap?.get(section)?.isCheck
        mediaMap?.get(section)?.isCheck = count == mediaMap?.get(section)?.sectionList?.size
        val newState = mediaMap?.get(section)?.isCheck

        if (oldState != newState)
            notifyDataSetChanged()

        totalSelectedSize(selectedList.size)
    }

    fun getSelectedImageList(): ArrayList<MediaData> {
        return selectedList
    }
}