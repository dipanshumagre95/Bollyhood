package com.app.bollyhood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.LocationEditListAdapterBinding
import com.app.bollyhood.databinding.LocationListAdapterBinding

class LocationListAdapter(private val isEdit: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    override fun getItemCount(): Int = 5

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    class PrimaryViewHolder(private val binding: LocationListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /*fun bind(item: LocationItem) {
            binding.textView.text = item.name
        }*/
    }

    class SecondaryViewHolder(private val binding: LocationEditListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
       /* fun bind(item: LocationItem) {
            binding.anotherTextView.text = item.name
        }*/
    }
}
