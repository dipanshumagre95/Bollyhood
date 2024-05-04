package com.app.bollyhood.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityCmsactivityBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.CMSModel
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CMSActivity : AppCompatActivity() {

    lateinit var binding: ActivityCmsactivityBinding
    lateinit var mContext: CMSActivity
    private val viewModel: DataViewModel by viewModels()
    private var mFrom: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cmsactivity)
        mContext = this
        initUI()
        addListner()
        addObserevs()
    }

    private fun initUI() {
        if (intent.extras != null) {
            mFrom = intent.getStringExtra("mFrom").toString()
        }

        if (isNetworkAvailable(mContext)) {
            viewModel.getCMS(mFrom)
        } else {
            Toast.makeText(
                mContext, getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addListner() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun addObserevs() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.cmsLiveData.observe(this, Observer {
            if (it.status == "1") {
                setData(it.result)
            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setData(result: ArrayList<CMSModel>) {

        binding.webview.settings.javaScriptEnabled = true
        binding.webview.settings.allowFileAccess = true
        binding.webview.loadDataWithBaseURL(
            null,
            result[0].description,
            "text/html",
            "UTF-8",
            null
        );
        ;


        when (result[0].type) {
            "privacy-policy" -> {
                binding.tvTitle.text = getString(R.string.str_title_privacy_policy)
            }

            "terms-condition" -> {
                binding.tvTitle.text = getString(R.string.str_title_terms_and_conditions)

            }

            "about-us" -> {
                binding.tvTitle.text = getString(R.string.str_aboutus)

            }
        }
    }
}