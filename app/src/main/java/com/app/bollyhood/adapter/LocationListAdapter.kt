package com.app.bollyhood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.LocationEditListAdapterBinding
import com.app.bollyhood.databinding.LocationListAdapterBinding

class LocationListAdapter(private val isEdit: Boolean,private var onItemClickInterface:LocationListAdapter.onItemClick) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                PrimaryViewHolder(binding,onItemClickInterface)
            }

            VIEW_TYPE_SECONDARY -> {
                val binding = LocationEditListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SecondaryViewHolder(binding,onItemClickInterface)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = 5

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    class PrimaryViewHolder(private val binding: LocationListAdapterBinding,
                            onItemClickInterface: onItemClick) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClickInterface.editClicked()
            }
        }
    }

    class SecondaryViewHolder(
        private val binding: LocationEditListAdapterBinding,
        onItemClickInterface: onItemClick
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.icEdit.setOnClickListener {
                onItemClickInterface.editClicked()
            }

            binding.root.setOnClickListener {
                onItemClickInterface.editClicked()
            }
        }
    }


    interface onItemClick
    {
        fun itemClicked()
        fun editClicked()
    }
}
