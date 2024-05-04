package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.AdpExpertiseBinding
import com.app.bollyhood.model.ExpertiseModel
import com.bumptech.glide.Glide



class ExpertiseAdapter(
    val requireContext: Context,
    val expertiseList: ArrayList<ExpertiseModel>,
    val onClick: onItemClick
) :
    RecyclerView.Adapter<ExpertiseAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MyViewHolder {
        return MyViewHolder(
            AdpExpertiseBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return expertiseList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.model = expertiseList[position]

        if (expertiseList[position].image.isNotEmpty()) {
            Glide.with(requireContext).load(expertiseList[position].image)
                .into(holder.binding.ivImage)

        }

        if (expertiseList[position].is_verify == "1") {
            holder.binding.ivBookMark.visibility = View.VISIBLE
        } else {
            holder.binding.ivBookMark.visibility = View.GONE
        }

        val stringList = arrayListOf<String>()

        for (i in 0 until expertiseList[position].categories.size) {
            stringList.add(expertiseList[position].categories[i].category_name)
        }

        holder.binding.tvCategory.text = stringList.joinToString(separator = "/")

        holder.itemView.setOnClickListener {
            onClick.onClick(position, expertiseModel = expertiseList[position])
        }
    }

    class MyViewHolder(val binding: AdpExpertiseBinding) : RecyclerView.ViewHolder(binding.root)

    interface onItemClick {
        fun onClick(pos: Int, expertiseModel: ExpertiseModel)
    }
}