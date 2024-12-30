package com.app.bollyhood.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.databinding.ActivityShootingLocationDetailsBinding
import com.app.bollyhood.viewmodel.DataViewModel

class ShootingLocationDetails : AppCompatActivity() {

    lateinit var binding: ActivityShootingLocationDetailsBinding
    private val viewModel: DataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_shooting_location_details)

        initUI()
        addListner()
        addObserevs()
    }

    private fun addObserevs() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })
    }

    private fun addListner() {

    }

    private fun initUI() {

    }
}