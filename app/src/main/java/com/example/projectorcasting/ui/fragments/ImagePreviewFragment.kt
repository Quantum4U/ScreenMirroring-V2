package com.example.projectorcasting.ui.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.projectorcasting.AnalyticsConstant
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.example.projectorcasting.adapter.ImagePreviewAdapter
import com.example.projectorcasting.adapter.MiniImagePreviewAdapter
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.casting.utils.CastHelper
import com.example.projectorcasting.casting.utils.Utils
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.databinding.FragmentImagePreviewBinding
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.utils.AppConstants
import com.example.projectorcasting.utils.MediaListSingleton
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.CastState
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import engine.app.analytics.logGAEvents
import java.util.*


class ImagePreviewFragment : BaseFragment(R.layout.fragment_image_preview),
    ViewPager.OnPageChangeListener,
    RecyclerView.OnChildAttachStateChangeListener {

    private var binding: FragmentImagePreviewBinding? = null
    private var imagePreviewAdapter: ImagePreviewAdapter? = null
    private var recyclerAdapter: MiniImagePreviewAdapter? = null
    private var remoteMediaClient: RemoteMediaClient? = null
    private var isCastConnected = false
    private val argument: ImagePreviewFragmentArgs by navArgs()
    private var itemList: ArrayList<MediaData> = arrayListOf()

    private var fromSlideshow = false
    private var filePosition = 0

    private var mHandler: Handler? = null
    private var timer: Timer? = null

    private var currentItemPos = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentImagePreviewBinding.bind(view)

        observeCastingLiveData()

        fromSlideshow = argument.fromSlideshow
        if (!fromSlideshow)
            filePosition = argument.filePosition

        if (fromSlideshow)
            MediaListSingleton.getSelectedImageList()?.let { itemList.addAll(it) }
        else
            MediaListSingleton.getAllImageListForPreview()?.let { itemList.addAll(it) }

        initViewpager()
        initRecyclerview()
        startSlideShow()

        binding?.llConnect?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Photo_Preview_Cast_Connect)
            openDeviceListPage(true)
        }

        binding?.llConnected?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Photo_Preview_Cast_DisConnect)
            getDashViewModel()?.showConnectionPrompt(context, ::actionPerform, false, null)
        }

        binding?.ivBack?.setOnClickListener {
            exitPage()
        }

        binding?.tvSlideshow?.setOnClickListener {
            slideshowButtonClick()
        }

        checkResultToStartSlideshow()

    }

    private fun openDeviceListPage(startSlideShow: Boolean) {
        Bundle().apply {
            putBoolean(AppConstants.FOR_START_SLIDESHOW,startSlideShow)
            findNavController().navigate(R.id.nav_scan_device,this)
        }
    }

    private fun initViewpager() {
        imagePreviewAdapter = ImagePreviewAdapter(itemList)
        binding?.vpImgPreview?.adapter = imagePreviewAdapter
        binding?.vpImgPreview?.addOnPageChangeListener(this)
        binding?.vpImgPreview?.currentItem = filePosition
    }

    private fun initRecyclerview() {
        recyclerAdapter = MiniImagePreviewAdapter(itemList, ::recyclerItemClick)
        binding?.rvHorizontalPreview?.adapter = recyclerAdapter
        binding?.rvHorizontalPreview?.addOnChildAttachStateChangeListener(this)
        binding?.rvHorizontalPreview?.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun slideshowButtonClick() {
        if (binding?.tvSlideshow?.text == getString(R.string.stop_slideshow)) {
            logGAEvents(AnalyticsConstant.GA_Photo_Preview_Stop_Slideshow)
            binding?.tvSlideshow?.text = getString(R.string.start_slideshow)
            stopTimer()
        } else {
            logGAEvents(AnalyticsConstant.GA_Photo_Preview_Start_Slideshow)
            binding?.tvSlideshow?.text = getString(R.string.stop_slideshow)
            startSlideShow()
        }
    }

    private fun startSlideShow() {
        if (!fromSlideshow) {
            binding?.llSlideshow?.visibility = View.GONE
            binding?.llMiniPreview?.visibility = View.VISIBLE
            return
        } else {
            binding?.llSlideshow?.visibility = View.VISIBLE
            binding?.llMiniPreview?.visibility = View.GONE
            binding?.tvCount?.text = "" + (filePosition + 1) + "/" + itemList.size
        }

//        castImage(filePosition)
        mHandler = Handler(Looper.getMainLooper())

        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                mHandler?.post(runnable)
            }
        }, 5000, 5000)
    }

    private fun stopTimer(){
        timer?.cancel()
        timer = null
    }

    private fun establishSession() {
        val mCastSession: CastSession? =
            context?.let { CastContext.getSharedInstance(it).sessionManager.currentCastSession }
        if (mCastSession == null || !mCastSession.isConnected) {
            Log.w("Utils.TAG", "showQueuePopup(): not connected to a cast device")
            return
        }
        remoteMediaClient = mCastSession.remoteMediaClient
        if (remoteMediaClient == null) {
            Log.w("Utils.TAG", "showQueuePopup(): null RemoteMediaClient")
            return
        }

        remoteMediaClient?.registerCallback(object : RemoteMediaClient.Callback() {
            override fun onStatusUpdated() {
                remoteMediaClient?.unregisterCallback(this)
            }
        })

    }

    private fun castImage(position: Int) {
        val mediaItem = imagePreviewAdapter?.getItem(position)
        val path = mediaItem?.path?.split("0/")?.get(1)
        CastHelper.castPhotos(remoteMediaClient, mediaItem, path.toString(), Utils.IMAGE)
    }

    private fun observeCastingLiveData() {
        castingLiveData().observe(viewLifecycleOwner, Observer { state ->
            if (state == CastState.CONNECTED) {
                isCastConnected = true
                binding?.llConnected?.visibility = View.VISIBLE
                binding?.llConnect?.visibility = View.GONE
                binding?.tvConnected?.text = getString(R.string.connected, getConnectedDeviceName())

                CastHelper.startServer(context)
                establishSession()
                castImage(filePosition)
            } else if (state == CastState.NOT_CONNECTED) {
                isCastConnected = false
                binding?.llConnected?.visibility = View.GONE
                binding?.llConnect?.visibility = View.VISIBLE
            }
        })
    }

    private fun actionPerform(isConnect: Boolean, castModel: CastModel?) {
        if (isConnect)
            startCasting(castModel?.routeInfo, castModel?.castDevice)
        else {
            fromSlideshow = false
            stopCasting()
            filePosition = binding?.vpImgPreview?.currentItem ?: 0
            startSlideShow()
            stopTimer()
        }
    }

    private val runnable = Runnable {
        filePosition = binding?.vpImgPreview?.currentItem ?: 0
        filePosition++
        if (filePosition < itemList.size)
            scrollViewpager(filePosition)
        else {
            filePosition = 0
            scrollViewpager(filePosition)
            binding?.tvSlideshow?.text = getString(R.string.start_slideshow)
            stopTimer()
        }
    }

    private fun scrollViewpager(pos: Int) {
        binding?.vpImgPreview?.setCurrentItem(pos, true)
    }

    private fun recyclerItemClick(mediaData: MediaData, position: Int) {
        binding?.vpImgPreview?.currentItem = position
    }

    override fun onDestroyView() {
        if(isCastConnected)
            stopCasting()
        super.onDestroyView()
        binding = null
        mHandler?.removeCallbacks(runnable)
        mHandler = null
    }

    private fun exitPage() {
        findNavController().navigateUp()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//        TODO("Not yet implemented")
    }

    override fun onPageSelected(position: Int) {
        if (fromSlideshow) {
            binding?.tvCount?.text = "" + (position + 1) + "/" + itemList.size
            if (isCastConnected)
                castImage(position)
        } else {
            val previousItemPos: Int = currentItemPos
            currentItemPos = position
            scroll(position, true)
            changeItem(position, previousItemPos)
        }

    }

    override fun onPageScrollStateChanged(state: Int) {
//        TODO("Not yet implemented")
    }

    override fun onChildViewAttachedToWindow(view: View) {
        val childPosition: Int = binding?.rvHorizontalPreview?.getChildAdapterPosition(view) ?: 0
        if (childPosition == currentItemPos) {
            highlightItem(view)
        }
    }

    override fun onChildViewDetachedFromWindow(view: View) {
        unHighlightItem(view)
    }

    private fun scroll(position: Int, isPortrait: Boolean) {
        val view: View? =
            binding?.rvHorizontalPreview?.getChildAt(0) //Only used to get the width of a view. They are all the same so this is safe
        if (view != null) {
            val width = if (isPortrait) view.width else view.height
            val pos: Int? =
                if (isPortrait) binding?.rvHorizontalPreview?.computeHorizontalScrollOffset() else binding?.rvHorizontalPreview?.computeVerticalScrollOffset()
            val targetPos = (position * width).toFloat()
            val delta = (pos?.minus(targetPos))?.times(-1)
            val x = if (isPortrait) delta?.toInt() else 0
            val y = if (isPortrait) 0 else delta?.toInt()
            x?.let { xAxis ->
                y?.let { yAxis ->
                    binding?.rvHorizontalPreview?.smoothScrollBy(
                        xAxis,
                        yAxis
                    )
                }
            }
        }
    }

    private fun changeItem(newItem: Int, oldItem: Int) {
        val holder = binding?.rvHorizontalPreview?.findViewHolderForAdapterPosition(newItem)
        if (holder is MiniImagePreviewAdapter.ViewHolder) {
            highlightItem(holder.getViewHolderContainer())
        }
        val oldHolder= binding?.rvHorizontalPreview?.findViewHolderForAdapterPosition(oldItem)
        if (oldHolder is MiniImagePreviewAdapter.ViewHolder) {
            unHighlightItem(oldHolder.getViewHolderContainer())
        }
    }

    private fun highlightItem(view: View) {
        view.background = ResourcesCompat.getDrawable(resources,R.drawable.selected_image_bg,null)
    }

    private fun unHighlightItem(view: View) {
        view.background = null
    }

    private fun checkResultToStartSlideshow() {
        setFragmentResultListener(AppConstants.START_SLIDESHOW_REQUEST_KEY) { requestKey: String, bundle: Bundle ->
            val result = bundle.getBoolean(AppConstants.START_SLIDESHOW)
            if (result){
                fromSlideshow = result
                filePosition = binding?.vpImgPreview?.currentItem ?: 0
                startSlideShow()
            }
        }
    }
}