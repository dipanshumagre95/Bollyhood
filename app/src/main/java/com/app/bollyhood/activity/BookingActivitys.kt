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
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.BookingModel
import com.app.bollyhood.model.DateModel
import com.app.bollyhood.util.DateUtils.Companion.formatDate
import com.app.bollyhood.util.DateUtils.Companion.getMillisecondsFromDate
import com.app.bollyhood.util.DateUtils.Companion.getTodayDate
import com.app.bollyhood.util.DateUtils.Companion.getTodayMilliseconds
import com.app.bollyhood.util.DialogsUtils.showCustomToast
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
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

        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.locationBookingList.observe(this, Observer {
            if (it.status == "1"&&!it.result.isNullOrEmpty()) {
                binding.noBookingFound.visibility=View.GONE
                binding.rvBookingList.visibility=View.VISIBLE
                setBookingListAdapter(it.result)
            } else {
                binding.noBookingFound.visibility=View.VISIBLE
                binding.rvBookingList.visibility=View.GONE
            }
        })
    }

    private fun initUi() {
        viewModel.generateDateList()
        setDateToUI("")
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
                setDateToUI(fullDate)
                getYourBookingsDetails(getMillisecondsFromDate(fullDate))
            }
        }
        binding.rvDate?.adapter = dateAdapter
        binding.rvDate?.post { dateAdapter.scrollToToday(binding.rvDate) }
    }

    private fun setBookingListAdapter(bookingModelList:ArrayList<BookingModel>) {
        binding.apply {
            rvBookingList.layoutManager =
                LinearLayoutManager(this@BookingActivitys, LinearLayoutManager.VERTICAL, false)
            rvBookingList.setHasFixedSize(true)
            yourBookingAdapter = YourBookingAdapter(this@BookingActivitys,bookingModelList)
            rvBookingList.adapter = yourBookingAdapter
            yourBookingAdapter?.notifyDataSetChanged()
        }
    }

    fun setDateToUI(givenDate:String)
    {
        binding.apply {
            var date=""
            if (givenDate.isNullOrEmpty()){
                date = getTodayDate()
            }else{
                date=givenDate
            }
            tvDate.text= formatDate(date)
        }
    }

    override fun onResume() {
        super.onResume()
        getYourBookingsDetails(getTodayMilliseconds())
    }

    private fun getYourBookingsDetails(date:String)
    {
        if (isNetworkAvailable(this)) {
            viewModel.getYourBookingsDetails(PrefManager(this).getvalue(StaticData.id).toString(),date)
        } else {
            showCustomToast(this,StaticData.networkIssue,getString(R.string.str_error_internet_connections),StaticData.close)
        }
    }
}