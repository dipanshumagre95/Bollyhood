package com.app.bollyhood.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityLoginBinding
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.extensions.isvalidMobileNumber
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    lateinit var mContext: LoginActivity
    lateinit var binding: ActivityLoginBinding
    private val viewModel: DataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mContext = this
        addlistner()
        addobsereves()
        setSpannableString()
    }

    private fun addlistner() {
        binding.apply {
            tvSendOtp.setOnClickListener {

                if (isNetworkAvailable(mContext)) {
                    if (isvalidMobileNumber(
                            mContext,
                            binding.edtMobileNumber.text.toString().trim()
                        )
                    ) {

                        viewModel.sendOtp(binding.edtMobileNumber.text.toString().trim())

                    }

                } else {
                    Toast.makeText(
                        mContext, getString(R.string.str_error_internet_connections),
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }

            tvSignUp.setOnClickListener {
                startActivity(Intent(mContext, SignupActivity::class.java))
            }
            tvForgotPassword.setOnClickListener {
                startActivity(Intent(mContext, ForgotPasswordActivity::class.java))
            }

        }
    }

    private fun addobsereves() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })


        viewModel.otpLiveData.observe(this, Observer {
            if (it.status == "1") {

                Toast.makeText(mContext, it.result.otp, Toast.LENGTH_SHORT).show()
                PrefManager(mContext).setvalue(StaticData.image, it.result.image)

                startActivity(
                    Intent(mContext, SendOtpActivity::class.java)
                        .putExtra(
                            StaticData.mobileNumber,
                            binding.edtMobileNumber.text.toString().trim()
                        )
                )

            } else {
                Toast.makeText(mContext, it.msg, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setSpannableString() {
        val spanTxt = SpannableStringBuilder(
            getString(R.string.str_privarcy_policy)
        )
        spanTxt.setSpan(
            ForegroundColorSpan(getColor(R.color.colorgrey)),
            spanTxt.length - getString(R.string.str_privarcy_policy).length,
            spanTxt.length,
            0
        )

        //  binding.tvPrivarcyPolicy.setText(spanTxt, TextView.BufferType.NORMAL)

        spanTxt.append(" Term of service")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(
                    applicationContext, "Term of service Clicked",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, spanTxt.length - " Term of service".length, spanTxt.length, 0)
        // FOR SET TEXT COLOR
        // FOR SET TEXT COLOR
        spanTxt.setSpan(
            ForegroundColorSpan(Color.RED),
            spanTxt.length - " Term of service".length,
            spanTxt.length,
            0
        )

        spanTxt.append(" and")
        spanTxt.setSpan(
            ForegroundColorSpan(getColor(R.color.colorgrey)),
            spanTxt.length - " and".length,
            spanTxt.length,
            0
        )

        spanTxt.append(" Privacy Policy")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(
                    applicationContext, "Privacy Policy Clicked",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, spanTxt.length - " Privacy Policy".length, spanTxt.length, 0)
        // FOR SET TEXT COLOR
        // FOR SET TEXT COLOR
        spanTxt.setSpan(
            ForegroundColorSpan(Color.RED),
            spanTxt.length - " Privacy Policy".length,
            spanTxt.length,
            0
        )


        spanTxt.append(" in use of the app.")
        spanTxt.setSpan(
            ForegroundColorSpan(getColor(R.color.colorgrey)),
            spanTxt.length - " in use of the app.".length,
            spanTxt.length,
            0
        )


        binding.tvPrivarcyPolicy.setMovementMethod(LinkMovementMethod.getInstance())
        binding.tvPrivarcyPolicy.setText(spanTxt, TextView.BufferType.SPANNABLE)


    }
}