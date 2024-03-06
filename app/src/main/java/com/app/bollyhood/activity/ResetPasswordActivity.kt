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
import com.app.bollyhood.databinding.ActivityResetPasswordBinding
import com.app.bollyhood.extensions.isvalidBothNewPassword
import com.app.bollyhood.extensions.isvalidConfirmNewPassword
import com.app.bollyhood.extensions.isvalidNewPassword
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordActivity : AppCompatActivity() {

    lateinit var mContext: ResetPasswordActivity
    lateinit var binding: ActivityResetPasswordBinding
    private val viewModel: DataViewModel by viewModels()
    private var mobileNumber: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password)
        mContext = this
        initUI()
        addListner()
        addObserevs()
    }

    private fun initUI() {

        if (intent.extras != null) {
            mobileNumber = intent.getStringExtra(StaticData.mobileNumber)
        }
    }

    private fun addListner() {

        binding.apply {
            ivBack.setOnClickListener {
                finish()
            }
            tvSubmit.setOnClickListener {
                if (isvalidNewPassword(
                        mContext,
                        binding.edtPassword.text.toString().trim()
                    ) && isvalidConfirmNewPassword(
                        mContext, binding.edtConfirmNewPassword.text.toString().trim()
                    ) && isvalidBothNewPassword(
                        mContext,
                        binding.edtPassword.text.toString().trim(),
                        binding.edtConfirmNewPassword.text.toString().trim()
                    )
                ) {
                    viewModel.doResetPassword(
                        mobileNumber.toString(), binding.edtPassword.text.toString().trim()
                    )
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

        viewModel.resetPasswordLiveData.observe(this, Observer { res ->
            if (res.status == "1") {
                Toast.makeText(mContext, res.msg, Toast.LENGTH_SHORT).show()
                startActivity(Intent(mContext, LoginActivity::class.java))
                finishAffinity()
            } else {
                Toast.makeText(mContext, res?.msg, Toast.LENGTH_SHORT).show()
            }
        })


    }
}