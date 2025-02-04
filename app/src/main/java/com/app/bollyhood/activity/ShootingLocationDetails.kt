package com.app.bollyhood.activity

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.bollyhood.R
import com.app.bollyhood.adapter.ImageViewPagerAdapter
import com.app.bollyhood.databinding.ActivityShootingLocationDetailsBinding
import com.app.bollyhood.model.ShootingLocationModels.ShootLocationModel
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide

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

        viewModel.shootLocation.observe(this, Observer {
            if (it.status=="1") {
                setLocationData(it.result)
            } else {
                Toast.makeText(this,it.msg,Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setLocationData(shootLocationModel: ShootLocationModel) {
        if (!shootLocationModel.locationName.isNullOrBlank()){
            binding.tvLocationName.text=shootLocationModel.locationName
        }else{
            binding.tvLocationName.visibility=View.GONE
        }

        if (!shootLocationModel.location.isNullOrBlank()){
            binding.locationCity.text=shootLocationModel.location
        }else{
            binding.locationCity.visibility=View.GONE
        }

        if (!shootLocationModel.rating.isNullOrBlank()){
            binding.tvrating.text=shootLocationModel.rating
        }else{
            binding.tvrating.visibility=View.GONE
        }

        if (!shootLocationModel.amount.isNullOrBlank()){
            binding.tvRupes.text=shootLocationModel.amount
        }else{
            binding.tvRupes.visibility=View.GONE
        }

        if (!shootLocationModel.shiftTime.isNullOrBlank()){
            binding.tvShift.text=shootLocationModel.shiftTime
        }else{
            binding.tvShift.visibility=View.GONE
        }

        if (!shootLocationModel.locationDescription.isNullOrBlank()){
            binding.tvDescription.text=shootLocationModel.locationDescription
        }else{
            binding.tvDescription.visibility=View.GONE
        }

        if (!shootLocationModel.parking.isNullOrBlank()){
            binding.tvParking.text=shootLocationModel.parking
        }else{
            binding.tvParking.visibility=View.GONE
        }

        if (!shootLocationModel.careTaker.isNullOrBlank()){
            binding.llcareTaker.visibility=View.VISIBLE
        }else{
            binding.llcareTaker.visibility=View.GONE
        }

        if (!shootLocationModel.securityAmount.isNullOrBlank()){
            binding.lldeposit.visibility=View.VISIBLE
        }else{
            binding.lldeposit.visibility=View.GONE
        }

        if (!shootLocationModel.acCount.isNullOrBlank()){
            binding.tvAcCount.text=shootLocationModel.acCount
        }else{
            binding.lldeposit.visibility=View.GONE
        }

        if (!shootLocationModel.locationImage.isNullOrEmpty()){
            binding.rvImages.visibility=View.VISIBLE
            setAdapter(shootLocationModel.locationImage)
        }else{
            binding.rvImages.visibility=View.GONE
        }

        if (!shootLocationModel.managerName.isNullOrBlank()){
            binding.tvmanageerName.text=shootLocationModel.managerName
        }else{
            binding.tvmanageerName.visibility=View.GONE
        }

        if (!shootLocationModel.rating.isNullOrBlank()){
            binding.tvrating.text=shootLocationModel.rating
        }else{
            binding.tvrating.visibility=View.GONE
        }

        if (!shootLocationModel.managerProfileName.isNullOrBlank()){
            Glide.with(this)
                .load(shootLocationModel.managerimage)
                .centerCrop()
                .error(R.drawable.ic_profile)
                .into(binding.managerImage)
        }else{
            binding.llhostingProfile.visibility=View.GONE
        }
    }

    private fun addListner() {
        binding.apply {
            contactBtn.setOnClickListener(this@ShootingLocationDetails)
            llBack.setOnClickListener(this@ShootingLocationDetails)
            llBookmark.setOnClickListener(this@ShootingLocationDetails)
            leftSide.setOnClickListener(this@ShootingLocationDetails)
            rightSide.setOnClickListener(this@ShootingLocationDetails)
        }
    }

    private fun initUI() {
        if (intent.hasExtra(StaticData.id)){
            viewModel.getShootLocation(intent.getStringExtra(StaticData.id))
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

            R.id.contactBtn ->{

            }

            R.id.llBookmark ->{

            }
        }
    }
}