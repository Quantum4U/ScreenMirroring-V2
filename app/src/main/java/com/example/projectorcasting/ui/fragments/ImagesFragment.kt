package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectorcasting.AnalyticsConstant
import com.example.projectorcasting.adapter.FolderSelectionAdapter
import com.example.projectorcasting.adapter.ImageSectionalAdapter
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.casting.utils.CastHelper
import com.example.projectorcasting.casting.utils.Utils
import com.example.projectorcasting.models.FolderModel
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.models.SectionModel
import com.example.projectorcasting.utils.AppConstants
import com.example.projectorcasting.utils.MediaListSingleton
import com.example.projectorcasting.utils.PromptHelper
import com.example.projectorcasting.utils.SpacesItemDecoration
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.cast.framework.CastState
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.R
import com.quantum.projector.screenmirroring.cast.casting.phoneprojector.videoprojector.casttv.castforchromecast.screencast.casttotv.databinding.FragmentImagesBinding
import engine.app.adshandler.AHandler
import engine.app.analytics.logGAEvents
import engine.app.listener.OnRewardedEarnedItem
import engine.app.server.v2.Slave
import io.github.dkbai.tinyhttpd.nanohttpd.core.util.PathSingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ImagesFragment : BaseFragment(R.layout.fragment_images) {

    private var binding: FragmentImagesBinding? = null
    private var imageSectionalAdapter: ImageSectionalAdapter? = null
    private var folderDialog: BottomSheetDialog? = null
    private val DOCUMENT_BUFFER = 50
    private var imgFolder: FolderModel? = null
    private var isListReadyForPreview = false
    private var isItemClick = false
    private var isConnected = false
    private var isFromPreviewPage = false
    private var alteredList: List<SectionModel> = arrayListOf()
    private var isAscending = true
    private var folderName: String? = null
    private var isRewardedCompleted = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentImagesBinding.bind(view)
        observeImageList()
        observeFolderList()
        observeCastingLiveData()
        observeListForPreview()

        getConnectionStatus()

        doFetchingWork()

        binding?.llSorting?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Photos_Date_Sorting)
            sortMediaList()
        }

        binding?.llConnect?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Photos_Cast_Connect)
