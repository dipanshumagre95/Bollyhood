package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.ViewpagerImageAdpBinding
import com.bumptech.glide.Glide

class ImageViewPagerAdapter(val context: Context, val list: ArrayList<String>):
    RecyclerView.Adapter<ImageViewPagerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ViewpagerImageAdpBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ImageViewPagerAdapter.MyViewHolder, position: Int) {
        val image = list[position]

        if (!image.isNullOrBlank()) {
            Glide.with(context)
                .load(image)
                .centerCrop()
                .into(holder.binding.ivImage)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(val binding: ViewpagerImageAdpBinding) : RecyclerView.ViewHolder(binding.root) {

    }

}