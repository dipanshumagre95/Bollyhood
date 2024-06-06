package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.databinding.AdapterAllActorsBinding
import com.app.bollyhood.model.SingleCategoryModel
import com.bumptech.glide.Glide

class AllActorsAdapter(val context: Context, var list: ArrayList<SingleCategoryModel>,val onitemclick:AllActorsAdapter.onItemCLick): RecyclerView.Adapter<AllActorsAdapter.ActoreViewHolder>() {


    class ActoreViewHolder(var binding: AdapterAllActorsBinding) :RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActoreViewHolder {
        return ActoreViewHolder(
            AdapterAllActorsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ActoreViewHolder, position: Int) {
        val model=list[position]
        Glide.with(context).load(model.image).placeholder(R.drawable.ic_profile).centerCrop().into(holder.binding.ivImage)
        holder.binding.txActorsName.text=model.name
        holder.binding.txrole.text=model.category_name

        holder.binding.mainItem.setOnClickListener(View.OnClickListener{
            onitemclick.onClick(model)
        })
    }

    interface onItemCLick{

        fun onClick(singleCategoryModel: SingleCategoryModel)
    }

}