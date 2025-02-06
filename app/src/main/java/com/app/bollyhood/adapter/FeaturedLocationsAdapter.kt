package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.databinding.FeaturedLocationsAdapterBinding
import com.app.bollyhood.model.ShootingLocationModels.ShootLocationModel
import com.bumptech.glide.Glide

class FeaturedLocationsAdapter(val context: Context, val shootLocationList: ArrayList<ShootLocationModel>, var onItemClickInterface:FeaturedLocationsAdapter.OnFeaturedItemClick) : RecyclerView.Adapter<FeaturedLocationsAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: FeaturedLocationsAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShootLocationModel,context: Context,onItemClickInterface: OnFeaturedItemClick) {
            if (!item.images.isNullOrEmpty()){
                Glide.with(context).load(item.images[0])
                    .error(R.drawable.upload_to_the_cloud_svg)
                    .centerCrop()
                    .into(binding.ivimage)
            }

            if (!item.property_name.isNullOrBlank()) {
                binding.locationName.text = item.property_name
            }

            if (!item.property_location.isNullOrBlank()) {
                binding.locationCity.text = item.property_location
            }

            binding.root.setOnClickListener {
                onItemClickInterface.tandingItemClicked(item.locationId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            FeaturedLocationsAdapterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int =shootLocationList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var item=shootLocationList.get(position)
        holder.bind(item,context,onItemClickInterface)
    }

    interface OnFeaturedItemClick
    {
        fun tandingItemClicked(locationId:String)
    }
}