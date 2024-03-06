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
import com.app.bollyhood.databinding.ActivityChangePasswordBinding
import com.app.bollyhood.extensions.isValidOldPassword
import com.app.bollyhood.extensions.isvalidBothNewPassword
import com.app.bollyhood.extensions.isvalidConfirmNewPassword
import com.app.bollyhood.extensions.isvalidName
import com.app.bollyhood.extensions.isvalidNewPassword
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityChangePasswordBinding
    lateinit var mContext: ChangePasswordActivity

    private val viewModel: DataViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)
        mContext = this
        initUI()
        addListner()
        addObserevs()

    }

    private fun initUI() {


    }

    private fun addListner() {
        binding.apply {
            ivBack.setOnClickListener {
                finish()
            }
            tvChangePassword.setOnClickListener {
                if (isValidOldPassword(
                        mContext,
                        binding.edtOldPassword.text.toString().trim()
                    ) && isvalidNewPassword(
                        mContext,
                        binding.edtNewPassword.text.toString().trim()
                    ) && isvalidConfirmNewPassword(
                        mContext, binding.edtConfirmNewPassword.text.toString().trim()
                    ) && isvalidBothNewPassword(
                        mContext,
                        binding.edtNewPassword.text.toString().trim(),
                        binding.edtConfirmNewPassword.text.toString().trim()
                    )
                ) {
                    viewModel.doChangePassword(
                        PrefManager(mContext).getvalue(StaticData.id).toString(),
                        binding.edtOldPassword.text.toString().trim(),
                        binding.edtNewPassword.text.toString().trim()
                    )
                }
            }
        }
    }

    private fun addObserevs() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility - View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.changePasswordLiveData.observe(this, Observer {
            if (it.status == "1") {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
                PrefManager(mContext).clearValue()
                startActivity(Intent(mContext, LoginActivity::class.java))
                finishAffinity()
            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        })
    }
}