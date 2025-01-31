package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.databinding.AdpBookmarkListBinding
import com.app.bollyhood.model.BookMarkModel
import com.bumptech.glide.Glide

class BookMarkListAdapter(val requireContext: Context, val bookList: ArrayList<BookMarkModel>,
                          val onclick:onItemClick) :
    RecyclerView.Adapter<BookMarkListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            AdpBookmarkListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = bookList[position]
        model.let {
            holder.binding.tvName.text=it.name
            Glide.with(requireContext).load(it.image)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(holder.binding.ivImage)
        }
    }

    class MyViewHolder(val binding: AdpBookmarkListBinding) : RecyclerView.ViewHolder(binding.root)

    interface onItemClick{
        fun onClick(position: Int,bookingModel: BookMarkModel)
    }
}