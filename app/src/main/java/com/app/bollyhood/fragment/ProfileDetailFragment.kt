package com.app.bollyhood.fragment

import Categorie
import android.app.AlertDialog
import android.app.Dialog
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
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.activity.MainActivity
import com.app.bollyhood.activity.YoutubeActivity
import com.app.bollyhood.adapter.ImagesAdapter
import com.app.bollyhood.adapter.WorkAdapter
import com.app.bollyhood.databinding.FragmentProfileDetailBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.ExpertiseModel
import com.app.bollyhood.model.PhotoModel
import com.app.bollyhood.model.SingleCategoryModel
import com.app.bollyhood.model.WorkLinkProfileData
import com.app.bollyhood.util.DialogsUtils.createFolderButton
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray

@AndroidEntryPoint
class ProfileDetailFragment : Fragment(),WorkAdapter.onItemClick,OnClickListener,ImagesAdapter.onItemClick {

    lateinit var binding: FragmentProfileDetailBinding
    private val viewModel: DataViewModel by viewModels()
    private var is_bookmark: Int = 0
    private var expertiseModel: ExpertiseModel?=null
    private var singleCategoryModel: SingleCategoryModel? =null
    lateinit var previousFragment:String
    private var workLinkList: ArrayList<WorkLinkProfileData> = arrayListOf()
    private var photolist:ArrayList<PhotoModel> = arrayListOf()
    var expanded:Boolean=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as MainActivity).showToolbar(false)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding= DataBindingUtil.inflate(layoutInflater,R.layout.fragment_profile_detail, container, false)

        initUI()
        addListner()
        addObserevs()

        return binding.root
    }

    private fun initUI() {
        (requireActivity() as MainActivity).binding.llBottom.setBackgroundResource(R.drawable.rectangle_curve)

        val bundle = arguments
        if (bundle!=null&&bundle?.getString(StaticData.previousFragment)=="AllActosFragment"){
            singleCategoryModel = Gson().fromJson(
                bundle.getString(StaticData.userModel),
                SingleCategoryModel::class.java
            )
            previousFragment = bundle.getString(StaticData.previousFragment).toString()

            when(singleCategoryModel?.category_name){

                Categorie.ACTOR.toString() ->{
                    setActorsProfile(singleCategoryModel)
                    binding.prfileActors.visibility=View.VISIBLE
                    binding.prfileSinger.visibility=View.GONE
                    binding.prfileDance.visibility=View.GONE
                    binding.prfileInfluencer.visibility=View.GONE
                }

                Categorie.SINGER.toString(),Categorie.DJ.toString() ->{
                    setSingerNDjProfile(singleCategoryModel)
                    binding.prfileActors.visibility=View.GONE
                    binding.prfileSinger.visibility=View.VISIBLE
                    binding.prfileDance.visibility=View.GONE
                    binding.prfileInfluencer.visibility=View.GONE
                }

                Categorie.DANCER.toString() ->{
                    setDancerProfile(singleCategoryModel)
                    binding.prfileActors.visibility=View.GONE
                    binding.prfileSinger.visibility=View.GONE
                    binding.prfileDance.visibility=View.VISIBLE
                    binding.prfileInfluencer.visibility=View.GONE
                }

                Categorie.INFLUENCER.toString()->{
                    setInfluencerProfile(singleCategoryModel)
                    binding.prfileActors.visibility=View.GONE
                    binding.prfileSinger.visibility=View.GONE
                    binding.prfileDance.visibility=View.GONE
                    binding.prfileInfluencer.visibility=View.VISIBLE
                }
            }
        }else {
            if (bundle != null) {
                expertiseModel = Gson().fromJson(
                    bundle.getString(StaticData.userModel),
                    ExpertiseModel::class.java
                )
                previousFragment = bundle.getString(StaticData.previousFragment).toString()
                setData(expertiseModel)
            }
        }

    }

    private fun setData(expertiseModel: ExpertiseModel?) {

        binding.apply {
            Glide.with(requireContext()).load(expertiseModel?.image).placeholder(R.drawable.ic_profile).into(ivImage)
            tvName.text = expertiseModel?.name
            if (expertiseModel?.is_verify == "1") {
                ivVerified.visibility = View.VISIBLE
            } else {
                ivVerified.visibility = View.GONE
            }


            val stringList = arrayListOf<String>()

            for (i in 0 until expertiseModel?.categories?.size!!) {
                stringList.add(expertiseModel?.categories?.get(i)?.category_name!!)
            }
            tvCategory.text = stringList.joinToString(separator = " / ")

            if (expertiseModel.description.length > 150){
                val shorttext=expertiseModel.description.substring(0,150)
                setSpannableString(shorttext,"read more",expertiseModel.description,"")
            }else{
                binding.tvDescription.text = expertiseModel.description
            }



            is_bookmark = expertiseModel.is_bookmarked

            if (expertiseModel.is_bookmarked == 1) {
                ivBookMark.setImageResource(R.drawable.ic_addedbookmark)
            } else {
                ivBookMark.setImageResource(R.drawable.ic_bookmark)
            }

            try {
                if (!expertiseModel.work_links.isNullOrEmpty()) {
                    val innerArrayStr = expertiseModel.work_links[0].worklink_url
                    val innerArray = JSONArray(innerArrayStr)
                    for (i in 0 until innerArray.length()) {
                        val item = innerArray.getJSONObject(i)
                        val workLink = WorkLinkProfileData(
                            item.getString("worklink_name"),
                            item.getString("worklink_url")
                        )
                        workLinkList.add(workLink)
                    }
                    setWorkLinksAdapter(workLinkList, binding.rvWorkLinks)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun setActorsProfile(singleCategoryModel: SingleCategoryModel?) {

        binding.apply {
            Glide.with(requireContext()).load(singleCategoryModel?.image).centerCrop().placeholder(R.drawable.ic_profile).into(ivImage)
            tvName.text = singleCategoryModel?.name
            tvage.text=singleCategoryModel?.age
            tvbodytype.text=singleCategoryModel?.body_type
            tvheight.text=singleCategoryModel?.height
            skinColor.text=singleCategoryModel?.skin_color
            tvpassport.text=singleCategoryModel?.passport
            tvlocation.text=singleCategoryModel?.location
            if (singleCategoryModel?.is_verify == "1") {
                ivVerified.visibility = View.VISIBLE
            } else {
                ivVerified.visibility = View.GONE
            }


            val stringList = arrayListOf<String>()

            for (i in 0 until singleCategoryModel?.categories?.size!!) {
                stringList.add(singleCategoryModel?.categories?.get(i)?.category_name!!)
            }
            tvCategory.text = stringList.joinToString(separator = " / ")

            if (singleCategoryModel.description.length > 150){
                val shorttext=singleCategoryModel.description.substring(0,150)
                setSpannableString(shorttext,"read more",singleCategoryModel.description,singleCategoryModel.category_name)
            }else{
                binding.tvDescription.text = singleCategoryModel.description
            }



            is_bookmark = singleCategoryModel.is_bookmarked

            if (singleCategoryModel.is_bookmarked == 1) {
                ivBookMark.setImageResource(R.drawable.ic_addedbookmark)
            } else {
                ivBookMark.setImageResource(R.drawable.ic_bookmark)
            }


            for (i in 0 until singleCategoryModel.imagefile.size){
                 photolist.add(PhotoModel(i, singleCategoryModel.imagefile.get(i)))
            }

            recyclerviewPhotos.layoutManager = GridLayoutManager(requireContext(),3)
            recyclerviewPhotos.setHasFixedSize(true)
            adapter = ImagesAdapter(requireContext(), photolist, this@ProfileDetailFragment)
            recyclerviewPhotos.adapter = adapter
            adapter?.notifyDataSetChanged()



            try {
                if (!singleCategoryModel.work_links.isNullOrEmpty()) {
                    val innerArrayStr = singleCategoryModel.work_links[0].worklink_url
                    val innerArray = JSONArray(innerArrayStr)
                    for (i in 0 until innerArray.length()) {
                        val item = innerArray.getJSONObject(i)
                        val workLink = WorkLinkProfileData(
                            item.getString("worklink_name"),
                            item.getString("worklink_url")
                        )
                        workLinkList.add(workLink)
                    }
                    setWorkLinksAdapter(workLinkList, binding.rvWorkLinks)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
    }

    private fun setSingerNDjProfile(singleCategoryModel: SingleCategoryModel?) {

        binding.apply {
            Glide.with(requireContext()).load(singleCategoryModel?.image).centerCrop().placeholder(R.drawable.ic_profile).into(ivImage)
            tvsingerName.text = singleCategoryModel?.name
            tvAchievements.text=singleCategoryModel?.achievements
            tvGenre.text=singleCategoryModel?.genre
            tvEvents.text=singleCategoryModel?.events

            when(singleCategoryModel?.category_name){
                Categorie.SINGER.toString() ->{
                    tvLanguagestitle.text="Languages"
                    tvLanguages.text=singleCategoryModel?.languages
                }

                Categorie.DJ.toString() ->{
                    tvLanguagestitle.text="Location"
                    tvLanguages.text=singleCategoryModel?.location
                }
            }

            if (singleCategoryModel?.is_verify == "1") {
                ivsingerVerified.visibility = View.VISIBLE
            } else {
                ivsingerVerified.visibility = View.GONE
            }


            val stringList = arrayListOf<String>()

            for (i in 0 until singleCategoryModel?.categories?.size!!) {
                stringList.add(singleCategoryModel?.categories?.get(i)?.category_name!!)
            }
            tvCategory.text = stringList.joinToString(separator = " / ")

            if (singleCategoryModel.description.length > 150){
                val shorttext=singleCategoryModel.description.substring(0,150)
                setSpannableString(shorttext,"read more",singleCategoryModel.description,singleCategoryModel.category_name)
            }else{
                binding.tvsingerDescription.text = singleCategoryModel.description
            }



            is_bookmark = singleCategoryModel.is_bookmarked

            if (singleCategoryModel.is_bookmarked == 1) {
                ivBookMark.setImageResource(R.drawable.ic_addedbookmark)
            } else {
                ivBookMark.setImageResource(R.drawable.ic_bookmark)
            }

            if (!singleCategoryModel.videos_url[0].video_url.equals("[]")) {
                val innerArrayStr = singleCategoryModel.videos_url[0].video_url
                val innerArray = JSONArray(innerArrayStr)
                for (i in 0 until innerArray.length()) {
                    val item = innerArray.getJSONObject(i)
                    if (i==0){
                        playVideo(item.getString("video_url"),binding.youtubePlayerView)
                    }
                }
            }else{
                binding.llVideoView.visibility=View.GONE
                binding.llShowreel.visibility=View.GONE
            }

            for (i in 0 until singleCategoryModel.imagefile.size){
                photolist.add(PhotoModel(i, singleCategoryModel.imagefile.get(i)))
            }

            SingerrecyclerviewPhotos.layoutManager = GridLayoutManager(requireContext(),3)
            SingerrecyclerviewPhotos.setHasFixedSize(true)
            adapter = ImagesAdapter(requireContext(), photolist, this@ProfileDetailFragment)
            SingerrecyclerviewPhotos.adapter = adapter
            adapter?.notifyDataSetChanged()



            try {
                if (!singleCategoryModel.work_links.isNullOrEmpty()) {
                    val innerArrayStr = singleCategoryModel.work_links[0].worklink_url
                    val innerArray = JSONArray(innerArrayStr)
                    for (i in 0 until innerArray.length()) {
                        val item = innerArray.getJSONObject(i)
                        val workLink = WorkLinkProfileData(
                            item.getString("worklink_name"),
                            item.getString("worklink_url")
                        )
                        workLinkList.add(workLink)
                    }
                    setWorkLinksAdapter(workLinkList,binding.rvSingerWorkLinks)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
    }

    private fun setDancerProfile(singleCategoryModel: SingleCategoryModel?) {

        binding.apply {
            Glide.with(requireContext()).load(singleCategoryModel?.image).centerCrop().placeholder(R.drawable.ic_profile).into(ivImage)
            tvDanceName.text = singleCategoryModel?.name
            tvDanceAchievements.text=singleCategoryModel?.achievements
            tvDanceLocation.text=singleCategoryModel?.location
            tvDanceForms.text=singleCategoryModel?.dancer_form
            tvDancewhatI.text=singleCategoryModel?.what_i_do

            if (singleCategoryModel?.is_verify == "1") {
                ivDanceVerified.visibility = View.VISIBLE
            } else {
                ivDanceVerified.visibility = View.GONE
            }


            val stringList = arrayListOf<String>()

            for (i in 0 until singleCategoryModel?.categories?.size!!) {
                stringList.add(singleCategoryModel?.categories?.get(i)?.category_name!!)
            }
            tvDanceCategory.text = stringList.joinToString(separator = " / ")

            if (singleCategoryModel.description.length > 150){
                val shorttext=singleCategoryModel.description.substring(0,150)
                setSpannableString(shorttext,"read more",singleCategoryModel.description,singleCategoryModel.category_name)
            }else{
                binding.tvDanceDescription.text = singleCategoryModel.description
            }



            is_bookmark = singleCategoryModel.is_bookmarked

            if (singleCategoryModel.is_bookmarked == 1) {
                ivBookMark.setImageResource(R.drawable.ic_addedbookmark)
            } else {
                ivBookMark.setImageResource(R.drawable.ic_bookmark)
            }

            if (!singleCategoryModel.videos_url[0].video_url.equals("[]")) {
                val innerArrayStr = singleCategoryModel.videos_url[0].video_url
                val innerArray = JSONArray(innerArrayStr)
                if (innerArray.length()>1){
                    binding.llDanceyoutubePlayerView1.visibility=View.VISIBLE
                    binding.llDanceyoutubePlayerView2.visibility=View.VISIBLE
                }else{
                    binding.llDanceyoutubePlayerView1.visibility=View.VISIBLE
                    binding.llDanceyoutubePlayerView2.visibility=View.GONE
                }
                for (i in 0 until innerArray.length()) {
                    val item = innerArray.getJSONObject(i)
                    if (i==0){
                        playVideo(item.getString("video_url"), binding.DanceyoutubePlayerView1)
                    }

                    if (i==1){
                        playVideo(item.getString("video_url"), binding.DanceyoutubePlayerView2)
                    }
                }
            }else{
                binding.llDanceVideoView.visibility=View.GONE
                binding.llDanceShowreel.visibility=View.GONE
            }

            for (i in 0 until singleCategoryModel.imagefile.size){
                photolist.add(PhotoModel(i, singleCategoryModel.imagefile.get(i)))
            }

            DancerecyclerviewPhotos.layoutManager = GridLayoutManager(requireContext(),3)
            DancerecyclerviewPhotos.setHasFixedSize(true)
            adapter = ImagesAdapter(requireContext(), photolist, this@ProfileDetailFragment)
            DancerecyclerviewPhotos.adapter = adapter
            adapter?.notifyDataSetChanged()



            try {
                if (!singleCategoryModel.work_links.isNullOrEmpty()) {
                    val innerArrayStr = singleCategoryModel.work_links[0].worklink_url
                    val innerArray = JSONArray(innerArrayStr)
                    for (i in 0 until innerArray.length()) {
                        val item = innerArray.getJSONObject(i)
                        val workLink = WorkLinkProfileData(
                            item.getString("worklink_name"),
                            item.getString("worklink_url")
                        )
                        workLinkList.add(workLink)
                    }
                    setWorkLinksAdapter(workLinkList,binding.rvDanceWorkLinks)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
    }

    private fun setInfluencerProfile(singleCategoryModel: SingleCategoryModel?) {

        binding.apply {
            Glide.with(requireContext()).load(singleCategoryModel?.image).centerCrop().placeholder(R.drawable.ic_profile).into(ivImage)
            tvInfluencerName.text = singleCategoryModel?.name
            tvInfluencerAverageLikes.text=singleCategoryModel?.average_like
            tvInfluencerCollaboratedWith.text=singleCategoryModel?.collaborate
            tvInfluencerPromotionType.text=singleCategoryModel?.promotion
            tvInfluencerAverageReel.text=singleCategoryModel?.average_reel_like

            if (singleCategoryModel?.is_verify == "1") {
                ivInfluencerVerified.visibility = View.VISIBLE
            } else {
                ivInfluencerVerified.visibility = View.GONE
            }


            val stringList = arrayListOf<String>()

            for (i in 0 until singleCategoryModel?.categories?.size!!) {
                stringList.add(singleCategoryModel?.categories?.get(i)?.category_name!!)
            }
            tvInfluencerCategory.text = stringList.joinToString(separator = " / ")

            if (singleCategoryModel.description.length > 150){
                val shorttext=singleCategoryModel.description.substring(0,150)
                setSpannableString(shorttext,"read more",singleCategoryModel.description,singleCategoryModel.category_name)
            }else{
                binding.tvInfluencerDescription.text = singleCategoryModel.description
            }



            is_bookmark = singleCategoryModel.is_bookmarked

            if (singleCategoryModel.is_bookmarked == 1) {
                ivBookMark.setImageResource(R.drawable.ic_addedbookmark)
            } else {
                ivBookMark.setImageResource(R.drawable.ic_bookmark)
            }

            if (!singleCategoryModel.videos_url[0].video_url.equals("[]")) {
                val innerArrayStr = singleCategoryModel.videos_url[0].video_url
                val innerArray = JSONArray(innerArrayStr)
                if (innerArray.length()>1){
                    binding.llInfluenceryoutubePlayerView1.visibility=View.VISIBLE
                    binding.llInfluenceryoutubePlayerView2.visibility=View.VISIBLE
                }else{
                    binding.llInfluenceryoutubePlayerView1.visibility=View.VISIBLE
                    binding.llInfluenceryoutubePlayerView2.visibility=View.GONE
                }
                for (i in 0 until innerArray.length()) {
                    val item = innerArray.getJSONObject(i)
                    if (i==0){
                        playVideo(item.getString("video_url"), binding.InfluenceryoutubePlayerView1)
                    }

                    if (i==1){
                        playVideo(item.getString("video_url"), binding.InfluenceryoutubePlayerView2)
                    }
                }
            }else{
                binding.llInfluencerVideoView.visibility=View.GONE
                binding.llInfluencerShowreel.visibility=View.GONE
            }

            for (i in 0 until singleCategoryModel.imagefile.size){
                photolist.add(PhotoModel(i, singleCategoryModel.imagefile.get(i)))
            }

            InfluencerrecyclerviewPhotos.layoutManager = GridLayoutManager(requireContext(),3)
            InfluencerrecyclerviewPhotos.setHasFixedSize(true)
            adapter = ImagesAdapter(requireContext(), photolist, this@ProfileDetailFragment)
            InfluencerrecyclerviewPhotos.adapter = adapter
            adapter?.notifyDataSetChanged()



            try {
                if (!singleCategoryModel.work_links.isNullOrEmpty()) {
                    val innerArrayStr = singleCategoryModel.work_links[0].worklink_url
                    val innerArray = JSONArray(innerArrayStr)
                    for (i in 0 until innerArray.length()) {
                        val item = innerArray.getJSONObject(i)
                        val workLink = WorkLinkProfileData(
                            item.getString("worklink_name"),
                            item.getString("worklink_url")
                        )
                        workLinkList.add(workLink)
                    }
                    setWorkLinksAdapter(workLinkList,binding.rvInfluencerWorkLinks)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
    }

    private fun playVideo(videoUrl: String, youtubePlayerView: YouTubePlayerView) {
        val options = IFramePlayerOptions.Builder()
            .controls(0)
            .build()


        val listener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                val videoId = extractVideoId(videoUrl) ?: ""
                youTubePlayer.loadVideo(videoId, 0f)
            }

            override fun onStateChange(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer, state: PlayerConstants.PlayerState) {
            }
        }

        youtubePlayerView.addYouTubePlayerListener(listener)
        youtubePlayerView.enableAutomaticInitialization = false
        youtubePlayerView.initialize(listener, options)
    }

    fun extractVideoId(url: String): String? {
        val pattern = "(?<=youtu\\.be/|watch\\?v=|/videos/|embed\\/|youtu\\.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2F|shorts/)[^#\\&\\?\\n]*"
        val regex = Regex(pattern)
        val matchResult = regex.find(url)
        return matchResult?.value
    }

    private fun setWorkLinksAdapter(
        worklinklist: ArrayList<WorkLinkProfileData>,
        view: RecyclerView
    )
    {
        view.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        view.setHasFixedSize(true)
        val adapter =
            WorkAdapter(requireContext(), worklinklist, this@ProfileDetailFragment)
        view.adapter = adapter
    }

    override fun onitemClick(pos: Int, work: WorkLinkProfileData) {
        startActivity(
            Intent(requireContext(), YoutubeActivity::class.java).putExtra("videoId", work.worklink_url)
        )
    }

    private fun addListner() {
        binding.llBack.setOnClickListener(this)
        binding.llbookmark.setOnClickListener(this)
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
                if (it.msg.equals("Bookmark Successfully")){
                    binding.ivBookMark.setImageResource(R.drawable.ic_addedbookmark)
                }else{
                    binding.ivBookMark.setImageResource(R.drawable.ic_bookmark)
                }
                Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.checkSubscriptionLiveData.observe(requireActivity(), Observer {
            if (it.status == "1") {
                if (it.result.is_subscription == "0") {
                   // startActivity(Intent(requireContext(), SubscriptionPlanActivity::class.java))
                }else{
                    if (expertiseModel?.mobile?.isNotEmpty()!!) {
                        val intent =
                            Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                                expertiseModel?.mobile, null))
                        startActivity(intent)
                    }

                }
            } else {
                Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun removeBookMarkDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to remove Bookmark?")

        builder.setPositiveButton(getString(R.string.str_yes)) { dialog, which ->
            if (isNetworkAvailable(requireContext())) {
                viewModel.addRemoveBookMark(
                    PrefManager(requireContext()).getvalue(StaticData.id),
                    expertiseModel?.id,
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

    private fun setSpannableString(text: String, button: String, fulltext: String,categorie: String) {
        val spanTxt = SpannableStringBuilder(text).append(" $button")

        val start = spanTxt.length - button.length
        val end = spanTxt.length

        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                if (expanded) {
                    setSpannableString(fulltext, "read less", text,categorie)
                    expanded=false
                }else{
                    setSpannableString(fulltext, "read more", text,categorie)
                    expanded=true
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false // Optional: remove underline
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spanTxt.setSpan(ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        when(categorie){
            Categorie.SINGER.toString(),Categorie.DJ.toString() ->{
                binding.tvsingerDescription.movementMethod = LinkMovementMethod.getInstance()
                binding.tvsingerDescription.setText(spanTxt, TextView.BufferType.SPANNABLE)
            }

            Categorie.DANCER.toString() ->{
                binding.tvDanceDescription.movementMethod = LinkMovementMethod.getInstance()
                binding.tvDanceDescription.setText(spanTxt, TextView.BufferType.SPANNABLE)
            }

            Categorie.INFLUENCER.toString() ->{
                binding.tvInfluencerDescription.movementMethod = LinkMovementMethod.getInstance()
                binding.tvInfluencerDescription.setText(spanTxt, TextView.BufferType.SPANNABLE)
            }

            else ->{
                binding.tvDescription.movementMethod = LinkMovementMethod.getInstance()
                binding.tvDescription.setText(spanTxt, TextView.BufferType.SPANNABLE)
            }
        }

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

    override fun onRemoveImage(pos: Int, photoModel: PhotoModel) {
        addWorkLinksDialog(photoModel)
    }

    private fun addWorkLinksDialog(photoModel: PhotoModel){
        val dialogView = Dialog(requireContext())
        dialogView.setContentView(R.layout.show_image_layout)

        val image=dialogView.findViewById<ImageView>(R.id.ivImage)

        Glide.with(requireContext()).load(photoModel.url)
            .centerCrop()
            .into(image)


        dialogView.show()
    }

    private fun  makeProfileBookMark()
    {
        createFolderButton(requireContext()) { folder ->
            viewModel.addRemoveBookMark(
                PrefManager(requireContext()).getvalue(StaticData.id), singleCategoryModel?.id, "1",folder.folder_id,folder.folder_name
            )
        }
    }

}