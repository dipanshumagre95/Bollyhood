package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.databinding.AdpAllCategoryBinding
import com.app.bollyhood.databinding.AdpInnerCategoryBinding
import com.app.bollyhood.model.CategoryModel
import com.bumptech.glide.Glide

class CategoryInnerAdapter(val requireContext: Context, val categoryList: ArrayList<CategoryModel>) :
    RecyclerView.Adapter<CategoryInnerAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return MyViewHolder(
            AdpInnerCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val model = categoryList[position]
        Glide.with(requireContext).load(model.category_image).placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile).into(holder.binding.ivCategoryImage)
        holder.binding.tvCategoryName.text = model.category_name
    }

    class MyViewHolder(val binding: AdpInnerCategoryBinding) : RecyclerView.ViewHolder(binding.root)
}