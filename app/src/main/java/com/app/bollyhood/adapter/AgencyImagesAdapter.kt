package com.app.bollyhood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.activity.AgencyDetailsActivity
import com.app.bollyhood.databinding.AdpAgencyImageBinding
import com.bumptech.glide.Glide

class AgencyImagesAdapter(val mContext: AgencyDetailsActivity, val images: ArrayList<String>) :
    RecyclerView.Adapter<AgencyImagesAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            AdpAgencyImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(mContext).load(images[position]).into(holder.binding.ivImages)
    }

    class MyViewHolder(val binding: AdpAgencyImageBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {

    }
}