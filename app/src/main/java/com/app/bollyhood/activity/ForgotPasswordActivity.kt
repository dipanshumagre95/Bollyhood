package com.app.bollyhood.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityForgotPasswordBinding
import com.app.bollyhood.extensions.isvalidMobileNumber
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var mContext: ForgotPasswordActivity
    lateinit var binding: ActivityForgotPasswordBinding
    private val viewModel: DataViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        mContext = this

        initUI()
        addListner()
    }

    private fun initUI() {

        addObserevs()
    }

    private fun addListner() {

        binding.apply {
            ivBack.setOnClickListener {
                finish()
            }

            tvNext.setOnClickListener {
                if (isvalidMobileNumber(mContext, binding.edtMobileNumber.text.toString().trim())) {
                    viewModel.doForgotPassword(binding.edtMobileNumber.text.toString().trim())

                }
            }
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

        viewModel.forgotLiveData.observe(this, Observer {
            if (it.status == "1") {
                Toast.makeText(mContext, it.result.otp, Toast.LENGTH_SHORT).show()
                startActivity(
                    Intent(mContext, ForgotOtpActivity::class.java)
                        .putExtra(
                            StaticData.mobileNumber,
                            binding.edtMobileNumber.text.toString().trim()
                        )
                        .putExtra(StaticData.otp, it.result.otp)
                )

            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        })
    }

}