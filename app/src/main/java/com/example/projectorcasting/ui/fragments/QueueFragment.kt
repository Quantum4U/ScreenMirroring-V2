package com.example.projectorcasting.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectorcasting.R
import com.example.projectorcasting.adapter.QueueListAdapter
import com.example.projectorcasting.casting.activities.ExpandedControlsActivity
import com.example.projectorcasting.casting.queue.QueueDataProvider
import com.example.projectorcasting.databinding.FragmentQueueBinding
import com.google.android.gms.cast.MediaQueueItem
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import org.json.JSONObject

class QueueFragment : BaseFragment(R.layout.fragment_queue),QueueListAdapter.OnStartDragListener {

    private var binding: FragmentQueueBinding? = null

    private var mProvider: QueueDataProvider? = null
    private var mItemTouchHelper: ItemTouchHelper? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentQueueBinding.bind(view)

        mProvider = QueueDataProvider.Companion.getInstance(context)
        val adapter = QueueListAdapter(mProvider!!.mediaQueue!! , requireContext(), this)
        binding?.rvQueue?.setHasFixedSize(true)
        binding?.rvQueue?.adapter = adapter
        binding?.rvQueue?.layoutManager = LinearLayoutManager(activity)

        adapter.setEventListener( object: QueueListAdapter.EventListener {
            override fun onItemViewClicked(view: View) {
                when (view.id) {
                    R.id.container -> {
//                        Log.d(
//                            TAG, ("onItemViewClicked() container "
//                                    + view.getTag(R.string.queue_tag_item))
//                        )
                        onContainerClicked(view)
                    }
                    R.id.play_pause -> {
//                        Log.d(
//                            TAG, ("onItemViewClicked() play-pause "
//                                    + view.getTag(R.string.queue_tag_item))
//                        )
                        onPlayPauseClicked(view)
                    }
                    R.id.play_upcoming -> mProvider!!.onUpcomingPlayClicked(
                        view,
                        view.getTag(R.string.queue_tag_item) as MediaQueueItem
                    )
                    R.id.stop_upcoming -> mProvider!!.onUpcomingStopClicked(
                        view,
                        view.getTag(R.string.queue_tag_item) as MediaQueueItem
                    )
                }
            }})

        mProvider?.setOnQueueDataChangedListener(
            object: QueueDataProvider.OnQueueDataChangedListener {
                override fun onQueueDataChanged() {
                    adapter.notifyDataSetChanged()
                }
            })

    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
        mItemTouchHelper?.startDrag((viewHolder)!!)
    }

    private fun onPlayPauseClicked(view: View) {
        val remoteMediaClient = remoteMediaClient
        remoteMediaClient?.togglePlayback()
    }

    private fun onContainerClicked(view: View) {
        val remoteMediaClient = remoteMediaClient ?: return
        val item = view.getTag(R.string.queue_tag_item) as MediaQueueItem
        val currentItemId = mProvider!!.currentItemId
        if (currentItemId == item.itemId) {
            // We selected the one that is currently playing so we take the user to the
            // full screen controller
            val castSession = CastContext.getSharedInstance(requireContext().applicationContext)
                .sessionManager
                .currentCastSession
            if (castSession != null) {
                val intent = Intent(activity, ExpandedControlsActivity::class.java)
                startActivity(intent)
            }
        } else {
            // a different item in the queue was selected so we jump there
            remoteMediaClient.queueJumpToItem(item.itemId,  JSONObject() )
        }
    }

    private val remoteMediaClient: RemoteMediaClient?
        private get() {
            val castSession = CastContext.getSharedInstance(requireContext())
                .sessionManager
                .currentCastSession
            return if (castSession != null && castSession.isConnected) {
                castSession.remoteMediaClient
            } else null
        }


}