package com.app.bollyhood.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.AllAgencyAdapter
import com.app.bollyhood.databinding.ActivityAllAgencyBinding
import com.app.bollyhood.model.castinglist.CastingDataModel
import com.app.bollyhood.model.castinglist.CastingUserData
import com.app.bollyhood.viewmodel.DataViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllAgencyActivity : AppCompatActivity(), AllAgencyAdapter.onItemClick{

    lateinit var binding: ActivityAllAgencyBinding
    lateinit var mContext: AllAgencyActivity
    private val viewModel: DataViewModel by viewModels()

    lateinit var castingUserData: CastingUserData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_agency)
        mContext = this
        initUI()
        addListner()

    }

    private fun initUI() {
        if (intent.extras != null) {
            castingUserData =
                Gson().fromJson(intent.getStringExtra("model"), CastingUserData::class.java)
        }
        setData(castingUserData)
    }

    private fun addListner() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun setData(castingUserData: CastingUserData) {
        binding.apply {
            rvAgency.layoutManager = LinearLayoutManager(mContext)
            rvAgency.setHasFixedSize(true)
            adapter = AllAgencyAdapter(mContext, castingUserData.casting_apply,this@AllAgencyActivity)
            rvAgency.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onClick(pos: Int, castingDataModel: CastingDataModel) {
        startActivity(Intent(mContext,AgencyDetailsActivity::class.java)
            .putExtra("model",Gson().toJson(castingDataModel)))
    }
}