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
import com.app.bollyhood.extensions.isNetworkAvailable
import com.app.bollyhood.model.DateModel
import com.app.bollyhood.model.ShootLocationBookingList
import com.app.bollyhood.model.ShootLocationNameModel
import com.app.bollyhood.util.DateUtils.Companion.formatDate
import com.app.bollyhood.util.DateUtils.Companion.getDateFromMilliseconds
import com.app.bollyhood.util.DateUtils.Companion.getMillisecondsFromDate
import com.app.bollyhood.util.DateUtils.Companion.getTodayDate
import com.app.bollyhood.util.DateUtils.Companion.getTodayMilliseconds
import com.app.bollyhood.util.DialogsUtils.showCustomToast
import com.app.bollyhood.util.PrefManager
import com.app.bollyhood.util.StaticData
import com.app.bollyhood.viewmodel.DataViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView

@AndroidEntryPoint
class ShootingBookingListFragment : Fragment(), OnClickListener,
    BookingNameListAdapter.OnItemClickListener,BookingListUsersAdapter.OnItemListener {

    private lateinit var binding: FragmentShootingBookingListBinding
    private val viewModel: DataViewModel by viewModels()
    private var locationList: ArrayList<ShootLocationBookingList> = arrayListOf()
    private var locationFilterdList: ArrayList<ShootLocationBookingList> = arrayListOf()
    private var locationNameList: ArrayList<ShootLocationNameModel> = arrayListOf()
    lateinit var dialog:Dialog

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
        viewModel.generateDateList()
        setDateToUI("")
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

        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.successData.observe(requireActivity(), Observer {
            if (it.status=="1")
            {
                dialog.dismiss()
                showCustomToast(requireContext(),StaticData.successMsg,it.msg,StaticData.success)
            } else {
                showCustomToast(requireContext(),StaticData.pleaseTryAgain,it.msg,StaticData.alert)
            }
        })

        viewModel.locationBookingData.observe(requireActivity(), Observer {
            if (it.status == "1") {
                if (!it.result.location_booking_list.isNullOrEmpty()){
                    locationList.clear()
                    locationNameList.clear()
                    locationNameList.add(0,ShootLocationNameModel("0","All","1"))
                    locationList.addAll(it.result.location_booking_list)
                    locationNameList.addAll(it.result.location_name_list)
                    binding.noBookingFound.visibility=View.GONE
                    binding.rvBookingList.visibility=View.VISIBLE
                    binding.rvlocationList.visibility=View.VISIBLE
                    setBookingListAdapter(locationList)
                    setNameListAdapter()
                }else{
                    binding.noBookingFound.visibility=View.VISIBLE
                    binding.rvBookingList.visibility=View.GONE
                    binding.rvlocationList.visibility=View.GONE
                }
            }else{
                binding.noBookingFound.visibility=View.VISIBLE
                binding.rvBookingList.visibility=View.GONE
                binding.rvlocationList.visibility=View.GONE
            }
        })
    }

    private fun setNameListAdapter() {
        binding.apply {
            rvlocationList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvlocationList.setHasFixedSize(true)
            shootingLocationNameList = BookingNameListAdapter(requireContext(),locationNameList,this@ShootingBookingListFragment)
            rvlocationList.adapter = shootingLocationNameList
            shootingLocationNameList?.notifyDataSetChanged()
        }
    }

    private fun setBookingListAdapter(locationBookingList:ArrayList<ShootLocationBookingList>) {
        binding.apply {
            rvBookingList.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvBookingList.setHasFixedSize(true)
            shootingLocationUserList = BookingListUsersAdapter(requireContext(),locationBookingList,this@ShootingBookingListFragment)
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
        val dateAdapter = DateAdapter(requireContext(), dateList,false) { selectedPosition ->
            if (selectedPosition in dateList.indices) {
                val fullDate = dateList[selectedPosition].fullDate
                setDateToUI(fullDate)
                getLocationBookingList(getMillisecondsFromDate(fullDate))
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

    override fun onNameItemClick(shootLocationNameModel:ShootLocationNameModel) {
        locationFilterdList.clear()
        locationFilterdList.addAll(locationList.filter { it.location_id == shootLocationNameModel.locationId })
        if (locationFilterdList.isEmpty()){
            setBookingListAdapter(locationList)
        }else {
            setBookingListAdapter(locationFilterdList)
        }
    }

    override fun onResume() {
        super.onResume()
        getLocationBookingList(getTodayMilliseconds())
        (requireActivity() as MainActivity).showToolbar(false)
    }

    fun locationBookingConfrimDialog(shootLocationBookingList:ShootLocationBookingList) {
        dialog = Dialog(requireContext())
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
        val view = dialog.findViewById<View>(R.id.view)

        if (!shootLocationBookingList.name.isNullOrEmpty()){
            tvname.text=shootLocationBookingList.name
        }

        if (!shootLocationBookingList.mobile.isNullOrEmpty()){
            val mobile = shootLocationBookingList.mobile
            tvPhone.text="+91-$mobile"
        }

        if (!shootLocationBookingList.email.isNullOrEmpty()){
            tvEmail.text=shootLocationBookingList.email
        }

        if (!shootLocationBookingList.booking_date.isNullOrEmpty()){
            val formatDate =
                formatDate(getDateFromMilliseconds(shootLocationBookingList.booking_date))
            tvdate.text=formatDate
        }

        if (!shootLocationBookingList.start_booking_time.isNullOrEmpty()){
            val start = shootLocationBookingList.start_booking_time
            val end = shootLocationBookingList.end_booking_time
            tvTime.text="$start to $end"
        }

        if (!shootLocationBookingList.booking_reason.isNullOrEmpty()){
            tvDescription.text=shootLocationBookingList.booking_reason
        }

        Glide.with(requireContext())
            .load(shootLocationBookingList.user_image)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .into(userImage)

        view.setOnClickListener(OnClickListener {
            dialog.dismiss()
        })

        rejectBtn.setOnClickListener(OnClickListener {
            setBookingConfirmation("1",shootLocationBookingList.location_id,shootLocationBookingList.uid,shootLocationBookingList.id)
        })

        acceptBtn.setOnClickListener(OnClickListener {
            setBookingConfirmation("2",shootLocationBookingList.location_id,shootLocationBookingList.uid,shootLocationBookingList.id)
        })

        dialog.show()
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes?.windowAnimations = R.style.BottomSheetDialogTheme
            setGravity(Gravity.BOTTOM)
        }
    }

    private fun getLocationBookingList(date:String)
    {
        if (isNetworkAvailable(requireContext())) {
            viewModel.getLocationBookingData(PrefManager(requireContext()).getvalue(StaticData.id).toString(),date)
        } else {
            showCustomToast(requireContext(),StaticData.networkIssue,getString(R.string.str_error_internet_connections),StaticData.close)
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
            tvdate.text= formatDate(date)
        }
    }

    override fun onBookingItemClick(shootLocationBookingList:ShootLocationBookingList) {
        locationBookingConfrimDialog(shootLocationBookingList)
    }

    private fun setBookingConfirmation(status:String,location_id:String,uid:String,booking_id:String)
    {
        if (isNetworkAvailable(requireContext())) {
            viewModel.setLocationBookingConfirmation(status,location_id,uid,booking_id)
        } else {
            showCustomToast(requireContext(),StaticData.networkIssue,getString(R.string.str_error_internet_connections),StaticData.close)
        }
    }
}