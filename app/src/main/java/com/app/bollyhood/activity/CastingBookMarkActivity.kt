package com.app.bollyhood.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityCastingBookMarkBinding

class CastingBookMarkActivity : AppCompatActivity() {

    lateinit var binding: ActivityCastingBookMarkBinding
    lateinit var mContext: CastingBookMarkActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_casting_book_mark)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI(){

    }
    private fun addListner(){

    }
}