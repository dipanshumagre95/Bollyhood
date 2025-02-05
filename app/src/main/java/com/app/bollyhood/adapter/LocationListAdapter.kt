package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.databinding.LocationEditListAdapterBinding
import com.app.bollyhood.databinding.LocationListAdapterBinding
import com.app.bollyhood.model.ShootingLocationModels.ShootLocationModel
import com.bumptech.glide.Glide

class LocationListAdapter(val context: Context,val isEdit: Boolean,val shootLocationList: ArrayList<ShootLocationModel>, private var onItemClickInterface:LocationListAdapter.onItemClick) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PRIMARY = 1
        private const val VIEW_TYPE_SECONDARY = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (isEdit) VIEW_TYPE_PRIMARY else VIEW_TYPE_SECONDARY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_PRIMARY -> {
                val binding = LocationListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PrimaryViewHolder(binding)
            }

            VIEW_TYPE_SECONDARY -> {
                val binding = LocationEditListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SecondaryViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = shootLocationList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = shootLocationList[position]
        when (holder) {
            is PrimaryViewHolder -> holder.bind(item,context,onItemClickInterface)
            is SecondaryViewHolder -> holder.bind(item,context,onItemClickInterface)
        }
    }

    class PrimaryViewHolder(private val binding: LocationListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ShootLocationModel,context: Context,onItemClickInterface: onItemClick) {
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
                onItemClickInterface.editClicked(item)
            }
        }
    }

    class SecondaryViewHolder(
        private val binding: LocationEditListAdapterBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
        }

        fun bind(item: ShootLocationModel,context: Context,onItemClickInterface: onItemClick) {
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

            binding.icEdit.setOnClickListener {
                onItemClickInterface.editClicked(item)
            }

            binding.root.setOnClickListener {
                onItemClickInterface.itemClicked(item.id)
            }
        }
    }


    interface onItemClick
    {
        fun itemClicked(locationId:String)
        fun editClicked(shootLocationModel: ShootLocationModel)
    }
}
