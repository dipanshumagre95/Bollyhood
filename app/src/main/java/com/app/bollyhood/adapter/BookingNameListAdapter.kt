package com.app.bollyhood.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.databinding.BookingNamelistAdapterBinding

class BookingNameListAdapter(val context: Context,val list:ArrayList<String>,var onItemClickListener:OnItemClickListener) : RecyclerView.Adapter<BookingNameListAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: BookingNamelistAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            if (position==0) {
                binding.itembg.background=context.getDrawable(R.drawable.rectangle_app_background)
                binding.locationName.setTextColor(Color.parseColor("#EF4F6A"))
            }else{
                binding.itembg.background=context.getDrawable(R.drawable.gray_bg_10dp)
                binding.locationName.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            binding.locationName.text=list.get(position)
            binding.root.setOnClickListener(View.OnClickListener {
                onItemClickListener.onItemClick()
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BookingNamelistAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int= list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    interface OnItemClickListener{
        fun onItemClick()
    }
}