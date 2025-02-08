package com.app.bollyhood.activity

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.adapter.DateAdapter
import com.app.bollyhood.adapter.YourBookingAdapter
import com.app.bollyhood.databinding.ActivityLocationBookingActivitysBinding
import com.app.bollyhood.model.DateModel
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BookingActivitys : AppCompatActivity(),OnClickListener {

    lateinit var binding: ActivityLocationBookingActivitysBinding
    private val viewModel: DataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_booking_activitys)

        initUi()
        addObserver()
        addListener()
    }

    private fun addListener() {
        binding.apply {
            llBack.setOnClickListener(this@BookingActivitys)
        }
    }

    private fun addObserver() {
        viewModel.dateList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                setDataListAdapter(it)
            }
        })
    }

    private fun initUi() {
        viewModel.generateDateList()
        setBookingListAdapter()
    }

    override fun onClick(view: View?) {
        when(view?.id){

            R.id.llBack ->{
                finish()
            }
        }
    }

    private fun setDataListAdapter(dateList: List<DateModel>)
    {
        if (dateList.isEmpty()) {
            Toast.makeText(this, "No dates available", Toast.LENGTH_SHORT).show()
            return
        }

        binding.rvDate?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val dateAdapter = DateAdapter(this, dateList,false) { selectedPosition ->
            if (selectedPosition in dateList.indices) {
                val fullDate = dateList[selectedPosition].fullDate
                Toast.makeText(this, fullDate, Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvDate?.adapter = dateAdapter
        binding.rvDate?.post { dateAdapter.scrollToToday(binding.rvDate) }
    }

    private fun setBookingListAdapter() {
        binding.apply {
            rvBookingList.layoutManager =
                LinearLayoutManager(this@BookingActivitys, LinearLayoutManager.VERTICAL, false)
            rvBookingList.setHasFixedSize(true)
            yourBookingAdapter = YourBookingAdapter(this@BookingActivitys)
            rvBookingList.adapter = yourBookingAdapter
            yourBookingAdapter?.notifyDataSetChanged()
        }
    }
}