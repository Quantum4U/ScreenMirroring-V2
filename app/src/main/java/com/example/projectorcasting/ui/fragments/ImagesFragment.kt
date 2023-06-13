package com.example.projectorcasting.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectorcasting.R
import com.example.projectorcasting.adapter.FolderSelectionAdapter
import com.example.projectorcasting.adapter.ImageSectionalAdapter
import com.example.projectorcasting.casting.model.CastModel
import com.example.projectorcasting.databinding.FragmentImagesBinding
import com.example.projectorcasting.models.FolderModel
import com.example.projectorcasting.models.MediaData
import com.example.projectorcasting.models.SectionModel
import com.example.projectorcasting.utils.AppUtils
import com.example.projectorcasting.utils.MediaListSingleton
import com.example.projectorcasting.utils.SpacesItemDecoration
import com.google.android.gms.cast.framework.CastState
import com.google.android.material.bottomsheet.BottomSheetDialog


class ImagesFragment : BaseFragment(R.layout.fragment_images) {

    private var binding: FragmentImagesBinding? = null
    private var imageSectionalAdapter: ImageSectionalAdapter? = null
    private var folderDialog: BottomSheetDialog? = null
    private val DOCUMENT_BUFFER = 50
    private var imgFolder: FolderModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentImagesBinding.bind(view)
        observeImageList()
        observeCastingLiveData()

        doFetchingWork()

        binding?.llSorting?.setOnClickListener {
            sortMediaList()
        }

        binding?.llConnect?.setOnClickListener {
            findNavController().navigate(R.id.nav_scan_device)
        }

        binding?.llConnected?.setOnClickListener {
            getDashViewModel()?.showConnectionPrompt(context, ::actionPerform, false, null)
        }

        binding?.llFolder?.setOnClickListener {
            showFolderSortingPrompt()
        }

        binding?.ivBack?.setOnClickListener {
            exitPage()
        }
    }

    private fun doFetchingWork() {
        if (MediaListSingleton.getGalleryImageFolderList() == null || MediaListSingleton.getGalleryImageFolderList()
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

    private fun observeImageList() {
        getDashViewModel()?.imagesFolderList?.observe(viewLifecycleOwner, Observer { imageList ->
            hideLoader()
            if (imageList != null && imageList.isNotEmpty()) {
                setAdapter()
            }
        })
    }

    private fun setAdapter() {
        imageSectionalAdapter = ImageSectionalAdapter(
            requireContext(),
            ::itemClick
        )
        val layoutManager = GridLayoutManager(requireContext(), 3)
        imageSectionalAdapter?.setLayoutManager(layoutManager)
        imageSectionalAdapter?.shouldShowHeadersForEmptySections(true)
        binding?.rvImages?.hasFixedSize()
        binding?.rvImages?.layoutManager = layoutManager
        binding?.rvImages?.adapter = imageSectionalAdapter
        binding?.rvImages?.addItemDecoration(SpacesItemDecoration(1))

        imageSectionalAdapter?.refreshList(
            MediaListSingleton.getGalleryImageFolderList()?.get(0)?.sectionList
        )
    }

    private fun sortMediaList() {
        if (binding?.tvSortingText?.text?.equals(getString(R.string.ascending)) == true) {
            binding?.ivSortingIcon?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_descending_icon,
                    null
                )
            )
            binding?.tvSortingText?.text = getString(R.string.descending)
        } else {
            binding?.ivSortingIcon?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_ascending_icon,
                    null
                )
            )
            binding?.tvSortingText?.text = getString(R.string.ascending)
        }

        val adapterList = imageSectionalAdapter?.getItemList()?.reversed()
        imageSectionalAdapter?.refreshList(adapterList)
    }

    private fun itemClick(mediaData: MediaData) {
        findNavController().navigate(R.id.nav_image_preview)
    }

    private fun actionPerform(isConnect: Boolean, castModel: CastModel?) {
        if (isConnect)
            startCasting(castModel?.routeInfo, castModel?.castDevice)
        else
            stopCasting()
    }

    fun showFolderSortingPrompt() {
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
        binding?.tvFolderName?.text = folderModel.folderName
        imageSectionalAdapter?.refreshList(folderModel.sectionList as ArrayList<SectionModel>?)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun exitPage() {
        findNavController().navigateUp()
    }
}