package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.AdpImageItemBinding
import com.app.bollyhood.model.PhotoModel
import com.bumptech.glide.Glide


class ImagesAdapter(
    val context: Context,
    val imageList: ArrayList<PhotoModel>,
    val onclick: onItemClick
) : RecyclerView.Adapter<ImagesAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            AdpImageItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (imageList[position].id == 0) {
            Glide.with(context).load(imageList[position].url)
                .into(holder.binding.ivImage)
        } else {
            Glide.with(context).load(imageList[position].url)
                .into(holder.binding.ivImage)
        }

        holder.itemView.setOnClickListener(View.OnClickListener {
            onclick.onRemoveImage(position,imageList[position])
        })
    }

    class MyViewHolder(val binding: AdpImageItemBinding) : RecyclerView.ViewHolder(binding.root)

    interface onItemClick {
        fun onRemoveImage(pos: Int, photoModel: PhotoModel)
    }
}