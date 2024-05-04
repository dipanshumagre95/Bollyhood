package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.databinding.AdpWorkBinding
import com.app.bollyhood.model.WorkLinks

class WorkAdapter(
    val mContext: Context, val workLinks: ArrayList<WorkLinks>,
    val onclick: onItemClick
) :
    RecyclerView.Adapter<WorkAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            AdpWorkBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun getItemCount(): Int {
        return workLinks.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = workLinks[position]

        holder.binding.tvName.text = model.worklink_name

        if (position % 2 == 0) {
            holder.binding.rrMain.setBackgroundResource(R.drawable.rectangle_work1)
        } else {
            holder.binding.rrMain.setBackgroundResource(R.drawable.rectangle_work2)
        }

        holder.itemView.setOnClickListener {
            onclick.onClick(position, work = workLinks[position])
        }
    }

    class MyViewHolder(val binding: AdpWorkBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    interface onItemClick {
        fun onClick(pos: Int, work: WorkLinks)
    }
}