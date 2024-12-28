package com.app.bollyhood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.LocationListAdapterBinding

class LocationList_Adapter : RecyclerView.Adapter<LocationList_Adapter.MyViewHolder>() {

    class MyViewHolder(binding: LocationListAdapterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LocationListAdapterBinding.inflate(
            LayoutInflater.from(parent.context)
            ,parent,false))
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    }
}