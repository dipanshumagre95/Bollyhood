package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.AdpBookmarkListBinding
import com.app.bollyhood.model.BookingModel
import com.bumptech.glide.Glide

class BookMarkListAdapter(val requireContext: Context, val bookList: ArrayList<BookingModel>,
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
        holder.binding.tvName.text = model.name
        if (model.image.isNotEmpty()) {
            Glide.with(requireContext).load(bookList[position].image)
                .into(holder.binding.ivImage)
        }

        if (bookList[position].is_verify == "1") {
            holder.binding.ivBookMark.visibility = View.VISIBLE
        } else {
            holder.binding.ivBookMark.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onclick.onClick(position,bookList[position])
        }


    }

    class MyViewHolder(val binding: AdpBookmarkListBinding) : RecyclerView.ViewHolder(binding.root)

    interface onItemClick{
        fun onClick(position: Int,bookingModel: BookingModel)
    }
}