/*
 * Copyright 2022 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.projectorcasting.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.example.projectorcasting.R
import com.example.projectorcasting.casting.listener.QueueItemTouchHelperCallback
import com.example.projectorcasting.casting.queue.QueueDataProvider
import com.example.projectorcasting.casting.utils.CustomVolleyRequest
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.MediaQueueItem
import com.google.android.gms.cast.MediaStatus
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.media.MediaQueue
import com.google.android.gms.cast.framework.media.MediaQueueRecyclerViewAdapter
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * An adapter to show the list of queue items.
 */
class QueueListAdapter(
    mediaQueue: MediaQueue,
    context: Context,
    dragStartListener: OnStartDragListener
) : MediaQueueRecyclerViewAdapter<QueueListAdapter.QueueItemViewHolder>(mediaQueue),
    QueueItemTouchHelperCallback.ItemTouchHelperAdapter {
    private val mAppContext: Context
    private val mProvider: QueueDataProvider?
    private val mDragStartListener: OnStartDragListener
    private val mItemViewOnClickListener: View.OnClickListener
    private var mEventListener: EventListener? = null
    private var mImageLoader: ImageLoader? = null
    private val myMediaQueueCallback: ListAdapterMediaQueueCallback =
        ListAdapterMediaQueueCallback()

    init {
        mAppContext = context.applicationContext
        mProvider = QueueDataProvider.Companion.getInstance(context)
        mDragStartListener = dragStartListener
        mItemViewOnClickListener = View.OnClickListener { view ->
            if (view.getTag(R.string.queue_tag_item) != null) {
                val item = view.getTag(R.string.queue_tag_item) as MediaQueueItem
                Log.d(TAG, item.itemId.toString())
            }
            onItemViewClick(view)
        }
        getMediaQueue().registerCallback(myMediaQueueCallback)
        setHasStableIds(false)
    }

    private fun onItemViewClick(view: View) {
        mEventListener?.onItemViewClicked(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.queue_item_layout, parent, false)
        return QueueItemViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: QueueItemViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item = getItem(position)
        if (item == null) {
            holder.updateControlsStatus(QueueItemViewHolder.NONE)
        } else if (mProvider!!.isCurrentItem(item)) {
            holder.updateControlsStatus(QueueItemViewHolder.CURRENT)
            updatePlayPauseButtonImageResource(holder.mPlayPause)
        } else if (mProvider.isUpcomingItem(item)) {
            holder.updateControlsStatus(QueueItemViewHolder.UPCOMING)
        } else {
            holder.updateControlsStatus(QueueItemViewHolder.NONE)
        }
        holder.mContainer.setTag(R.string.queue_tag_item, item)
        holder.mPlayPause.setTag(R.string.queue_tag_item, item)
        holder.mPlayUpcoming.setTag(R.string.queue_tag_item, item)
        holder.mStopUpcoming.setTag(R.string.queue_tag_item, item)

        // Set listeners
        holder.mContainer.setOnClickListener(mItemViewOnClickListener)
        holder.mPlayPause.setOnClickListener(mItemViewOnClickListener)
        holder.mPlayUpcoming.setOnClickListener(mItemViewOnClickListener)
        holder.mStopUpcoming.setOnClickListener(mItemViewOnClickListener)
        val info = item?.media
        val mediaType = info?.metadata?.mediaType


        var imageUrl: String? = null
        if (info != null && info.metadata != null) {
            val metaData = info.metadata
            holder.mTitleView.text = metaData?.getString(MediaMetadata.KEY_TITLE)
            Log.d("QueueListAdapter", "onBindViewHolder A13 : >>"+metaData?.getString(MediaMetadata.KEY_SUBTITLE))
            holder.mDescriptionView.text = metaData?.getString(MediaMetadata.KEY_SUBTITLE)
            val images = metaData.images
            if (images != null && images.isNotEmpty()) {
                imageUrl = images[0].url.toString()
            }
        } else {
            holder.mTitleView.text = null
            holder.mDescriptionView.text = null
        }
        if (mediaType == MediaMetadata.MEDIA_TYPE_MOVIE) {


            holder.mProgressLoading.visibility = View.VISIBLE
            holder.mImageView.visibility = View.VISIBLE
            holder.audioIcon.visibility = View.GONE

            if (imageUrl != null) {
                mImageLoader = CustomVolleyRequest.Companion.getInstance(mAppContext)?.imageLoader
                val imageListener: ImageLoader.ImageListener = object : ImageLoader.ImageListener {
                    override fun onErrorResponse(error: VolleyError) {
                        Log.d(
                            "QueueListAdapter",
                            "onErrorResponse A13 : >>" + error + "//" + error.message
                        )
                        holder.mProgressLoading.visibility = View.GONE
                        holder.mImageView.setErrorImageResId(R.drawable.ic_queue_item_placeholder)
                    }

                    override fun onResponse(
                        response: ImageLoader.ImageContainer,
                        isImmediate: Boolean
                    ) {
                        if (response.bitmap != null) {
                            holder.mProgressLoading.visibility = View.GONE
                            holder.mImageView.setImageBitmap(response.bitmap)
                        }
                    }
                }
                holder.mImageView.setImageUrl(
                    mImageLoader!![imageUrl, imageListener].requestUrl,
                    mImageLoader
                )
            } else {
                holder.mProgressLoading.postDelayed({
                    holder.mProgressLoading.visibility = View.GONE
//                holder.mImageView.setDefaultImageResId(R.drawable.cast_album_art_placeholder)
//                holder.mImageView.setImageResource(R.drawable.cast_album_art_placeholder)
                }, 3000)
            }

        } else {
            holder.mProgressLoading.visibility = View.GONE
            holder.mImageView.visibility = View.GONE
            holder.audioIcon.visibility = View.VISIBLE
            holder.audioIcon.setImageDrawable(mAppContext.getDrawable(R.drawable.ic_audio_icon))
        }
        holder.mDragHandle.setOnTouchListener(OnTouchListener { view, event ->
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                mDragStartListener.onStartDrag(holder)
            } else if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_BUTTON_RELEASE) {
                Log.d("QueueListAdapter", "onBindViewHolder A13 : >>")
                view.clearFocus()
                view.clearAnimation()
                holder.setIsRecyclable(false)
                return@OnTouchListener true
            }
            false
        })
    }

    override fun onItemDismiss(position: Int) {
        mProvider!!.removeFromQueue(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition == toPosition) {
            return false
        }
        mProvider!!.moveItem(fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun dispose() {
        super.dispose()
        //unregister callback
        val queue = mediaQueue
        if (queue != null) {
            queue.unregisterCallback(myMediaQueueCallback)
        }
    }

    fun setEventListener(eventListener: EventListener?) {
        mEventListener = eventListener
    }

    private fun updatePlayPauseButtonImageResource(button: ImageButton) {
        val castSession = CastContext.getSharedInstance(mAppContext)
            .sessionManager.currentCastSession
        val remoteMediaClient = castSession?.remoteMediaClient
        if (remoteMediaClient == null) {
            button.visibility = View.GONE
            return
        }
        val status = remoteMediaClient.playerState
        when (status) {
            MediaStatus.PLAYER_STATE_PLAYING -> button.setImageResource(PAUSE_RESOURCE)
            MediaStatus.PLAYER_STATE_PAUSED -> button.setImageResource(PLAY_RESOURCE)
            else -> button.visibility = View.GONE
        }
    }

    /**
     * Handles ListAdapter notification upon MediaQueue data changes.
     */
    internal inner class ListAdapterMediaQueueCallback : MediaQueue.Callback() {
        override fun itemsInsertedInRange(start: Int, end: Int) {
            notifyItemRangeInserted(start, end)
        }

        override fun itemsReloaded() {
            notifyDataSetChanged()
        }

        override fun itemsRemovedAtIndexes(ints: IntArray) {
            for (i in ints) {
                notifyItemRemoved(i)
            }
        }

        override fun itemsReorderedAtIndexes(list: List<Int>, i: Int) {
            notifyDataSetChanged()
        }

        override fun itemsUpdatedAtIndexes(ints: IntArray) {
            for (i in ints) {
                notifyItemChanged(i)
            }
        }

        override fun mediaQueueChanged() {
            notifyDataSetChanged()
        }
    }

    class QueueItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ItemTouchHelperViewHolder {
        private val mContext: Context
        val mPlayPause: ImageButton
        private val mControls: View
        private val mUpcomingControls: View
        val mPlayUpcoming: ImageButton
        val mStopUpcoming: ImageButton
        var mImageView: NetworkImageView
        var audioIcon: ImageView
        var mContainer: ViewGroup
        var mDragHandle: ImageView
        var mTitleView: TextView
        var mDescriptionView: TextView
        var mProgressLoading: ProgressBar
        override fun onItemSelected() {
        }

        override fun onItemClear() {
//            itemView.setBackgroundColor(0)
        }

        @Retention(RetentionPolicy.SOURCE)
        @IntDef(CURRENT, UPCOMING, NONE)
        private annotation class ControlStatus

        init {
            mContext = itemView.context
            mContainer = itemView.findViewById<View>(R.id.container) as ViewGroup
            mDragHandle = itemView.findViewById<View>(R.id.drag_handle) as ImageView
            mTitleView = itemView.findViewById<View>(R.id.textView1) as TextView
            mDescriptionView = itemView.findViewById<View>(R.id.textView2) as TextView
            mImageView = itemView.findViewById<View>(R.id.imageView1) as NetworkImageView
            audioIcon = itemView.findViewById<View>(R.id.iv_audio_icon) as ImageView
            mPlayPause = itemView.findViewById<View>(R.id.play_pause) as ImageButton
            mControls = itemView.findViewById(R.id.controls)
            mUpcomingControls = itemView.findViewById(R.id.controls_upcoming)
            mPlayUpcoming = itemView.findViewById<View>(R.id.play_upcoming) as ImageButton
            mStopUpcoming = itemView.findViewById<View>(R.id.stop_upcoming) as ImageButton
            mProgressLoading = itemView.findViewById<View>(R.id.item_progress) as ProgressBar
        }

        fun updateControlsStatus(@ControlStatus status: Int) {
            var bgResId = R.drawable.bg_item_normal_state
//            mTitleView.setTextAppearance(mContext, R.style.Base_TextAppearance_AppCompat_Subhead)
//            mDescriptionView.setTextAppearance(
//                mContext,
//                R.style.Base_TextAppearance_AppCompat_Caption
//            )
            Log.d(TAG, "updateControlsStatus for status = $status")
            when (status) {
                CURRENT -> {
                    bgResId = R.drawable.bg_item_normal_state
                    mControls.visibility = View.VISIBLE
                    mPlayPause.visibility = View.VISIBLE
                    mUpcomingControls.visibility = View.GONE
//                    mDragHandle.setImageResource(DRAG_HANDLER_DARK_RESOURCE)
                }
                UPCOMING -> {
                    mControls.visibility = View.VISIBLE
                    mPlayPause.visibility = View.GONE
                    mUpcomingControls.visibility = View.VISIBLE
//                    mDragHandle.setImageResource(DRAG_HANDLER_LIGHT_RESOURCE)
                    //bg color for upcoming video
//                    bgResId = R.drawable.bg_item_upcoming_state
//                    mTitleView.setTextAppearance(
//                        mContext,
//                        R.style.TextAppearance_AppCompat_Small_Inverse
//                    )
//                    mTitleView.setTextAppearance(
//                        mTitleView.context,
//                        R.style.Base_TextAppearance_AppCompat_Subhead_Inverse
//                    )
//                    mDescriptionView.setTextAppearance(
//                        mContext,
//                        R.style.Base_TextAppearance_AppCompat_Caption
//                    )
                }
                else -> {
                    mControls.visibility = View.GONE
                    mPlayPause.visibility = View.GONE
                    mUpcomingControls.visibility = View.GONE
//                    mDragHandle.setImageResource(DRAG_HANDLER_DARK_RESOURCE)
                }
            }
            mContainer.setBackgroundResource(bgResId)
            super.itemView.requestLayout()
        }

        companion object {
            const val CURRENT = 0
            const val UPCOMING = 1
            const val NONE = 2
        }
    }

    /**
     * Interface for catching clicks on the ViewHolder items
     */
    interface EventListener {
        fun onItemViewClicked(view: View)
    }

    /**
     * Interface to notify an item ViewHolder of relevant callbacks from [ ].
     */
    interface ItemTouchHelperViewHolder {
        /**
         * Called when the [ItemTouchHelper] first registers an item as being moved or
         * swiped.
         * Implementations should update the item view to indicate it's active state.
         */
        fun onItemSelected()

        /**
         * Called when the [ItemTouchHelper] has completed the move or swipe, and the active
         * item state should be cleared.
         */
        fun onItemClear()
    }

    /**
     * Listener for manual initiation of a drag.
     */
    interface OnStartDragListener {
        /**
         * Called when a view is requesting a start of a drag.
         */
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder?)
    }

    companion object {
        private const val TAG = "QueueListAdapter"
        private const val PLAY_RESOURCE = R.drawable.ic_play_icon
        private const val PAUSE_RESOURCE = R.drawable.ic_pause
//        private const val DRAG_HANDLER_DARK_RESOURCE = R.drawable.ic_drag_updown_grey_24dp
//        private const val DRAG_HANDLER_LIGHT_RESOURCE = R.drawable.ic_drag_updown_white_24dp
    }
}