package com.app.bollyhood.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityCastingCallDetailsBinding

class CastingCallDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityCastingCallDetailsBinding
    lateinit var mContext: CastingCallDetailsActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_casting_call_details)
        mContext = this
        initUI()
        addListner()
    }
    private fun initUI(){

    }
    private fun addListner(){

    }
}