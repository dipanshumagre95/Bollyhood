package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.databinding.AdapterActiveChatUserBinding
import com.app.bollyhood.model.ExpertiseModel
import com.bumptech.glide.Glide

class ActiveChatUserAdapter(val context :Context, var chatList: ArrayList<ExpertiseModel>,var onclick:ActiveUserOnClick):
    RecyclerView.Adapter<ActiveChatUserAdapter.MyViewHolder>() {


    init {
        chatList = chatList.filter { it.is_online == "1" } as ArrayList<ExpertiseModel>
    }
    class MyViewHolder(val binding: AdapterActiveChatUserBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return ActiveChatUserAdapter.MyViewHolder(
            AdapterActiveChatUserBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model=chatList[position]

            holder.binding.tvName.text = model.name

            Glide.with(context).load(model.image)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(holder.binding.ivImage)


        holder.itemView.setOnClickListener {
            onclick.activeChatItemClick(position, expertiseModel = chatList[position])
        }

    }


    interface ActiveUserOnClick
    {
        fun activeChatItemClick(pos:Int,expertiseModel: ExpertiseModel)
    }
}