//            openDeviceListPage(false)
            PromptHelper.showCastingPrompt(context, ::castPromtAction, isConnected, null)
        }

        binding?.llConnected?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Photos_Cast_DisConnect)
            getDashViewModel()?.showConnectionPrompt(
                context,
                ::actionPerform,
                false,
                null,
                ""
            )
        }

        binding?.llFolder?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Photos_Folder_Sort_Button)
            showFolderSortingPrompt()
        }

        binding?.ivBack?.setOnClickListener {
            exitPage()
        }

        binding?.tvSlideshow?.setOnClickListener {
            logGAEvents(AnalyticsConstant.GA_Photos_Slideshow)
            MediaListSingleton.setSelectedImageList(imageSectionalAdapter?.getSelectedImageList())
            imageSectionalAdapter?.disableLongClick()
            if (isConnected) {
                openPreviewPage(true, 0, "")
            } else {
//                openDeviceListPage(true)
                PromptHelper.showCastingPrompt(context, ::castPromtAction, isConnected, null)
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitPage()
                }
            })

        checkResultToStartSlideshow()
        setBrowserValue()
    }

    private fun showImagesInHtml() {
        var pathList: java.util.ArrayList<String> = arrayListOf()
        val selectedList = MediaListSingleton.getSelectedImageList()
        Log.d("ImagesFragment", "showImagesInHtml A13 : >>" + selectedList?.size)
        CastHelper.startServer(context)
        if (selectedList != null) {
            for (data in selectedList) {
                val path = data.path?.split("0/")?.get(1)
                pathList.add(path.toString())
                CastHelper.showImagesInHtml(
                    context,
                    data,
                    path.toString(),
                    Utils.IMAGE
                )
            }
        }
        PathSingleton.setImagePath(pathList)
        PathSingleton.setVideoPath(null)
        PathSingleton.setAudioPath(null)
    }

    private fun openDeviceListPage(startSlideShow: Boolean) {
        Bundle().apply {
            putBoolean(AppConstants.FOR_START_SLIDESHOW, startSlideShow)
            findNavController().navigate(R.id.nav_scan_device, this)
        }
        showFullAds(activity)
    }

    private fun doFetchingWork() {
        if (MediaListSingleton.getGalleryImageList() == null || MediaListSingleton.getGalleryImageList()
                ?.isEmpty() == true
        ) {
            if (getDashViewModel()?.isLoading == true)
                showLoader()
            else
                fetchImages()
        } else {
            setAdapter()
        }
    }

    private fun fetchImages() {
        showLoader()
        getDashViewModel()?.getAllGalleryImages(context)
        getDashViewModel()?.fetchImages(context)
    }

    private fun observeListForPreview() {
        getDashViewModel()?.imageListForPreview?.observe(viewLifecycleOwner, Observer {
            isListReadyForPreview = true
            MediaListSingleton.setAllImageListForPreview(it)
            if (isItemClick) {
                itemClick(null)
            }
        })
    }

    private fun observeCastingLiveData() {
        castingLiveData().observe(viewLifecycleOwner, Observer { state ->
            if (state == CastState.CONNECTED) {
                isConnected = true
//                binding?.llConnected?.visibility = View.VISIBLE
//                binding?.llConnect?.visibility = View.GONE
//                binding?.tvConnected?.text = getString(R.string.connected, getConnectedDeviceName())
                binding?.ivCasting?.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_cast_enable,
                        null
                    )
                )
            } else if (state == CastState.NOT_CONNECTED) {
                isConnected = false
//                binding?.llConnected?.visibility = View.GONE
//                binding?.llConnect?.visibility = View.VISIBLE
                binding?.ivCasting?.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_cast_disable,
                        null
                    )
                )
            }

            setBrowserValue()

        })
    }

    private fun getConnectionStatus() {
        isConnected = isCastingConnected() == true
        if (isConnected)
            binding?.ivCasting?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_cast_enable,
                    null
                )
            )
        else
            binding?.ivCasting?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_cast_disable,
                    null
                )
            )
    }

    private fun setBrowserValue() {
        if (isServerRunning()) {
            binding?.ivBrowser?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_browser_enable,
                    null
                )
            )
        } else {
            binding?.ivBrowser?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_browser_disable,
                    null
                )
            )
        }
    }

    private fun observeImageList() {
        getDashViewModel()?.imagesList?.observe(viewLifecycleOwner, Observer { imageList ->
            Log.d("AppUtils>>", "getGalleryAllImages A14 : >> check time>> all images fragment")
            hideLoader()
            if (imageList != null && imageList.isNotEmpty()) {
                setAdapter()
            }
        })
    }

    private fun observeFolderList(){
        getDashViewModel()?.imagesFolderList?.observe(viewLifecycleOwner, Observer {
            if (it != null && it.isNotEmpty())
                binding?.llFolder?.visibility = View.VISIBLE
        })
    }

    private fun setAdapter() {
        imageSectionalAdapter = ImageSectionalAdapter(
            requireContext(),
            ::itemClick, ::onLongClick, ::totalSelectedSize
        )
        val layoutManager = GridLayoutManager(requireContext(), 3)
        imageSectionalAdapter?.setLayoutManager(layoutManager)
        imageSectionalAdapter?.shouldShowHeadersForEmptySections(true)
        binding?.rvImages?.hasFixedSize()
        binding?.rvImages?.layoutManager = layoutManager
        binding?.rvImages?.adapter = imageSectionalAdapter
        binding?.rvImages?.addItemDecoration(SpacesItemDecoration(1))

        if (!isFromPreviewPage) {
            alteredList =
                ArrayList(MediaListSingleton.getGalleryImageFolderList()?.get(0)?.sectionList)
        } else {
            sortingIconPlacement(!isAscending)
        }
        imageSectionalAdapter?.refreshList(alteredList)
        setListForPreview()
    }

    private fun setListForPreview() {
        isListReadyForPreview = false
        imageSectionalAdapter?.getItemList()?.let { getDashViewModel()?.setImageListForPreview(it) }
    }

    private fun sortMediaList() {
        sortingIconPlacement(isAscending)

        MediaListSingleton.setAllImageListForPreview(
            MediaListSingleton.getAllImageListForPreview()?.reversed()
        )
        val adapterList = imageSectionalAdapter?.getItemList()?.reversed()
        imageSectionalAdapter?.refreshList(adapterList)
        alteredList = ArrayList(imageSectionalAdapter?.getItemList()!!)
    }

    private fun sortingIconPlacement(isAscendingBoolean: Boolean) {
        if (isAscendingBoolean) {
            Log.d("ImagesFragment", "sortingIconPlacement A13 : >>00")
            isAscending = false
            binding?.ivSortingIcon?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_descending_icon,
                    null
                )
            )
            binding?.tvSortingText?.text = getString(R.string.descending)
        } else {
            isAscending = true
            binding?.ivSortingIcon?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_ascending_icon,
                    null
                )
            )
            binding?.tvSortingText?.text = getString(R.string.ascending)
            binding?.tvFolderName?.text = folderName?:getString(R.string.all_photos)
        }
    }

    private fun itemClick(mediaData: MediaData?) {
//        MediaListSingleton.setSelectedImageList(imageSectionalAdapter?.getSelectedImageList())
        if (isListReadyForPreview) {
            hideLoader()
            isItemClick = false
            openPreviewPage(
                false,
                MediaListSingleton.getAllImageListForPreview()?.indexOf(mediaData)!!,
                mediaData?.file?.name.toString()
            )
        } else {
            showLoader()
            isItemClick = true
        }
    }

    private fun openPreviewPage(startSlideshow: Boolean, pos: Int, name: String) {

//        val imageList = MediaListSingleton.getSelectedImageList()
//        var count =0
//        Log.d("ImagesFragment", "openPreviewPage A13 : >>"+imageList?.size)
//        CoroutineScope(Dispatchers.Main).launch {
//            for (mediaData in imageList!!) {
//                val castSession: CastSession? =
//                    context?.let { CastContext.getSharedInstance(it).sessionManager.currentCastSession }
//
//                val remoteMediaClient: RemoteMediaClient? = castSession?.remoteMediaClient
//
//                val path = mediaData.path?.split("0/")?.get(1)
//                Log.d("ImagesFragment", "openPreviewPage A13 : >> 11..>>"+path)
//                val queueItem: MediaQueueItem =
//                    MediaQueueItem.Builder(
//                        (Utils.buildMediaInfo(
//                            mediaData,
//                            path.toString(), path.toString(), VIDEO
//                        ))!!
//                    ).setAutoplay(
//                        true
//                    ).setPreloadTime(Utils.PRELOAD_TIME_S.toDouble()).build()
//                val newItemArray: Array<MediaQueueItem> = arrayOf(queueItem)
//
//                if (count == 0){
//                    count++
//                    remoteMediaClient?.queueLoad(
//                        newItemArray, 0,
//                        MediaStatus.REPEAT_MODE_REPEAT_OFF, JSONObject()
//                    )
//                }else {
//                    remoteMediaClient?.queueAppendItem(queueItem, JSONObject())
//                }
//                Log.d("ImagesFragment", "openPreviewPage A13 : >> 22..>>"+remoteMediaClient)
//            }
//
//
//            withContext(Dispatchers.Main) {
//                val action =
//                    ImagesFragmentDirections.actionImageToPreview(startSlideshow, pos)
//                findNavController().navigate(action)
//                showFullAds(activity)
//            }
//        }

        isFromPreviewPage = true
        val action =
            ImagesFragmentDirections.actionImageToPreview(startSlideshow, pos, isAscending)
        findNavController().navigate(action)
        showNavigationFullAds(activity)
    }

    private fun onLongClick(isLongClick: Boolean) {
        if (isLongClick) {
            binding?.tvSlideshow?.visibility = View.VISIBLE
            binding?.rlBottom?.visibility = View.GONE
        } else {
            binding?.tvSlideshow?.visibility = View.GONE
            binding?.rlBottom?.visibility = View.VISIBLE
        }
    }

    private fun totalSelectedSize(totalFiles: Int) {
        binding?.tvHeader?.text = getString(R.string.items_selected, totalFiles)
    }

    private fun actionPerform(isConnect: Boolean, castModel: CastModel?) {
        if (isConnect)
            startCasting(castModel?.routeInfo, castModel?.castDevice)
        else
            stopCasting()
    }

    private fun castPromtAction(isCastDeviceClick: Boolean, mediaData: MediaData?) {
        if (Slave.hasPurchased(context)) {
            openPageForConnection(isCastDeviceClick,mediaData)
        }else{
            PromptHelper.showInappBindPrompt(context,::premiumPromptAction,isCastDeviceClick,mediaData)
        }
    }

    private fun premiumPromptAction(isGoPremium: Boolean,isCastDeviceClick: Boolean, mediaData: MediaData?) {
        if (isGoPremium){
            AHandler.getInstance().showRemoveAdsPrompt(context)
        }else{
            AHandler.getInstance().showRewardedVideoOrFullAds(activity, true, object :
                OnRewardedEarnedItem {
                override fun onRewardedLoaded() {

                }

                override fun onRewardedFailed(msg: String?) {
                    openPageForConnection(isCastDeviceClick,mediaData)
                }

                override fun onUserEarnedReward(reward: RewardItem?) {
                    isRewardedCompleted = true
                }

                override fun onRewardAdsDismiss() {
                    if (isRewardedCompleted) {
                        isRewardedCompleted = false
                        openPageForConnection(isCastDeviceClick,mediaData)
                    }
                }

            })
        }
    }

    private fun openPageForConnection(isCastDeviceClick: Boolean, mediaData: MediaData?) {
        if (isCastDeviceClick) {
//            if (!isConnected)
//                findNavController().navigate(R.id.nav_scan_device)
//            else
//                stopCasting()
//            openDeviceListPage(true)
            findNavController().navigate(R.id.nav_scan_device)
            showFullAds(activity)
        } else {
            GlobalScope.launch(Dispatchers.Default) {
                showImagesInHtml()
            }
            openBrowserPage()
        }
    }


    fun showFolderSortingPrompt() {
        isAscending = true
        sortingIconPlacement(!isAscending)
        folderDialog = context?.let { BottomSheetDialog(it, R.style.BottomSheetDialog) }
        folderDialog?.setContentView(R.layout.folder_sorting_layout)
        val recyclerview: RecyclerView? = folderDialog?.findViewById(R.id.rv_folder)
        val adapter = MediaListSingleton.getGalleryImageFolderList()?.let {
            FolderSelectionAdapter(
                it,
                ::folderClick
            )
        }

        recyclerview?.layoutManager = LinearLayoutManager(context)
        recyclerview?.adapter = adapter

        val cardView: RelativeLayout? = folderDialog?.findViewById(R.id.rl_root)
        cardView?.setBackgroundResource(R.drawable.sheet_dialog_bg)

        folderDialog?.show()
    }

    private fun folderClick(folderModel: FolderModel) {
        imgFolder = folderModel
        folderDialog?.cancel()
        folderName = folderModel.folderName
        binding?.tvFolderName?.text = folderName
        val folderList = folderModel.sectionList
        imageSectionalAdapter?.refreshList(folderList as ArrayList<SectionModel>?)
        alteredList = ArrayList(folderList)

        setListForPreview()
    }

    private fun openBrowserPage() {
        findNavController().navigate(R.id.nav_browse_cast)
        showFullAds(activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun exitPage() {
        if (imageSectionalAdapter?.isLongClickEnable() == true) {
            imageSectionalAdapter?.disableLongClick()
            onLongClick(false)
            binding?.tvHeader?.text = getString(R.string.images)
        } else
            findNavController().navigateUp()
    }

    private fun checkResultToStartSlideshow() {
        setFragmentResultListener(AppConstants.START_SLIDESHOW_REQUEST_KEY) { requestKey: String, bundle: Bundle ->
            val result = bundle.getBoolean(AppConstants.START_SLIDESHOW)
            if (result)
                openPreviewPage(true, 0, "")
        }
    }
}