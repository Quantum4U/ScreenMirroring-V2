package com.example.projectorcasting.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectorcasting.AnalyticsConstant
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.example.projectorcasting.adapter.QueueListAdapter
import com.example.projectorcasting.casting.activities.ExpandedControlsActivity
import com.example.projectorcasting.casting.listener.QueueItemTouchHelperCallback
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.casting.queue.QueueDataProvider
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.databinding.FragmentQueueBinding
import com.google.android.gms.cast.MediaQueueItem
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.CastState
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import engine.app.analytics.logGAEvents
import org.json.JSONObject

class QueueFragment : BaseFragment(R.layout.fragment_queue),QueueListAdapter.OnStartDragListener {

    private var binding: FragmentQueueBinding? = null

    private var mProvider: QueueDataProvider? = null
    private var mItemTouchHelper: ItemTouchHelper? = null
    private var mRemoteMediaClient: RemoteMediaClient? = null
    private val mRemoteMediaClientCallback: RemoteMediaClient.Callback =
        MyRemoteMediaClientCallback()
    private val mSessionManagerListener: SessionManagerListener<CastSession> =
        MySessionManagerListener()

    private inner class MySessionManagerListener : SessionManagerListener<CastSession> {
        override fun onSessionEnded(session: CastSession, error: Int) {
            Log.d("MySessionManagerener", "onSessionEnded A13 00: >.")
            if (mRemoteMediaClient != null) {
                mRemoteMediaClient!!.unregisterCallback(mRemoteMediaClientCallback)
            }
            mRemoteMediaClient = null
            binding?.tvEmpty?.visibility = View.VISIBLE
            binding?.rvQueue?.visibility = View.GONE
        }

        override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {
            mRemoteMediaClient = remoteMediaClient
            if (mRemoteMediaClient != null) {
                mRemoteMediaClient!!.registerCallback(mRemoteMediaClientCallback)
            }
        }

        override fun onSessionStarted(session: CastSession, sessionId: String) {
            mRemoteMediaClient = remoteMediaClient
            if (mRemoteMediaClient != null) {
                mRemoteMediaClient!!.registerCallback(mRemoteMediaClientCallback)
            }
        }

        override fun onSessionStarting(session: CastSession) {}
        override fun onSessionStartFailed(session: CastSession, error: Int) {}
        override fun onSessionEnding(session: CastSession) {}
        override fun onSessionResuming(session: CastSession, sessionId: String) {}
        override fun onSessionResumeFailed(session: CastSession, error: Int) {}
        override fun onSessionSuspended(session: CastSession, reason: Int) {
            if (mRemoteMediaClient != null) {
                mRemoteMediaClient!!.unregisterCallback(mRemoteMediaClientCallback)
            }
            mRemoteMediaClient = null
        }
    }

    private inner class MyRemoteMediaClientCallback : RemoteMediaClient.Callback() {
        override fun onStatusUpdated() {
            updateMediaQueue()
        }

        override fun onQueueStatusUpdated() {
            updateMediaQueue()
        }

        private fun updateMediaQueue() {
            val mediaStatus = mRemoteMediaClient?.mediaStatus
            val queueItems = mediaStatus?.queueItems
            if (queueItems == null || queueItems.isEmpty()) {
                binding?.tvEmpty?.visibility = View.VISIBLE
                binding?.rvQueue?.visibility = View.GONE
            } else {
                binding?.tvEmpty?.visibility = View.GONE
                binding?.rvQueue?.visibility = View.VISIBLE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentQueueBinding.bind(view)

        observeCastingLiveData()

        mProvider = QueueDataProvider.Companion.getInstance(context)
        Log.d("QueueFragment", "onViewCreated A13 :<<< "+mProvider?.mediaQueue)
        val adapter = mProvider?.mediaQueue?.let { QueueListAdapter(it, requireContext(), this) }
        binding?.rvQueue?.setHasFixedSize(true)
        binding?.rvQueue?.adapter = adapter
        binding?.rvQueue?.layoutManager = LinearLayoutManager(activity)

        val callback: ItemTouchHelper.Callback = QueueItemTouchHelperCallback(adapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper?.attachToRecyclerView(binding?.rvQueue)

        adapter?.setEventListener( object: QueueListAdapter.EventListener {
            override fun onItemViewClicked(view: View) {
                when (view.id) {
                    R.id.container -> {
                        onContainerClicked(view)
                    }
                    R.id.play_pause -> {
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
                    adapter?.notifyDataSetChanged()
                }
            })

        binding?.llConnect?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Queue_Cast_Connect)
            findNavController().navigate(R.id.nav_scan_device)
            showNavigationFullAds(activity)
        }

        binding?.llConnected?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Queue_Cast_DisConnect)
            getDashViewModel()?.showConnectionPrompt(
                context,
                ::actionPerform,
                false,
                null,
                ""
            )
        }

        binding?.ivBack?.setOnClickListener {
            exitPage()
        }

        registerCallback()

    }

    private fun observeCastingLiveData() {
        castingLiveData().observe(viewLifecycleOwner, Observer { state ->
            if (state == CastState.CONNECTED) {
                binding?.llConnected?.visibility = View.VISIBLE
                binding?.llConnect?.visibility = View.GONE
                binding?.tvConnected?.text = getString(R.string.connected, getConnectedDeviceName())
            } else if (state == CastState.NOT_CONNECTED) {
                binding?.llConnected?.visibility = View.GONE
                binding?.llConnect?.visibility = View.VISIBLE
            }

        })
    }

    private fun actionPerform(isConnect: Boolean, castModel: CastModel?) {
        if (isConnect)
            startCasting(castModel?.routeInfo, castModel?.castDevice)
        else
            stopCasting()
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

    override fun onDestroy() {
        if (mRemoteMediaClient != null) {
            mRemoteMediaClient!!.unregisterCallback(mRemoteMediaClientCallback)
        }

        getCastContext()?.sessionManager?.removeSessionManagerListener(mSessionManagerListener, CastSession::class.java)
        super.onDestroy()
    }

    private fun registerCallback(){
        getCastContext()?.sessionManager?.addSessionManagerListener(mSessionManagerListener, CastSession::class.java)

        if (mRemoteMediaClient == null) {
            mRemoteMediaClient = remoteMediaClient
        }
        if (mRemoteMediaClient != null) {
            mRemoteMediaClient!!.registerCallback(mRemoteMediaClientCallback)
            val mediaStatus = mRemoteMediaClient!!.mediaStatus
            val queueItems = mediaStatus?.queueItems
            if (queueItems != null && queueItems.isNotEmpty()) {
                binding?.tvEmpty?.visibility = View.GONE
                binding?.rvQueue?.visibility = View.VISIBLE
            }
        }
    }

    private fun exitPage() {
        findNavController().navigateUp()
    }
}