package com.app.bollyhood.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.WorkAdapter
import com.app.bollyhood.databinding.ActivityBookingDetailBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.BookingModel
import com.app.bollyhood.model.ExpertiseModel
import com.app.bollyhood.model.WorkLinks
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingDetailActivity : AppCompatActivity(), WorkAdapter.onItemClick {

    lateinit var binding: ActivityBookingDetailBinding
    lateinit var mContext: BookingDetailActivity
    private val viewModel: DataViewModel by viewModels()
    private var Id: String? = ""
    lateinit var bookingModel: BookingModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking_detail)
        mContext = this
        initUI()
        addListner()
        addObserevs()
    }

    private fun initUI() {
        if (intent.extras != null) {
            bookingModel = Gson().fromJson(
                intent.getStringExtra(StaticData.userModel),
                BookingModel::class.java
            )
            setData(bookingModel)
        }
/*
        if (isNetworkAvailable(mContext)) {
            viewModel.getExpertiseProfileDetail(Id, PrefManager(mContext).getvalue(StaticData.id))
        } else {
            Toast.makeText(
                mContext, getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT
            ).show()
        }*/

    }

    private fun addListner() {

        binding.llBack.setOnClickListener {
            finish()
        }


    }

    private fun addObserevs() {
       /* viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.expertiseProfileLiveData.observe(this, Observer {
            if (it.status == "1") {
                setData(it.result[0])
            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        })
*/
    }

    private fun setData(expertiseModel: BookingModel) {
        binding.apply {
            Glide.with(mContext)
                .load(expertiseModel.image)
                .into(ivImage)
            tvName.text = expertiseModel.name

            if (expertiseModel.is_verify == "1") {
                ivVerified.visibility = View.VISIBLE
            } else {
                ivVerified.visibility = View.GONE
            }


            val stringList = arrayListOf<String>()

            for (i in 0 until expertiseModel.categories.size) {
                stringList.add(expertiseModel.categories[i].category_name)
            }
            tvCategory.text = stringList.joinToString(separator = " / ")

            tvDescription.text = expertiseModel.description

            tvJobsDone.text = expertiseModel.jobs_done
            tvExperience.text = expertiseModel.experience
            tvReviews.text = expertiseModel.reviews



            rvWorkLinks.layoutManager =
                LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            rvWorkLinks.setHasFixedSize(true)
            val adapter =
                WorkAdapter(mContext, expertiseModel.work_links, this@BookingDetailActivity)
            rvWorkLinks.adapter = adapter
        }
    }

    override fun onClick(pos: Int, work: WorkLinks) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(work.worklink_url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.google.android.youtube")
        startActivity(intent)
    }

}