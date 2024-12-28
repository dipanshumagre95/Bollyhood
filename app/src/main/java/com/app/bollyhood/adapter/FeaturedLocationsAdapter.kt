package com.app.bollyhood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.FeaturedLocationsAdapterBinding

class FeaturedLocationsAdapter : RecyclerView.Adapter<FeaturedLocationsAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: FeaturedLocationsAdapterBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            FeaturedLocationsAdapterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

    }
}