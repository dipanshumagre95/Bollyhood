package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.AdpChatitemBinding
import com.app.bollyhood.databinding.AdpExpertiseBinding
import com.app.bollyhood.model.ExpertiseModel
import com.bumptech.glide.Glide

class ChatAdapter(val requireContext: Context, val chatList: ArrayList<ExpertiseModel>,
    val onclick:onItemClick) :
    RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            AdpChatitemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = chatList[position]

        holder.binding.tvName.text = model.name

        if (!model.categories.isNullOrEmpty()){
            holder.binding.tvCategory.text = model.categories[0].category_name
        }


        if (!model.last_message.isEmpty()){
            holder.binding.tvLastMessage.visibility=View.VISIBLE
            holder.binding.tvLastMessage.text = model.last_message
        }else{
            holder.binding.tvLastMessage.visibility=View.GONE
        }


        if (model.image.isNotEmpty()) {
            Glide.with(requireContext).load(chatList[position].image)
                .into(holder.binding.ivImage)
        }

        if (chatList[position].is_verify == "1") {
            holder.binding.ivBookMark.visibility = View.VISIBLE
        } else {
            holder.binding.ivBookMark.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onclick.onClick(position, expertiseModel = chatList[position])
        }

    }

    class MyViewHolder(val binding: AdpChatitemBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {

    }

    interface onItemClick{
        fun onClick(pos:Int,expertiseModel: ExpertiseModel)
    }
}