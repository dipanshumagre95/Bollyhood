package com.app.bollyhood.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityPaymentSuccessBinding

class PaymentSuccessActivity : AppCompatActivity() {

    lateinit var binding: ActivityPaymentSuccessBinding
    lateinit var mContext: PaymentSuccessActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_success)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {

    }

    private fun addListner() {

        binding.tvGoBack.setOnClickListener {
            startActivity(Intent(mContext, MainActivity::class.java))
            finish()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(mContext, MainActivity::class.java))
        finish()
    }
}