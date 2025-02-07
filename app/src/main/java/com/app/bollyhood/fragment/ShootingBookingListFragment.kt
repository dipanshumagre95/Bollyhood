package com.app.bollyhood.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.activity.BookingDetailActivity
import com.app.bollyhood.activity.MainActivity
import com.app.bollyhood.adapter.BookingListUsersAdapter
import com.app.bollyhood.adapter.BookingNameListAdapter
import com.app.bollyhood.adapter.DateAdapter
import com.app.bollyhood.databinding.FragmentShootingBookingListBinding
import com.app.bollyhood.model.DateModel
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class ShootingBookingListFragment : Fragment(), OnClickListener,
    BookingNameListAdapter.OnItemClickListener  {

    private lateinit var binding: FragmentShootingBookingListBinding
    lateinit var mContext: BookingDetailActivity
    private val viewModel: DataViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(layoutInflater,R.layout.fragment_shooting_booking_list, container, false)

        initUI()
        addListner()
        addObserevs()
        return binding.root
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
            llBack.setOnClickListener(this@ShootingBookingListFragment)
        }
    }

    private fun addObserevs() {
        viewModel.dateList.observe(requireActivity(), Observer {
            if (!it.isNullOrEmpty()) {
                setDataListAdapter(it)
            }
        })
    }

    private fun setNameListAdapter(list:ArrayList<String>) {
        binding.apply {
            rvlocationList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvlocationList.setHasFixedSize(true)
            shootingLocationNameList = BookingNameListAdapter(requireContext(),list,this@ShootingBookingListFragment)
            rvlocationList.adapter = shootingLocationNameList
            shootingLocationNameList?.notifyDataSetChanged()
        }
    }

    private fun setBookingListAdapter() {
        binding.apply {
            rvBookingList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvBookingList.setHasFixedSize(true)
            shootingLocationUserList = BookingListUsersAdapter(requireContext())
            rvBookingList.adapter = shootingLocationUserList
            shootingLocationUserList?.notifyDataSetChanged()
        }
    }

    private fun setDataListAdapter(dateList: List<DateModel>)
    {
        if (dateList.isEmpty()) {
            Toast.makeText(requireContext(), "No dates available", Toast.LENGTH_SHORT).show()
            return
        }

        binding.rvDate?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val dateAdapter = DateAdapter(requireContext(), dateList) { selectedPosition ->
            if (selectedPosition in dateList.indices) {
                val fullDate = dateList[selectedPosition].fullDate
                Toast.makeText(requireContext(), fullDate, Toast.LENGTH_SHORT).show()
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
                (requireActivity() as MainActivity).onBackPressed()
            }
        }
    }


    override fun onItemClick() {
        TODO("Not yet implemented")
    }

}