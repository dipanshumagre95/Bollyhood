package com.app.bollyhood.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.AllAgencyAdapter
import com.app.bollyhood.databinding.ActivityAllAgencyBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.castinglist.CastingDataModel
import com.app.bollyhood.util.DialogsUtils.showCustomToast
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllAgencyActivity : AppCompatActivity(), AllAgencyAdapter.onItemClick {

    lateinit var binding: ActivityAllAgencyBinding
    lateinit var mContext: AllAgencyActivity

    private val viewModel: DataViewModel by viewModels()

    private var list: ArrayList<CastingDataModel> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_agency)
        mContext = this
        initUI()
        addListner()
        addObserevs()

    }

    private fun initUI() {
        if (isNetworkAvailable(this)) {
            viewModel.getAgency(PrefManager(this).getvalue(StaticData.id).toString())
        } else {
            showCustomToast(this,StaticData.networkIssue,getString(R.string.str_error_internet_connections),StaticData.close)
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

        viewModel.agencyLiveData.observe(this, Observer {
            if (it.status == "1") {
                list.clear()
                list.addAll(it.result)
                if (list.size > 0) {
                    binding.rvAgency.visibility = View.VISIBLE
                    binding.tvNoBookings.visibility = View.GONE
                    setData(list)
                } else {
                    binding.rvAgency.visibility = View.GONE
                    binding.tvNoBookings.visibility = View.VISIBLE
                }
            } else {
                binding.rvAgency.visibility = View.GONE
                binding.tvNoBookings.visibility = View.VISIBLE
            }
        })
    }


    private fun setData(castingData: ArrayList<CastingDataModel>) {
        binding.apply {
            rvAgency.layoutManager = LinearLayoutManager(mContext)
            rvAgency.setHasFixedSize(true)
            adapter =
                AllAgencyAdapter(mContext, castingData, this@AllAgencyActivity)
            rvAgency.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onClick(pos: Int, castingDataModel: CastingDataModel) {
        startActivity(
            Intent(mContext, AgencyDetailsActivity::class.java)
                .putExtra("model", Gson().toJson(castingDataModel))
        )
    }
}