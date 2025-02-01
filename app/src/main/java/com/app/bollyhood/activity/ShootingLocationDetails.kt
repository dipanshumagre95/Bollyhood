package com.app.bollyhood.activity

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.adapter.ImageViewPagerAdapter
import com.app.bollyhood.databinding.ActivityShootingLocationDetailsBinding
import com.app.bollyhood.viewmodel.DataViewModel

class ShootingLocationDetails : AppCompatActivity(),OnClickListener{

    lateinit var binding: ActivityShootingLocationDetailsBinding
    private val viewModel: DataViewModel by viewModels()
    private var ImageList= ArrayList<String>()

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
        binding.apply {
            leftSide.setOnClickListener(this@ShootingLocationDetails)
            rightSide.setOnClickListener(this@ShootingLocationDetails)
            llBack.setOnClickListener(this@ShootingLocationDetails)
        }
    }

    private fun setAdapter(ImageList: ArrayList<String>) {
        binding.apply {
            adapter = ImageViewPagerAdapter(this@ShootingLocationDetails, ImageList)
            rvImages.adapter = adapter
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){

            R.id.rightSide ->
            {
                val currentPosition = binding.rvImages.currentItem
                if (currentPosition < ImageList.size - 1) {
                    binding.rvImages.setCurrentItem(currentPosition + 1, true)
                }
            }

            R.id.leftSide ->{
                val currentPosition = binding.rvImages.currentItem
                if (currentPosition > 0) {
                    binding.rvImages.setCurrentItem(currentPosition - 1, true)
                }
            }

            R.id.llBack ->{
                finish()
            }
        }
    }
}