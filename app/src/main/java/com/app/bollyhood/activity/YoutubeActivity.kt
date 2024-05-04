package com.app.bollyhood.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityYoutubeBinding
import com.app.bollyhood.util.YTubePlayerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YoutubeActivity : AppCompatActivity() {

    lateinit var binding: ActivityYoutubeBinding
    lateinit var mContext: YoutubeActivity
    private var url: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_youtube)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        if (intent.extras != null) {
            url = intent.getStringExtra("videoId")
        }

        binding.youtubePlayerView.setInstanseOfActivity(this)

        val yTubePlayerView: YTubePlayerView = binding.youtubePlayerView
        yTubePlayerView.loadUrl("https://www.youtube.com/embed/$url?autoplay=1")

    }


    private fun addListner() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        binding.youtubePlayerView.loadUrl("")
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        binding.youtubePlayerView.loadUrl("")
        finish()
    }


}