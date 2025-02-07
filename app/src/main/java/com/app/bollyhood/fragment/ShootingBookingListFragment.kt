package com.app.bollyhood.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bollyhood.R
import com.app.bollyhood.activity.MainActivity
import com.app.bollyhood.adapter.BookingListUsersAdapter
import com.app.bollyhood.adapter.BookingNameListAdapter
import com.app.bollyhood.adapter.DateAdapter
import com.app.bollyhood.databinding.FragmentShootingBookingListBinding
import com.app.bollyhood.model.DateModel
import com.app.bollyhood.viewmodel.DataViewModel
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar

@AndroidEntryPoint
class ShootingBookingListFragment : Fragment(), OnClickListener,
    BookingNameListAdapter.OnItemClickListener,BookingListUsersAdapter.OnItemListener {

    private lateinit var binding: FragmentShootingBookingListBinding
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
            shootingLocationUserList = BookingListUsersAdapter(requireContext(),this@ShootingBookingListFragment)
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

    }

    fun locationBookingConfrimDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.location_booking_details_formanager)

        val userImage = dialog.findViewById<CircleImageView>(R.id.ivImage)
        val tvname = dialog.findViewById<TextView>(R.id.tvname)
        val tvPhone = dialog.findViewById<TextView>(R.id.tvPhone)
        val tvEmail = dialog.findViewById<TextView>(R.id.tvEmail)
        val llkyc = dialog.findViewById<RelativeLayout>(R.id.llkyc)
        val profileCreatedDate = dialog.findViewById<TextView>(R.id.profileCreatedDate)
        val tvdate = dialog.findViewById<TextView>(R.id.tvdate)
        val tvTime = dialog.findViewById<TextView>(R.id.tvTime)
        val tvDescription = dialog.findViewById<TextView>(R.id.tvDescription)
        val acceptBtn = dialog.findViewById<RelativeLayout>(R.id.acceptBtn)
        val rejectBtn = dialog.findViewById<RelativeLayout>(R.id.rejectBtn)



        dialog.show()
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes?.windowAnimations = R.style.BottomSheetDialogTheme
            setGravity(Gravity.BOTTOM)
        }
    }

    override fun onClick() {
        locationBookingConfrimDialog()
    }

}