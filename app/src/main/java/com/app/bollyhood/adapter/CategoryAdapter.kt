package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.databinding.AdpCategpryBinding
import com.app.bollyhood.model.CategoryModel
import com.bumptech.glide.Glide

class CategoryAdapter(val requireContext: Context, val categoryList: ArrayList<CategoryModel>,
    val onclick:onItemClick) :
    RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return MyViewHolder(
            AdpCategpryBinding.inflate(
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

        holder.itemView.setOnClickListener {
            onclick.onClick(position, categoryModel = categoryList[position])
        }
    }

    class MyViewHolder(val binding: AdpCategpryBinding) : RecyclerView.ViewHolder(binding.root)

    interface onItemClick{
        fun onClick(pos:Int,categoryModel: CategoryModel)
    }
}