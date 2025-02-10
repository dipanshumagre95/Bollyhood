package com.app.bollyhood.fragment.profileDetailsFragments

import ImagePickerUtil.playVideo
import ImagePickerUtil.stopVideo
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.activity.MainActivity
import com.app.bollyhood.activity.YoutubeActivity
import com.app.bollyhood.adapter.ActorsProfileWorkLinkAda
import com.app.bollyhood.databinding.FragmentInfluencerProfileDetailBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.PhotoModel
import com.app.bollyhood.model.SingleCategoryModel
import com.app.bollyhood.model.WorkLinkProfileData
import com.app.bollyhood.util.DialogsUtils.createFolderButton
import com.app.bollyhood.util.DialogsUtils.showCustomToast
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InfluencerProfileDetailFragment : Fragment(),OnClickListener, ActorsProfileWorkLinkAda.onItemClick{

    private lateinit var binding: FragmentInfluencerProfileDetailBinding
    private var singleCategoryModel: SingleCategoryModel? =null
    private lateinit var previousFragment:String
    private val viewModel: DataViewModel by viewModels()
    private var workLinkList: ArrayList<WorkLinkProfileData> = arrayListOf()
    private var is_bookmark: Int = 0
    private var photolist:ArrayList<PhotoModel> = arrayListOf()
    private var expanded:Boolean=true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(layoutInflater,R.layout.fragment_influencer_profile_detail, container, false)

        initUi()
        addListener()
        addObserevs()
        return binding.root
    }

    private fun addListener() {
        binding.llbookmark.setOnClickListener(this)
        binding.llBack.setOnClickListener(this)
        binding.llCall.setOnClickListener(this)
    }

    private fun addObserevs() {

        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.successData.observe(requireActivity(), Observer {
            if (it.status == "1") {
                if (it.msg.equals("Bookmarked Successfully")){
                    singleCategoryModel?.is_bookmarked=1
                    binding.ivBookMark.setBackgroundResource(R.drawable.ic_addedbookmark)
                }else{
                    singleCategoryModel?.is_bookmarked=0
                    binding.ivBookMark.setBackgroundResource(R.drawable.ic_bookmark)
                }
                showCustomToast(requireContext(),StaticData.successMsg,it.msg,StaticData.success)
            } else {
                showCustomToast(requireContext(),StaticData.pleaseTryAgain,it.msg,StaticData.alert)
            }
        })

        viewModel.checkSubscriptionLiveData.observe(requireActivity(), Observer {
            if (it.status == "1") {
                if (it.result.is_subscription == "0") {
                    //   startActivity(Intent(requireContext(), SubscriptionPlanActivity::class.java))
                }else{
                    if (singleCategoryModel?.mobile?.isNotEmpty()!!) {
                        val intent =
                            Intent(
                                Intent.ACTION_DIAL, Uri.fromParts("tel",
                                    singleCategoryModel?.mobile, null))
                        startActivity(intent)
                    }

                }
            } else {
                Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initUi() {
        val bundle = arguments
        if (bundle!=null&&bundle?.getString(StaticData.previousFragment)=="AllActosFragment") {
            (requireActivity() as MainActivity).binding.llBottom.setBackgroundResource(R.drawable.rectangle_curve)
            getDataFromJson(bundle)
        }else if (bundle!=null&&bundle?.getString(StaticData.previousFragment)=="BookMark"){
            getDataFromJson(bundle)
        }
    }

    private fun getDataFromJson(bundle: Bundle) {
        singleCategoryModel = Gson().fromJson(
            bundle.getString(StaticData.userModel),
            SingleCategoryModel::class.java
        )
        previousFragment = bundle.getString(StaticData.previousFragment).toString()
        setProfile(singleCategoryModel)
    }

    override fun onClick(item: View?) {
        when(item?.id){

            R.id.llBack ->{
                (requireActivity() as MainActivity).onBackPressed()
            }

            R.id.llbookmark ->{
                if (isNetworkAvailable(requireContext())) {
                    if (singleCategoryModel?.is_bookmarked == 0) {
                        makeProfileBookMark()
                    } else {
                        removeBookMarkDialog()
                    }
                } else {
                    Toast.makeText(
                        requireContext(), getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT
                    ).show()
                }
            }

            R.id.llCall ->{
                if (isNetworkAvailable(requireContext())) {
                    viewModel.checkSubscriptions(
                        PrefManager(requireContext()).getvalue(StaticData.id).toString()
                    )
                } else {
                    Toast.makeText(
                        requireContext(), getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun removeBookMarkDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to remove Bookmark?")

        builder.setPositiveButton(getString(R.string.str_yes)) { dialog, which ->
            if (isNetworkAvailable(requireContext())) {
                viewModel.addRemoveBookMark(
                    PrefManager(requireContext()).getvalue(StaticData.id),
                    singleCategoryModel?.id,
                    "2",
                    "",
                    ""
                )
            } else {
                Toast.makeText(
                    requireContext(), getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT
                ).show()
            }
        }

        builder.setNegativeButton(getString(R.string.str_no)) { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun setProfile(singleCategoryModel: SingleCategoryModel?) {

        binding.apply {
            Glide.with(requireContext()).load(singleCategoryModel?.image).centerCrop().placeholder(R.drawable.ic_profile).into(cvProfile)
            tvName.text = singleCategoryModel?.name

            if (singleCategoryModel?.promotion?.isNotEmpty() == true){
                tvPromotionType.text=singleCategoryModel?.promotion
            }else{
                llPromotionType.visibility=View.GONE
            }

            if (singleCategoryModel?.platforms?.isNotEmpty()==true){
                tvPlatforms.text=singleCategoryModel?.platforms
            }else{
                llPlatforms.visibility=View.GONE
            }

            if (singleCategoryModel?.followers?.isNotEmpty()==true){
                Followers.text=singleCategoryModel?.followers
            }else{
                llFollowers.visibility=View.GONE
            }

            if (singleCategoryModel?.averageviews?.isNotEmpty()==true) {
                tvAverageViews.text = singleCategoryModel?.averageviews
            }else{
                tvAverageViews.visibility=View.GONE
            }


            if (singleCategoryModel?.is_verify == "1") {
                ivVerified.visibility = View.VISIBLE
            } else {
                ivVerified.visibility = View.GONE
            }


            val stringList = arrayListOf<String>()

            for (i in 0 until singleCategoryModel?.categories?.size!!) {
                stringList.add(singleCategoryModel?.categories?.get(i)?.category_name!!)
            }
            tvCategroy.text = stringList.joinToString(separator = " / ")


            if (singleCategoryModel.description.isNullOrEmpty()==false) {
                if (singleCategoryModel.description.length > 150) {
                    val shorttext = singleCategoryModel.description.substring(0, 150)
                    setSpannableString(shorttext, "read more", singleCategoryModel.description)
                } else {
                    binding.tvDescription.text = singleCategoryModel.description
                }
            }else{
                llabout.visibility=View.GONE
            }

            is_bookmark = singleCategoryModel.is_bookmarked

            if (!previousFragment.equals("BookMark")) {
                if (singleCategoryModel.is_bookmarked == 1) {
                    ivBookMark.setBackgroundResource(R.drawable.ic_addedbookmark)
                } else {
                    ivBookMark.setBackgroundResource(R.drawable.ic_bookmark)
                }
            }else{
                llbookmark.visibility=View.GONE
            }

            if (!singleCategoryModel.videos_url.isNullOrEmpty()) {
                if (singleCategoryModel.videos_url.size >1){
                    binding.llyoutubePlayerView1.visibility=View.VISIBLE
                    binding.llyoutubePlayerView2.visibility=View.VISIBLE
                }else{
                    binding.llyoutubePlayerView1.visibility=View.VISIBLE
                    binding.llyoutubePlayerView2.visibility=View.GONE
                }
                for (i in 0 until singleCategoryModel.videos_url.size) {
                    val item = singleCategoryModel.videos_url[i].video_url
                    if (i==0){
                        playVideo(item, binding.youtubePlayerView1)
                    }

                    if (i==1){
                        playVideo(item, binding.youtubePlayerView2)
                    }
                }
            }else{
                binding.llVideoView.visibility=View.GONE
                binding.dancerFrame.visibility=View.GONE
            }


            if (!(singleCategoryModel?.imagefile?.isNullOrEmpty() == true)) {
                for (i in 0 until singleCategoryModel.imagefile.size) {
                    photolist.add(PhotoModel(i, singleCategoryModel.imagefile.get(i)))
                }
                setImages(photolist)
            }else{
                llimage.visibility=View.GONE
            }

            try {
                if (!singleCategoryModel.work_links.isNullOrEmpty()) {
                    for (i in 0 until singleCategoryModel.work_links.size) {
                        val item = singleCategoryModel.work_links[i]
                        val workLink = WorkLinkProfileData(
                            item.worklink_name,
                            item.worklink_url
                        )
                        workLinkList.add(workLink)
                    }
                    setWorkLinksAdapter(workLinkList)
                }else{
                    workLinkFrame.visibility=View.GONE
                    rrworkLink.visibility=View.GONE
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun setSpannableString(text: String, button: String, fulltext: String) {
        val spanTxt = SpannableStringBuilder(text).append(" $button")

        val start = spanTxt.length - button.length
        val end = spanTxt.length

        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (expanded) {
                    setSpannableString(fulltext, "read less", text)
                    expanded=false
                }else{
                    setSpannableString(fulltext, "read more", text)
                    expanded=true
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false // Optional: remove underline
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spanTxt.setSpan(ForegroundColorSpan(Color.parseColor("#EA1874")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        binding.tvDescription.movementMethod = LinkMovementMethod.getInstance()
        binding.tvDescription.setText(spanTxt, TextView.BufferType.SPANNABLE)

    }


    private fun setImagesInView(photolist: ArrayList<PhotoModel>, imageCount: Int) {
        try {
            val imageViews = when (imageCount) {
                1 -> listOf(binding.image1)
                2 -> listOf(binding.twoimage1, binding.twoimage2)
                3 -> listOf(binding.threeimage1, binding.threeimage2, binding.threeimage3)
                4 -> listOf(
                    binding.fourthimage1,
                    binding.fourthimage2,
                    binding.fourthimage3,
                    binding.fourthimage4
                )

                5 -> listOf(
                    binding.fiveimage1,
                    binding.fiveimage2,
                    binding.fiveimage3,
                    binding.fiveimage4,
                    binding.fiveimage5
                )

                6 -> listOf(
                    binding.image1,
                    binding.image2,
                    binding.image3,
                    binding.image4,
                    binding.image5,
                    binding.image6
                )

                else -> emptyList()
            }

            for (i in 0 until photolist.size) {
                Glide.with(requireContext())
                    .load(photolist[i].url)
                    .centerCrop()
                    .placeholder(R.drawable.ic_profile)
                    .into(imageViews[i])
            }
        }catch (e:Exception){
            Log.d("error",e.message.toString())
        }
    }

    private fun setImages(photolist: ArrayList<PhotoModel>)
    {
        when(photolist.size){
            1 ->{
                binding.llsiximage.visibility=View.GONE
                binding.llfiveimage.visibility=View.GONE
                binding.llfourthimage.visibility=View.GONE
                binding.llthreeimage.visibility=View.GONE
                binding.lltwoimage.visibility=View.GONE
                binding.lloneimage.visibility=View.VISIBLE
                setImagesInView(photolist,1)
            }

            2 ->{
                binding.llsiximage.visibility=View.GONE
                binding.llfiveimage.visibility=View.GONE
                binding.llfourthimage.visibility=View.GONE
                binding.llthreeimage.visibility=View.GONE
                binding.lltwoimage.visibility=View.VISIBLE
                binding.lloneimage.visibility=View.GONE
                setImagesInView(photolist,2)
            }

            3 ->{
                binding.llsiximage.visibility=View.GONE
                binding.llfiveimage.visibility=View.GONE
                binding.llfourthimage.visibility=View.GONE
                binding.llthreeimage.visibility=View.VISIBLE
                binding.lltwoimage.visibility=View.GONE
                binding.lloneimage.visibility=View.GONE
                setImagesInView(photolist,3)
            }

            4 ->{
                binding.llsiximage.visibility=View.GONE
                binding.llfiveimage.visibility=View.GONE
                binding.llfourthimage.visibility=View.VISIBLE
                binding.llthreeimage.visibility=View.GONE
                binding.lltwoimage.visibility=View.GONE
                binding.lloneimage.visibility=View.GONE
                setImagesInView(photolist,4)
            }

            5 ->{
                binding.llsiximage.visibility=View.GONE
                binding.llfiveimage.visibility=View.VISIBLE
                binding.llfourthimage.visibility=View.GONE
                binding.llthreeimage.visibility=View.GONE
                binding.lltwoimage.visibility=View.GONE
                binding.lloneimage.visibility=View.GONE
                setImagesInView(photolist,5)
            }

            else ->{
                binding.llsiximage.visibility=View.VISIBLE
                binding.llfiveimage.visibility=View.GONE
                binding.llfourthimage.visibility=View.GONE
                binding.llthreeimage.visibility=View.GONE
                binding.lltwoimage.visibility=View.GONE
                binding.lloneimage.visibility=View.GONE
                setImagesInView(photolist,6)
            }
        }
    }

    private fun setWorkLinksAdapter(
        worklinklist: ArrayList<WorkLinkProfileData>)
    {
        binding.rrworkLink.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rrworkLink.setHasFixedSize(true)
        binding.adapter =
            ActorsProfileWorkLinkAda(requireContext(), worklinklist, this@InfluencerProfileDetailFragment)
        binding.rrworkLink.adapter = binding.adapter
    }

    override fun onitemClick(pos: Int, work: WorkLinkProfileData) {
        startActivity(
            Intent(requireContext(), YoutubeActivity::class.java).putExtra("videoId", work.worklink_url)
        )
    }

    override fun onStop() {
        super.onStop()
        stopVideo()
    }

    private fun makeProfileBookMark()
    {
        createFolderButton(requireContext()) { folder ->
            viewModel.addRemoveBookMark(
                PrefManager(requireContext()).getvalue(StaticData.id), singleCategoryModel?.id, "1",folder.folder_id,folder.folder_name
            )
        }
    }

}