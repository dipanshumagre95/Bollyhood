package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.AdapterCastinglistedBinding
import com.app.bollyhood.model.UserModel
import com.app.bollyhood.util.DateUtils
import com.bumptech.glide.Glide

class CastingCallListAdapter(val context: Context,val applyedUserList:ArrayList<UserModel>,val onClickItem: onClickItems): RecyclerView.Adapter<CastingCallListAdapter.MyViewHolder>() {

    class MyViewHolder(val bindfing:AdapterCastinglistedBinding) :RecyclerView.ViewHolder(bindfing.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return CastingCallListAdapter.MyViewHolder(
            AdapterCastinglistedBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return applyedUserList.size
    }

    override fun onBindViewHolder(holder: CastingCallListAdapter.MyViewHolder, position: Int) {
        val userModel=applyedUserList[position]

        if (userModel.name.isNotEmpty() == true){
            holder.bindfing.tvName.text=userModel.name
        }

        if (userModel.image.isNotEmpty() == true){
            Glide.with(context).load(userModel.image)
                .into(holder.bindfing.ivImage)
        }

        if (userModel.apply_create_date.isNotEmpty()){
            holder.bindfing.tvtime.text=DateUtils.getConvertDateTiemFormat(userModel.apply_create_date)
        }

        holder.bindfing.root.setOnClickListener(View.OnClickListener {
            onClickItem.onClick(userModel)
        })
    }

    interface onClickItems{
        fun onClick(userModel: UserModel)
    }

}