package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.databinding.BookinglistUsersAdapterBinding
import com.app.bollyhood.model.ShootLocationBookingList
import com.bumptech.glide.Glide

class BookingListUsersAdapter(
    val context: Context,
    val locationBookingList:ArrayList<ShootLocationBookingList>,
    val onItemListener: BookingListUsersAdapter.OnItemListener
): RecyclerView.Adapter<BookingListUsersAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: BookinglistUsersAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(shootLocationBookingList:ShootLocationBookingList) {

            if (!shootLocationBookingList.start_booking_time.isNullOrEmpty()) {
                binding.tvStartTime.text = shootLocationBookingList.start_booking_time.replace(Regex("(?i)\\s*(AM|PM)"), "").trim()
            }

            if (!shootLocationBookingList.name.isNullOrEmpty()){
                binding.tvName.text=shootLocationBookingList.name
            }

            if (!shootLocationBookingList.booking_reason.isNullOrEmpty()){
                binding.bookingReason.text=shootLocationBookingList.booking_reason
            }

                Glide.with(context)
                    .load(shootLocationBookingList.user_image)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(binding.cvProfile)

            if (!shootLocationBookingList.property_name.isNullOrEmpty()){
                binding.userLocation.text=shootLocationBookingList.property_name
            }

            binding.root.setOnClickListener(View.OnClickListener {
                onItemListener.onBookingItemClick(shootLocationBookingList)
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BookinglistUsersAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int= locationBookingList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = locationBookingList.get(position)
        holder.bind(model)
    }

    interface OnItemListener{
        fun onBookingItemClick(shootLocationBookingList:ShootLocationBookingList)
    }
}