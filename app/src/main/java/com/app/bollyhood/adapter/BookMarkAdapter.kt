package com.app.bollyhood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.activity.MyBookMarkActivity
import com.app.bollyhood.databinding.AdpBookmarkBinding
import com.app.bollyhood.model.Folder

class BookMarkAdapter(
    val mContext: MyBookMarkActivity,
    val folderList: ArrayList<Folder>,
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
        return folderList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = folderList[position]

        if (!model.folder_name.isNullOrEmpty()) {
            holder.binding.folderName.text = model.folder_name
        }

        holder.binding.llMain.setOnClickListener(View.OnClickListener {
            onclick.onClick(model)
        })
    }

    class MyViewHolder(val binding: AdpBookmarkBinding) : RecyclerView.ViewHolder(binding.root)

    interface onItemClick {
        fun onClick(model:Folder)
    }
}