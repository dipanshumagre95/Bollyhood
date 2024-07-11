package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.databinding.AdapterProductionHouseBinding
import com.app.bollyhood.model.SingleCategoryModel
import com.bumptech.glide.Glide

class ProductionHouseAdapter(val context:Context,var productionList: ArrayList<SingleCategoryModel>,val onProductionItemClick: OnProductionItemClick): RecyclerView.Adapter<ProductionHouseAdapter.ViewHolder>() {


    class ViewHolder(var binding: AdapterProductionHouseBinding) :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AdapterProductionHouseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productionList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productionHouse=productionList[position]

        Glide.with(context).load(productionHouse.image).placeholder(R.drawable.ic_profile).centerCrop().into(holder.binding.ivImage)
        holder.binding.tvName.text=productionHouse.name
        holder.binding.tvLocation.text=productionHouse.location
        holder.binding.tvtag.text=productionHouse.tag

        holder.binding.root.setOnClickListener( {
            onProductionItemClick.onItemClick(productionHouse)
        })
    }

    fun updateList(newCategoryList: ArrayList<SingleCategoryModel>) {
        productionList = newCategoryList
        notifyDataSetChanged()
    }

    interface OnProductionItemClick{
        fun onItemClick(singleCategoryModel: SingleCategoryModel)
    }
}