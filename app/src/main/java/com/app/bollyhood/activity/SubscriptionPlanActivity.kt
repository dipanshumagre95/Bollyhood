package com.app.bollyhood.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.PlanAdapter
import com.app.bollyhood.databinding.ActivitySubscriptionPlanBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.PlanModel
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubscriptionPlanActivity : AppCompatActivity(), PlanAdapter.onItemClick {

    lateinit var binding: ActivitySubscriptionPlanBinding
    lateinit var mContext: SubscriptionPlanActivity
    private val viewModel: DataViewModel by viewModels()
    private val planList: ArrayList<PlanModel> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_subscription_plan)
        mContext = this
        initUI()
        addListner()
        addObsereves()
    }

    private fun initUI() {
        if (isNetworkAvailable(mContext)) {
            viewModel.getPlan()
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

    private fun addObsereves() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.subscriptionPlanLiveData.observe(this, Observer {
            if (it.status.equals("1")){
                planList.clear()
                planList.addAll(it.result)
                setAdapter(planList)
            }else{
                Toast.makeText(mContext,it.msg,Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setAdapter(planList: ArrayList<PlanModel>) {
        binding.apply {
            rvPlan.layoutManager = LinearLayoutManager(mContext)
            rvPlan.setHasFixedSize(true)
            adapter = PlanAdapter(mContext, planList, this@SubscriptionPlanActivity)
            rvPlan.adapter = adapter
            adapter?.notifyDataSetChanged()
        }

    }

    override fun onClick(position: Int, planModel: PlanModel) {

    }
}