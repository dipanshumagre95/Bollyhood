package com.app.bollyhood.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.AgencyImagesAdapter
import com.app.bollyhood.databinding.ActivityAgencyDetailsBinding
import com.app.bollyhood.model.castinglist.CastingDataModel
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AgencyDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityAgencyDetailsBinding
    lateinit var mContext: AgencyDetailsActivity
    private val viewModel: DataViewModel by viewModels()
    private var type: String? = ""
    lateinit var model: CastingDataModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agency_details)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        if (intent.extras != null) {
            model = Gson().fromJson(intent.getStringExtra("model"), CastingDataModel::class.java)

            setData(model)
        }
    }

    private fun addListner() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(castingCallModel: CastingDataModel) {
        binding.apply {
            tvOrganization.text = castingCallModel.organization
            tvCompunyName.text = castingCallModel.company_name
            tvLocation.text = castingCallModel.location
            tvShift.text = castingCallModel.shifting + "Hr Shift"
            tvDaysAgo.text = castingCallModel.date
            tvCastingRequirement.text = castingCallModel.requirement
            tvSkillRequirement.text = castingCallModel.skill
            tvRole.text = castingCallModel.role
            if (castingCallModel.price_discussed == "1") {
                tvSalary.text = "T B D"
            } else {
                tvSalary.text = "₹" + castingCallModel.price
            }

            if (castingCallModel.company_logo.isNotEmpty()) {
                Glide.with(mContext).load(castingCallModel.company_logo)
                    .into(ivLogo)
            }


        }

        when (castingCallModel.type) {
            "blue" -> {
                binding.llMain.setBackgroundResource(R.drawable.rectangle_castingcall1)
                binding.tvLocation.setBackgroundResource(R.drawable.rectangle_loc_blue)
                binding.tvVerified.setBackgroundResource(R.drawable.rectangle_loc_blue)
                binding.tvShift.setBackgroundResource(R.drawable.rectangle_loc_blue)
            }

            "red" -> {
                binding.llMain.setBackgroundResource(R.drawable.rectangle_castingcall2)
                binding.tvLocation.setBackgroundResource(R.drawable.rectangle_loc_red)
                binding.tvVerified.setBackgroundResource(R.drawable.rectangle_loc_red)
                binding.tvShift.setBackgroundResource(R.drawable.rectangle_loc_red)
            }

            else -> {
                binding.llMain.setBackgroundResource(R.drawable.rectangle_castingcall3)
                binding.tvLocation.setBackgroundResource(R.drawable.rectangle_loc_yellow)
                binding.tvVerified.setBackgroundResource(R.drawable.rectangle_loc_yellow)
                binding.tvShift.setBackgroundResource(R.drawable.rectangle_loc_yellow)
            }
        }

        if (castingCallModel.images.size > 0) {
            binding.tvImages.visibility = View.VISIBLE
            binding.rvImages.apply {
                layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
                val agencyImagesAdapter = AgencyImagesAdapter(mContext, castingCallModel.images)
                adapter = agencyImagesAdapter
                adapter?.notifyDataSetChanged()

            }

        } else {
            binding.tvImages.visibility = View.GONE
        }


    }

}