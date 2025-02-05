package com.app.bollyhood.activity

import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.adapter.DateAdapter
import com.app.bollyhood.adapter.ImageViewPagerAdapter
import com.app.bollyhood.databinding.ActivityShootingLocationDetailsBinding
import com.app.bollyhood.model.DateModel
import com.app.bollyhood.model.ShootingLocationModels.ShootLocationModel
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class ShootingLocationDetails : AppCompatActivity(),OnClickListener{

    lateinit var binding: ActivityShootingLocationDetailsBinding
    private val viewModel: DataViewModel by viewModels()
    private var ImageList= ArrayList<String>()
    lateinit var dialog:Dialog

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
        if (!shootLocationModel.property_name.isNullOrBlank()){
            binding.tvLocationName.text=shootLocationModel.property_name
        }else{
            binding.tvLocationName.visibility=View.GONE
        }

        if (!shootLocationModel.property_location.isNullOrBlank()){
            binding.locationCity.text=shootLocationModel.property_location
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

        if (!shootLocationModel.shift_type.isNullOrBlank()){
            binding.tvShift.text=shootLocationModel.shift_type
        }else{
            binding.tvShift.visibility=View.GONE
        }

        if (!shootLocationModel.description.isNullOrBlank()){
            binding.tvDescription.text=shootLocationModel.description
        }else{
            binding.tvDescription.visibility=View.GONE
        }

        if (!shootLocationModel.parking.isNullOrBlank()){
            binding.tvParking.text=shootLocationModel.parking
        }else{
            binding.tvParking.visibility=View.GONE
        }

        if (!shootLocationModel.care_taker.isNullOrBlank()&&!shootLocationModel.care_taker.equals("Yes")){
            binding.llcareTaker.visibility=View.VISIBLE
        }else{
            binding.llcareTaker.visibility=View.GONE
        }

        if (!shootLocationModel.security_deposit.isNullOrBlank()&&!shootLocationModel.security_deposit.equals("Yes")){
            binding.lldeposit.visibility=View.VISIBLE
        }else{
            binding.lldeposit.visibility=View.GONE
        }

        if (!shootLocationModel.air_conditioner.isNullOrBlank()){
            binding.tvAcCount.text=shootLocationModel.air_conditioner
        }else{
            binding.lldeposit.visibility=View.GONE
        }

        if (!shootLocationModel.images.isNullOrEmpty()){
            binding.rvImages.visibility=View.VISIBLE
            setAdapter(shootLocationModel.images)
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
                createFolderButton()
            }

            R.id.llBookmark ->{

            }
        }
    }

    fun createFolderButton() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.locationbookrequestdialog)

        val edtName = dialog.findViewById<EditText>(R.id.edtName)
        val edtEmail = dialog.findViewById<EditText>(R.id.edtEmail)
        val edtMobileNumber = dialog.findViewById<EditText>(R.id.edtMobileNumber)
        val edtBookingReason = dialog.findViewById<EditText>(R.id.edtBookingreason)
        val edtBookingFrom = dialog.findViewById<TextView>(R.id.edtBookingfrom)
        val edtBookingEnd = dialog.findViewById<TextView>(R.id.edtBookingEnd)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.rv_date)

        edtBookingFrom?.setOnClickListener {
            showTimePickerDialog(edtBookingFrom)
        }
        edtBookingEnd?.setOnClickListener {
            showTimePickerDialog(edtBookingEnd)
        }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val dateList: MutableList<DateModel> = generateDateList(year)

        if (dateList.isEmpty()) {
            Toast.makeText(this, "No dates available", Toast.LENGTH_SHORT).show()
            return
        }

        recyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val dateAdapter = DateAdapter(this, dateList) { selectedPosition ->
            if (selectedPosition in dateList.indices) {
                val fullDate = dateList[selectedPosition].fullDate
                Toast.makeText(this, fullDate, Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView?.adapter = dateAdapter
        recyclerView?.post { dateAdapter.scrollToToday(recyclerView) } // Scroll to today's date

        // Show Dialog
        dialog.show()
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes?.windowAnimations = R.style.BottomSheetDialogTheme
            setGravity(Gravity.BOTTOM)
        }
    }



    private fun showTimePickerDialog(txtSelectedTime: TextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY) // 24-hour format
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            R.style.CustomTimePicker,
            { _, selectedHour, selectedMinute ->
                val amPm = if (selectedHour >= 12) "PM" else "AM"
                val formattedHour = if (selectedHour % 12 == 0) 12 else selectedHour % 12
                val formattedTime = String.format("%02d:%02d %s", formattedHour, selectedMinute, amPm)

                txtSelectedTime.text = formattedTime
            },
            hour, minute, false // `false` ensures 12-hour format
        )
        timePickerDialog.show()
    }


    private fun generateDateList(year: Int): MutableList<DateModel> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, Calendar.JANUARY)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val dateList = mutableListOf<DateModel>()

        val totalDaysInYear = if (calendar.getActualMaximum(Calendar.DAY_OF_YEAR) == 366) 366 else 365

        for (i in 0 until totalDaysInYear) {
            val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
            val month = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
            val fullDate = "$day/$month/${calendar.get(Calendar.YEAR)}"

            dateList.add(DateModel(day, month, calendar.get(Calendar.YEAR).toString(), fullDate))
            calendar.add(Calendar.DAY_OF_YEAR, 1) // Move to next day
        }
        return dateList
    }
}