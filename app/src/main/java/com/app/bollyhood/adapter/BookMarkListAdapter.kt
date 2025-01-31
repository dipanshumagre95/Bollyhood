package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.databinding.AdpBookmarkListBinding
import com.app.bollyhood.model.BookMarkModel
import com.app.bollyhood.util.DateUtils
import com.bumptech.glide.Glide

class BookMarkListAdapter(val requireContext: Context, val bookList: ArrayList<BookMarkModel>,
                          val onItemclick:onItemClick) :
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

        if (!model.name.isNullOrBlank()) {
            holder.binding.tvName.text = model.name
        }

        if (!model.image.isNullOrBlank()) {
            Glide.with(requireContext).load(model.image)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(holder.binding.ivImage)
        }

        if (!model.bookmark_time.isNullOrBlank()) {
            holder.binding.tvtime.text = DateUtils.getConvertDateTiemFormat(model.bookmark_time)
        }

        holder.binding.llmain.setOnClickListener(View.OnClickListener {
            onItemclick.onClick(model)
        })

    }

    class MyViewHolder(val binding: AdpBookmarkListBinding) : RecyclerView.ViewHolder(binding.root)

    interface onItemClick{
        fun onClick(bookingModel: BookMarkModel)
    }
}