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
import com.app.bollyhood.model.ShootLocationNameModel

class BookingNameListAdapter(val context: Context, val locationNameList:ArrayList<ShootLocationNameModel>, var onItemClickListener:OnItemClickListener) : RecyclerView.Adapter<BookingNameListAdapter.ViewHolder>(){

    var provoiousPosition=0

    inner class ViewHolder(val binding: BookingNamelistAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(shootLocationNameModel:ShootLocationNameModel,position: Int) {
            if (shootLocationNameModel.isSelected=="1") {
                binding.itembg.background=context.getDrawable(R.drawable.rectangle_app_background)
                binding.locationName.setTextColor(Color.parseColor("#EF4F6A"))
            }else{
                binding.itembg.background=context.getDrawable(R.drawable.gray_bg_10dp)
                binding.locationName.setTextColor(ContextCompat.getColor(context, R.color.black))
            }

            binding.locationName.text=shootLocationNameModel.name

            binding.root.setOnClickListener(View.OnClickListener {
                provoiousPosition=position
                locationNameList.get(provoiousPosition).isSelected= "0"
                locationNameList.get(position).isSelected="1"
                onItemClickListener.onNameItemClick(shootLocationNameModel)
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BookingNamelistAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int= locationNameList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(locationNameList.get(position),position)
    }

    interface OnItemClickListener{
        fun onNameItemClick(shootLocationNameModel:ShootLocationNameModel)
    }
}