package com.app.bollyhood.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityCastingBookMarkDetailBinding
import com.app.bollyhood.databinding.ActivityCastingCallDetailsBinding
import com.app.bollyhood.databinding.ActivityCastingDetailsBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.CastingCallModel
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CastingBookMarkDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityCastingBookMarkDetailBinding
    lateinit var mContext: CastingBookMarkDetailActivity
    private val viewModel: DataViewModel by viewModels()
    private var type: String? = ""
    lateinit var castingCallModel: CastingCallModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_casting_book_mark_detail)
        mContext = this
        initUI()
        addListner()
        addObsereves()
    }

    private fun initUI() {

        if (intent.extras != null) {
            type = intent.getStringExtra("type")
            castingCallModel =
                Gson().fromJson(intent.getStringExtra("model"), CastingCallModel::class.java)
            setData(castingCallModel)
        }

    }


    private fun addListner() {

        binding.ivBack.setOnClickListener {
            finish()
        }


        binding.tvSave.setOnClickListener {
            if (!binding.tvSave.text.toString().trim().equals("Saved")) {
                if (isNetworkAvailable(mContext)) {
                    if (castingCallModel.is_casting_bookmark == 0) {
                        viewModel.castingBookmark(
                            PrefManager(mContext).getvalue(StaticData.id),
                            castingCallModel.id,
                            "1"
                        )

                    } else {
                        removeBookMarkDialog()
                    }


                } else {
                    Toast.makeText(
                        mContext,
                        getString(R.string.str_error_internet_connections),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                removeBookMarkDialog()
            }
        }
        binding.tvApplyNow.setOnClickListener {
            startActivity(
                Intent(mContext, CastingApplyActivity::class.java)
                    .putExtra("model", Gson().toJson(castingCallModel))
            )
        }
    }

    private fun removeBookMarkDialog() {
        val builder = AlertDialog.Builder(mContext)
        builder.setMessage("Are you sure you want to remove Save Jobs?")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            if (isNetworkAvailable(mContext)) {
                viewModel.castingBookmark(
                    PrefManager(mContext).getvalue(StaticData.id),
                    castingCallModel.id,
                    "2"
                )
            } else {
                Toast.makeText(
                    mContext, getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT
                ).show()
            }
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }


    private fun addObsereves() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })


        viewModel.castingBookmark.observe(this, Observer {
            if (it.status == "1") {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        })

    }


    @SuppressLint("SetTextI18n")
    private fun setData(castingCallModel: CastingCallModel) {
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

            if (castingCallModel.is_apply == 1) {

                binding.tvApplied.visibility = View.VISIBLE
                binding.tvApplyNow.visibility = View.GONE
                /*
                binding.tvApplyNow.text = "Applied"
                binding.tvApplyNow.isFocusable = false
                binding.tvApplyNow.isFocusableInTouchMode = false
                binding.tvApplyNow.isClickable = false*/
            } else {
                binding.tvApplied.visibility = View.GONE
                binding.tvApplyNow.visibility = View.VISIBLE
            }


            if (castingCallModel.is_casting_bookmark == 1) {
                binding.tvSave.text = "Saved"
                binding.tvSave.isFocusable = false
                binding.tvSave.isFocusableInTouchMode = false
                binding.tvSave.isClickable = false
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

    }
}