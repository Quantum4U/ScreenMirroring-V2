package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.example.projectorcasting.R
import com.example.projectorcasting.adapter.ImagePreviewAdapter
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.casting.utils.CastHelper
import com.example.projectorcasting.casting.utils.Utils
import com.example.projectorcasting.databinding.FragmentImagePreviewBinding
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.utils.MediaListSingleton
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.CastState
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import java.util.*

class ImagePreviewFragment : BaseFragment(R.layout.fragment_image_preview) {

    private var binding: FragmentImagePreviewBinding? = null
    private var imagePreviewAdapter: ImagePreviewAdapter? = null
    private var remoteMediaClient: RemoteMediaClient? = null
    private var isCastConnected = false
    private val argument: ImagePreviewFragmentArgs by navArgs()
    private var itemList: ArrayList<MediaData> = arrayListOf()

    private var fromSlideshow = false
    private var filePosition = 0

    private var mHandler: Handler? = null
    private var timer: Timer? = null

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

        imagePreviewAdapter = ImagePreviewAdapter(itemList)

        binding?.vpImgPreview?.adapter = imagePreviewAdapter

        binding?.vpImgPreview?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                binding?.tvCount?.text = ""+(position+1)+"/"+itemList.size
                if (isCastConnected)
                    castImage(position)
            }

        })

        binding?.vpImgPreview?.currentItem = filePosition
//        castImage(1)
        startSlideShow()

        binding?.llConnect?.setOnClickListener {
            findNavController().navigate(R.id.nav_scan_device)
        }

        binding?.llConnected?.setOnClickListener {
            getDashViewModel()?.showConnectionPrompt(context, ::actionPerform, false, null)
        }

        binding?.ivBack?.setOnClickListener {
            exitPage()
        }

        binding?.tvSlideshow?.setOnClickListener {
            slideshowButtonClick()
        }

    }

    private fun slideshowButtonClick(){
        if (binding?.tvSlideshow?.text == getString(R.string.stop_slideshow)){
            binding?.tvSlideshow?.text = getString(R.string.start_slideshow)
            timer?.cancel()
            timer = null
        }else{
            binding?.tvSlideshow?.text = getString(R.string.stop_slideshow)
            startSlideShow()
        }
    }

    private fun startSlideShow() {
        if (!fromSlideshow) {
            binding?.llSlideshow?.visibility = View.GONE
            binding?.llMiniPreview?.visibility = View.VISIBLE
            return
        }else{
            binding?.llSlideshow?.visibility = View.VISIBLE
            binding?.llMiniPreview?.visibility = View.GONE
            binding?.tvCount?.text = ""+(filePosition+1)+"/"+itemList.size
        }

        mHandler = Handler(Looper.getMainLooper())

        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                mHandler?.post(runnable)
            }
        }, 2000, 3000)
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
        else
            stopCasting()
    }

    private val runnable = Runnable {
        filePosition = binding?.vpImgPreview?.currentItem ?: 0
        filePosition++
        if (filePosition < itemList.size)
            binding?.vpImgPreview?.setCurrentItem(filePosition, true)
        else {
            filePosition = 0
            binding?.vpImgPreview?.setCurrentItem(filePosition, true)
            binding?.tvSlideshow?.text = getString(R.string.start_slideshow)
            timer?.cancel()
            timer = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        mHandler?.removeCallbacks(runnable)
        mHandler = null
    }

    private fun exitPage() {
        findNavController().navigateUp()
    }
}