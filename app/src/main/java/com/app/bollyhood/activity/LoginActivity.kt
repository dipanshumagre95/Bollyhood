package com.app.bollyhood.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.View.OnClickListener
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
import com.app.bollyhood.util.DialogsUtils.showCustomToast
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : AppCompatActivity(),OnClickListener {

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
        binding.tvSendOtp.setOnClickListener(this)
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
                        ).putExtra("Otp",it.result.otp)
                )

            } else if(it.status=="0" && it.msg.equals("User is not exist")){
                startActivity(Intent(mContext, SignupActivity::class.java)
                    .putExtra(
                        StaticData.mobileNumber,
                        binding.edtMobileNumber.text.toString().trim()
                    ))
            } else {
                showCustomToast(this,StaticData.pleaseTryAgain,it.msg,StaticData.alert)
            }
        })
    }

    private fun setSpannableString() {
        val spanTxt = SpannableStringBuilder()

        spanTxt.append(" Term of service")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(mContext,CMSActivity::class.java)
                    .putExtra("mFrom","terms-condition")
                )
            }
        }, spanTxt.length - " Term of service".length, spanTxt.length, 0)
        // FOR SET TEXT COLOR
        // FOR SET TEXT COLOR
        spanTxt.setSpan(
            ForegroundColorSpan(Color.GRAY),
            spanTxt.length - " Term of service".length,
            spanTxt.length,
            0
        )

        spanTxt.append(" Privacy Policy")
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(mContext,CMSActivity::class.java)
                    .putExtra("mFrom","privacy-policy")
                )
            }
        }, spanTxt.length - " Privacy Policy".length, spanTxt.length, 0)
        // FOR SET TEXT COLOR
        // FOR SET TEXT COLOR
        spanTxt.setSpan(
            ForegroundColorSpan(Color.GRAY),
            spanTxt.length - " Privacy Policy".length,
            spanTxt.length,
            0
        )

        binding.tvPrivarcyPolicy.setMovementMethod(LinkMovementMethod.getInstance())
        binding.tvPrivarcyPolicy.setText(spanTxt, TextView.BufferType.SPANNABLE)


    }

    override fun onClick(item: View?) {
        when(item?.id){
            R.id.tvSendOtp ->{
                if (isNetworkAvailable(mContext)) {
                    if (isvalidMobileNumber(
                            mContext,
                            binding.edtMobileNumber.text.toString().trim()
                        )
                    ) {
                        viewModel.sendOtp(binding.edtMobileNumber.text.toString().trim())
                    }

                } else {
                    showCustomToast(this,StaticData.networkIssue,getString(R.string.str_error_internet_connections),StaticData.close)
                }
            }
        }
    }
}