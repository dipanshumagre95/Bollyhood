package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.AllcastingcalllAdapterBinding
import com.app.bollyhood.model.CastingCallModel
import com.app.bollyhood.util.DateUtils
import com.bumptech.glide.Glide

class AllCastingCallListAdapter(val context: Context,val onitemclick:AllCastingCallListAdapter.OnClickInterface,val castingModels: ArrayList<CastingCallModel>): RecyclerView.Adapter<AllCastingCallListAdapter.MyViewHolder>() {


    class MyViewHolder(val binding:AllcastingcalllAdapterBinding) :RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return AllCastingCallListAdapter.MyViewHolder(
            AllcastingcalllAdapterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
       return castingModels.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val castingModel=castingModels[position]

        if (castingModels[position].company_logo.isNotEmpty()) {
            Glide.with(context).load(castingModels[position].company_logo)
                .into(holder.binding.ivImage)
        }

        if (!castingModel.role.isNullOrEmpty()){
            holder.binding.tvcastingName.text=castingModel.role
        }

        if (!castingModels[position].modify_date.isNullOrEmpty()) {
            holder.binding.tvtime.text = DateUtils.getConvertDateTiemFormat(castingModels[position].modify_date)
        }

        if (!castingModel.apply_casting_count.isNullOrEmpty()&&castingModel.apply_casting_count!="0"){
            holder.binding.tvapplyCount.visibility=View.VISIBLE
            holder.binding.tvapplyCount.text=castingModel.apply_casting_count
        }else{
            holder.binding.tvapplyCount.visibility=View.GONE
        }

        holder.binding.llmain.setOnClickListener(View.OnClickListener {
            onitemclick.onItemClick(castingModel)
        })
    }

    interface OnClickInterface
    {
        fun onItemClick(castingModel:CastingCallModel)
    }
}