package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.AdapterActorsWorklinkBinding
import com.app.bollyhood.model.WorkLinkProfileData

class ActorsProfileWorkLinkAda(val mContext: Context, val workLinks: ArrayList<WorkLinkProfileData>,val onclick: ActorsProfileWorkLinkAda.onItemClick) :
    RecyclerView.Adapter<ActorsProfileWorkLinkAda.MyViewHolder>(){


    class MyViewHolder(val binding: AdapterActorsWorklinkBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorsProfileWorkLinkAda.MyViewHolder {
        return ActorsProfileWorkLinkAda.MyViewHolder(
            AdapterActorsWorklinkBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun getItemCount(): Int {
        return workLinks.size
    }

    override fun onBindViewHolder(holder: ActorsProfileWorkLinkAda.MyViewHolder, position: Int) {
        val model = workLinks[position]

        holder.binding.tvName.text = model.worklink_name

        holder.itemView.setOnClickListener {
            onclick.onitemClick(position, work = workLinks[position])
        }
    }

    interface onItemClick {
        fun onitemClick(pos: Int, work: WorkLinkProfileData)
    }
}