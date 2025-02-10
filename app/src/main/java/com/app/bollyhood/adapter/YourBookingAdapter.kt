package com.app.bollyhood.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.databinding.YourBookingAdapterBinding
import com.app.bollyhood.model.BookingModel
import com.app.bollyhood.util.DateUtils.Companion.formatDate
import com.app.bollyhood.util.DateUtils.Companion.getDateFromMilliseconds
import com.bumptech.glide.Glide

class YourBookingAdapter(val context: Context,val bookingModelList:ArrayList<BookingModel>): RecyclerView.Adapter<YourBookingAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: YourBookingAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            var isDwonArrow=true
            var bookingModel=bookingModelList.get(position)

            when (bookingModel.status){
                "0" -> {
                    binding.apply {
                        statusBg.setBackgroundColor(Color.parseColor("#FFCC80"))
                        bookingStatus.setTextColor(ContextCompat.getColor(context, R.color.black))
                        bookingStatus.text="Booking Pending"
                        tvTextDetails.visibility= View.GONE
                        downArrow.visibility=View.GONE
                        managerDetails.visibility=View.GONE
                    }
                }

                "1" -> {
                    binding.apply {
                        statusBg.setBackgroundColor(Color.parseColor("#329932"))
                        bookingStatus.setTextColor(ContextCompat.getColor(context, R.color.white))
                        bookingStatus.text="Booking Accepted"
                        tvTextDetails.visibility= View.VISIBLE
                        downArrow.visibility=View.VISIBLE
                        managerDetails.visibility=View.GONE
                    }
                }

                "2" ->{
                    binding.apply {
                        statusBg.setBackgroundColor(Color.parseColor("#ff3535"))
                        bookingStatus.setTextColor(ContextCompat.getColor(context, R.color.white))
                        bookingStatus.text="Booking Cancalled"
                        tvTextDetails.visibility= View.GONE
                        downArrow.visibility=View.GONE
                        managerDetails.visibility=View.GONE
                    }
                }
            }
            binding.llarrow.setOnClickListener(View.OnClickListener {
                if(isDwonArrow){
                    binding.downArrow.setBackgroundResource(R.drawable.up_arrow_icon)
                    binding.managerDetails.visibility=View.VISIBLE
                    isDwonArrow=false
                }else{
                    isDwonArrow=true
                    binding.downArrow.setBackgroundResource(R.drawable.down_arrow_icon)
                    binding.managerDetails.visibility=View.GONE
                }
            })

            if (!bookingModel.property_name.isNullOrEmpty()){
                binding.tvname.text=bookingModel.property_name
            }

            if (!bookingModel.property_location.isNullOrEmpty()){
                binding.location.text=bookingModel.property_location
            }

            if (!bookingModel.booking_date.isNullOrEmpty()){
                val formatDate =
                    formatDate(getDateFromMilliseconds(bookingModel.booking_date))
                binding.tvdate.text=formatDate
            }

            if (!bookingModel.start_booking_time.isNullOrEmpty()){
                val start = bookingModel.start_booking_time
                val end = bookingModel.end_booking_time
                binding.tvTime.text="$start to $end"
            }

            if (!bookingModel.name.isNullOrEmpty()){
                binding.tvManagername.text=bookingModel.name
            }

            if (!bookingModel.phone.isNullOrEmpty()){
                val mobile = bookingModel.phone
                binding.tvPhone.text="+91 $mobile"
            }

            if (!bookingModel.email.isNullOrEmpty()){
                binding.tvEmail.text=bookingModel.email
            }

            Glide.with(context)
                .load(bookingModel.image)
                .centerCrop()
                .error(R.drawable.ic_profile)
                .into(binding.ivImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = YourBookingAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int =3

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}