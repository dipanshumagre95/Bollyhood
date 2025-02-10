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
import android.widget.ProgressBar
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
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.extensions.isStartTimeBeforeEndTime
import com.app.bollyhood.extensions.isvalidEmailAddress
import com.app.bollyhood.extensions.isvalidField
import com.app.bollyhood.extensions.isvalidMobileNumber
import com.app.bollyhood.extensions.isvalidName
import com.app.bollyhood.model.DateModel
import com.app.bollyhood.model.ShootingLocationModels.ShootLocationModel
import com.app.bollyhood.util.DateUtils.Companion.dateToMilliseconds
import com.app.bollyhood.util.DialogsUtils.showCustomToast
import com.app.bollyhood.util.PrefManager
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
    lateinit var shootLocationModel: ShootLocationModel
    lateinit var dialog:Dialog
    val dateList= ArrayList<DateModel>()

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
                shootLocationModel=it.result
                setLocationData(it.result)
            } else {
                Toast.makeText(this,it.msg,Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.successData.observe(this, Observer {
            if (it.status=="1") {
                dialog.dismiss()
                showCustomToast(this,StaticData.successMsg,it.msg,StaticData.success)
            } else {
                showCustomToast(this,StaticData.pleaseTryAgain,it.msg,StaticData.alert)
            }
        })

        viewModel.dateList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                dateList.addAll(it)
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
            val name=shootLocationModel.shift_type
            binding.tvShift.text="/$name"
        }else{
            binding.tvShift.visibility=View.GONE
        }

        if (!shootLocationModel.description.isNullOrBlank()){
            binding.tvDescription.text=shootLocationModel.description
        }else{
            binding.tvDescription.visibility=View.GONE
        }

        if (!shootLocationModel.parking.isNullOrBlank()&&shootLocationModel.parking.equals("Yes")){
            binding.llparking.visibility=View.VISIBLE
        }else{
            binding.llparking.visibility=View.GONE
        }

        if (!shootLocationModel.care_taker.isNullOrBlank()&&shootLocationModel.care_taker.equals("Yes")){
            binding.llcareTaker.visibility=View.VISIBLE
        }else{
            binding.llcareTaker.visibility=View.GONE
        }

        if (!shootLocationModel.security_deposit.isNullOrBlank()&&shootLocationModel.security_deposit.equals("Yes")){
            binding.lldeposit.visibility=View.VISIBLE
        }else{
            binding.lldeposit.visibility=View.GONE
        }

        if (!shootLocationModel.air_conditioner.isNullOrBlank()&&shootLocationModel.air_conditioner.equals("Yes")){
            binding.llac.visibility=View.VISIBLE
        }else{
            binding.llac.visibility=View.GONE
        }

        if (!shootLocationModel.images.isNullOrEmpty()){
            binding.rvImages.visibility=View.VISIBLE
            ImageList.clear()
            ImageList=shootLocationModel.images
            setAdapter(shootLocationModel.images)
        }else{
            binding.rvImages.visibility=View.GONE
        }

        if (!shootLocationModel.name.isNullOrBlank()){
            binding.tvmanageerName.text=shootLocationModel.name
        }else{
            binding.tvmanageerName.visibility=View.GONE
        }

        if (!shootLocationModel.rating.isNullOrBlank()){
            binding.tvrating.text=shootLocationModel.rating
        }else{
            binding.tvrating.visibility=View.VISIBLE
        }

        if (!shootLocationModel.image.isNullOrBlank()){
            Glide.with(this)
                .load(shootLocationModel.image)
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
        viewModel.generateDateList()
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
                locationBookingDialog()
            }

            R.id.llBookmark ->{

            }
        }
    }

    fun locationBookingDialog() {
        val calendar = Calendar.getInstance()
        var fullDate = SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault()).format(calendar.time)
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.locationbookrequestdialog)

        val edtName = dialog.findViewById<EditText>(R.id.edtName)
        val edtEmail = dialog.findViewById<EditText>(R.id.edtEmail)
        val edtMobileNumber = dialog.findViewById<EditText>(R.id.edtMobileNumber)
        val edtBookingReason = dialog.findViewById<EditText>(R.id.edtBookingreason)
        val edtBookingFrom = dialog.findViewById<TextView>(R.id.edtBookingfrom)
        val edtBookingEnd = dialog.findViewById<TextView>(R.id.edtBookingEnd)
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.rv_date)
        val tvSendOtp = dialog.findViewById<TextView>(R.id.tvSendOtp)
        val loader = dialog.findViewById<ProgressBar>(R.id.pbLoadData)

        edtBookingFrom?.setOnClickListener {
            showTimePickerDialog(edtBookingFrom)
        }

        edtBookingEnd?.setOnClickListener {
            showTimePickerDialog(edtBookingEnd)
        }

        tvSendOtp.setOnClickListener(OnClickListener {
            if (isStartTimeBeforeEndTime(edtBookingFrom.text.toString().trim(), edtBookingEnd.text.toString().trim(), false)) {
                addLocationBooking(
                    shootLocationModel.locationId,
                    edtName.text.toString().trim(),
                    edtEmail.text.toString().trim(),
                    edtMobileNumber.text.toString().trim(),
                    edtBookingReason.text.toString().trim(),
                    fullDate,
                    edtBookingFrom.text.toString().trim(),
                    edtBookingEnd.text.toString().trim(),
                    loader
                )
            }else{
                Toast.makeText(this,"Please Select Start Time Before End Time",Toast.LENGTH_SHORT).show()
            }
        })

        if (dateList.isEmpty()) {
            Toast.makeText(this, "No dates available", Toast.LENGTH_SHORT).show()
            return
        }

        recyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val dateAdapter = DateAdapter(this, dateList,true) { selectedPosition ->
            if (selectedPosition in dateList.indices) {
                fullDate = dateList[selectedPosition].fullDate
            }
        }
        recyclerView?.adapter = dateAdapter
        recyclerView?.post { dateAdapter.scrollToToday(recyclerView) } // Scroll to today's date

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
            hour, minute, false
        )
        timePickerDialog.show()
    }

    private fun addLocationBooking(
        location_id: String,
        name: String,
        email: String,
        mobile: String,
        booking_reason: String,
        booking_date: String,
        start_booking_time: String,
        end_booking_time: String,
        view:View
    ){
        if (isNetworkAvailable(this)) {
            if (isvalidName(
                    this,
                    name
                ) && isvalidEmailAddress(
                    this,
                    email
                ) && isvalidMobileNumber(
                    this,
                    mobile
                ) && isvalidField(this,
                    start_booking_time,
                    "Please Select Booking Start Time"
                )&& isvalidField(this,
                    end_booking_time,
                    "Please Select Booking End Time"
                )
            ) {
                view.visibility=View.VISIBLE
                val dateToMilliseconds = dateToMilliseconds(booking_date, "dd/MMM/yyyy")
                viewModel.addLocationBooking(
                    PrefManager(this).getvalue(StaticData.id).toString(),
                    location_id,
                    name,
                    email,
                    mobile,
                    booking_reason,
                    dateToMilliseconds,
                    start_booking_time,
                    end_booking_time
                )
            }
        } else {
            Toast.makeText(
                this, getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}