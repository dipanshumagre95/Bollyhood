package com.app.bollyhood.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.ExpertiseAdapter
import com.app.bollyhood.databinding.ActivityAllExpertiseProfileBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.ExpertiseModel
import com.app.bollyhood.util.DialogsUtils.showCustomToast
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllExpertiseProfileActivity : AppCompatActivity(), ExpertiseAdapter.onItemClick {

    lateinit var binding: ActivityAllExpertiseProfileBinding
    lateinit var mContext: AllExpertiseProfileActivity
    private val viewModel: DataViewModel by viewModels()
    var experiseList: ArrayList<ExpertiseModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_expertise_profile)
        mContext = this
        initUI()
        addListner()
        addObserevs()
    }

    private fun initUI() {



    }

    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable(mContext)) {
            viewModel.getAllExpertise(PrefManager(mContext).getvalue(StaticData.id).toString())
        } else {
            Toast.makeText(
                mContext,
                getString(R.string.str_error_internet_connections),
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
        viewModel.isLoading.observe(mContext, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.expertiseLiveData.observe(mContext, Observer {
            if (it.status == "1") {
                experiseList.clear()
                experiseList.addAll(it.result)
                setExpertiseAdapter(experiseList)
            } else {
                showCustomToast(this,StaticData.pleaseTryAgain,it.msg,StaticData.alert)
            }
        })
    }

    private fun setExpertiseAdapter(experiseList: ArrayList<ExpertiseModel>) {
        binding.apply {
            rvExpertise.layoutManager =
                GridLayoutManager(mContext, 2)
            rvExpertise.setHasFixedSize(true)
            adapter = ExpertiseAdapter(mContext, experiseList, this@AllExpertiseProfileActivity)
            rvExpertise.adapter = adapter
            adapter?.notifyDataSetChanged()

        }
    }

    override fun onClick(pos: Int, expertiseModel: ExpertiseModel) {
       /* startActivity(
          *//*  Intent(mContext, ProfileDetailActivity::class.java)
                .putExtra(StaticData.userModel,Gson().toJson(expertiseModel))*//*
        )*/

    }

}