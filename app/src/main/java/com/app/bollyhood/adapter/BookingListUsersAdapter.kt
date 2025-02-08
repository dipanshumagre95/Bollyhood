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

            if (!shootLocationBookingList.start_booking_time.isNotEmpty()) {
                binding.tvStartTime.text = shootLocationBookingList.start_booking_time
            }

            if (!shootLocationBookingList.name.isNotEmpty()){
                binding.tvName.text=shootLocationBookingList.name
            }

            if (!shootLocationBookingList.booking_reason.isNotEmpty()){
                binding.bookingReason.text=shootLocationBookingList.booking_reason
            }

            if (!shootLocationBookingList.image.isNotEmpty()){
                Glide.with(context)
                    .load(shootLocationBookingList.image)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(binding.cvProfile)
            }

            if (!shootLocationBookingList.locationName.isNotEmpty()){
                binding.userLocation.text=shootLocationBookingList.locationName
            }

            binding.root.setOnClickListener(View.OnClickListener {
                onItemListener.onBookingItemClick()
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
        fun onBookingItemClick()
    }
}