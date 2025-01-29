package com.app.bollyhood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.activity.MyBookMarkActivity
import com.app.bollyhood.databinding.AdpBookmarkBinding
import com.app.bollyhood.model.BookMarkModel

class BookMarkAdapter(
    val mContext: MyBookMarkActivity,
    val bookMarkList: ArrayList<BookMarkModel>,
    val onclick: onItemClick
) : RecyclerView.Adapter<BookMarkAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return MyViewHolder(
            AdpBookmarkBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun getItemCount(): Int {
        return bookMarkList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = bookMarkList[position]
    }

    class MyViewHolder(val binding: AdpBookmarkBinding) : RecyclerView.ViewHolder(binding.root)

    interface onItemClick {
        fun onClick(position: Int, model: BookMarkModel)
    }
}