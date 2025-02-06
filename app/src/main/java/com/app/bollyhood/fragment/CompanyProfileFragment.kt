package com.app.bollyhood.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.bollyhood.R
import com.app.bollyhood.activity.MainActivity
import com.app.bollyhood.activity.MyProfileActivity
import com.app.bollyhood.databinding.FragmentCompanyProfileBinding
import com.app.bollyhood.model.SingleCategoryModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray

@AndroidEntryPoint
class CompanyProfileFragment : Fragment(),OnClickListener {

    lateinit var binding:FragmentCompanyProfileBinding
    private var singleCategoryModel: SingleCategoryModel? =null
    lateinit var previousFragment:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as MainActivity).showToolbar(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= DataBindingUtil.inflate(layoutInflater,R.layout.fragment_company_profile, container, false)

        initUi()
        addListener()
        return binding.root
    }

    private fun addListener() {
        binding.let {
            it.ivBack.setOnClickListener(this@CompanyProfileFragment)
            it.cvProfile.setOnClickListener(this@CompanyProfileFragment)
            it.tvInquiry.setOnClickListener(this@CompanyProfileFragment)
        }
    }

    private fun initUi() {

        (requireActivity() as MainActivity).binding.llBottom.setBackgroundResource(R.drawable.rectangle_curve)

        if (PrefManager(requireContext()).getvalue(StaticData.image)?.isNotEmpty() == true) {
            Glide.with(requireContext())
                .load(PrefManager(requireContext()).getvalue(StaticData.image))
                .placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile)
                .into(binding.cvProfile)
        }

        val bundle = arguments
        if (bundle!=null&&(bundle?.getString(StaticData.previousFragment)=="AllActosFragment")||(bundle?.getString(StaticData.previousFragment)=="ProductionHouseFragment")) {
            singleCategoryModel = Gson().fromJson(
                bundle.getString(StaticData.userModel),
                SingleCategoryModel::class.java
            )
            previousFragment = bundle.getString(StaticData.previousFragment).toString()
            setData()
        }
    }

    private fun setData()
    {
        if (singleCategoryModel!=null) {
            binding.let {
                Glide.with(requireContext()).load(singleCategoryModel?.image).centerCrop()
                    .placeholder(R.drawable.ic_profile).into(it.ivImage)
                it.tvCompanyName.setText(singleCategoryModel?.name)
                it.tvLocation.setText(singleCategoryModel?.location)
                it.tvDescription.setText(singleCategoryModel?.description)

                if (singleCategoryModel?.is_verify == "1") {
                    it.ivCompanyVerified.visibility = View.VISIBLE
                } else {
                    it.ivCompanyVerified.visibility = View.GONE
                }

                try {
                    if (!singleCategoryModel?.work_links.isNullOrEmpty()) {
                        val innerArrayStr = singleCategoryModel?.work_links?.get(0)?.worklink_url
                        val innerArray = JSONArray(innerArrayStr)
                        for (i in 0 until innerArray.length()) {
                            val item = innerArray.getJSONObject(i)
                            if (i == 0) {
                                playVideo(item.getString("worklink_url"), binding.playerView1)
                            }

                            if (i == 1) {
                                playVideo(item.getString("worklink_url"), binding.playerView2)
                            }
                        }
                    } else {
                        binding.llVideoView.visibility = View.GONE
                        binding.llrecentWork.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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

    override fun onClick(view: View?) {
        when(view?.id){

            R.id.ivBack ->{
                (requireActivity() as MainActivity).onBackPressed()
            }

            R.id.cvProfile ->{
                startActivity(Intent(requireContext(),MyProfileActivity::class.java))
            }

            R.id.tvInquiry ->{

            }
        }
    }


}