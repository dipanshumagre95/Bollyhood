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
import com.app.bollyhood.adapter.BookingListUsersAdapter
import com.app.bollyhood.adapter.BookingNameListAdapter
import com.app.bollyhood.adapter.DateAdapter
import com.app.bollyhood.databinding.ActivityBookingDetailBinding
import com.app.bollyhood.model.DateModel
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class BookingDetailActivity : AppCompatActivity(),OnClickListener,
    BookingNameListAdapter.OnItemClickListener {

    lateinit var binding: ActivityBookingDetailBinding
    lateinit var mContext: BookingDetailActivity
    private val viewModel: DataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking_detail)
        mContext = this
        initUI()
        addListner()
        addObserevs()
    }

    private fun initUI() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        viewModel.generateDateList(year)
        val list= arrayListOf("All","Dipanshu","Ram","Manav")
        setNameListAdapter(list)
        setBookingListAdapter()
    }

    private fun addListner() {
        binding.apply {
            llBack.setOnClickListener(this@BookingDetailActivity)
        }
    }

    private fun addObserevs() {
        viewModel.dateList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                setDataListAdapter(it)
            }
        })
    }

    private fun setNameListAdapter(list:ArrayList<String>) {
        binding.apply {
            rvlocationList.layoutManager =
                LinearLayoutManager(this@BookingDetailActivity, LinearLayoutManager.HORIZONTAL, false)
            rvlocationList.setHasFixedSize(true)
            shootingLocationNameList = BookingNameListAdapter(this@BookingDetailActivity,list,this@BookingDetailActivity)
            rvlocationList.adapter = shootingLocationNameList
            shootingLocationNameList?.notifyDataSetChanged()
        }
    }

    private fun setBookingListAdapter() {
        binding.apply {
            rvBookingList.layoutManager =
                LinearLayoutManager(this@BookingDetailActivity, LinearLayoutManager.VERTICAL, false)
            rvBookingList.setHasFixedSize(true)
            shootingLocationUserList = BookingListUsersAdapter(this@BookingDetailActivity)
            rvBookingList.adapter = shootingLocationUserList
            shootingLocationUserList?.notifyDataSetChanged()
        }
    }

    private fun setDataListAdapter(dateList: List<DateModel>)
    {
        if (dateList.isEmpty()) {
            Toast.makeText(this, "No dates available", Toast.LENGTH_SHORT).show()
            return
        }

        binding.rvDate?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val dateAdapter = DateAdapter(this, dateList) { selectedPosition ->
            if (selectedPosition in dateList.indices) {
                val fullDate = dateList[selectedPosition].fullDate
                Toast.makeText(this, fullDate, Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvDate?.adapter = dateAdapter
        binding.rvDate?.post { dateAdapter.scrollToToday(binding.rvDate) }
        binding.apply {

        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.llBack ->{
                finish()
            }
        }
    }


    override fun onItemClick() {
        TODO("Not yet implemented")
    }
}