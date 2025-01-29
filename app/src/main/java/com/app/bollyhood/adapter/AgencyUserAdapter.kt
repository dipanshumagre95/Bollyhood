package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.AdpAgencyUserBinding
import com.app.bollyhood.model.castinglist.CastingUserData
import com.bumptech.glide.Glide

class AgencyUserAdapter(val requireContext: Context, val list: ArrayList<CastingUserData>,
    val onclick:AgencyUserAdapter.onItemClick) :
    RecyclerView.Adapter<AgencyUserAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return MyViewHolder(
            AdpAgencyUserBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        holder.binding.tvName.text = model.name
        if (model.image.isNotEmpty()) {
            Glide.with(requireContext).load(list[position].image)
                .into(holder.binding.ivImage)
        }

        if (list[position].is_verify == "1") {
            holder.binding.ivBookMark.visibility = View.VISIBLE
        } else {
            holder.binding.ivBookMark.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onclick.onClick(castingUserData = list[position],position)
        }

    }

    class MyViewHolder(val binding: AdpAgencyUserBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    interface onItemClick{
        fun onClick(castingUserData: CastingUserData,pos:Int)
    }

}