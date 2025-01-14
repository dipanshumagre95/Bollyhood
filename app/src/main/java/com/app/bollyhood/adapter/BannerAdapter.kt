package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.AdpBannerlistBinding
import com.app.bollyhood.model.BannerModel
import com.bumptech.glide.Glide

class BannerAdapter(val requireContext: Context, val bannerList: ArrayList<BannerModel>) :
    RecyclerView.Adapter<BannerAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            AdpBannerlistBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun getItemCount(): Int {
        return bannerList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = bannerList[position]
        Glide.with(requireContext)
            .load(model.banner_image)
            .centerCrop()
            .into(holder.binding.ivImage)
    }

    class MyViewHolder(val binding: AdpBannerlistBinding) : RecyclerView.ViewHolder(binding.root) {

    }

}