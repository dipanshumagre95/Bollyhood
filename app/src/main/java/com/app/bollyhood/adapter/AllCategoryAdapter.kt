package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.databinding.AdpAllCategoryBinding
import com.app.bollyhood.model.CategoryModel
import com.bumptech.glide.Glide

class AllCategoryAdapter(val requireContext: Context, var categoryList: ArrayList<CategoryModel>,
                         val onClick:onItemClick) :
    RecyclerView.Adapter<AllCategoryAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return MyViewHolder(
            AdpAllCategoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val model = categoryList[position]
        Glide.with(requireContext)
            .load(model.category_image)
            .centerCrop()
            .error(R.drawable.ic_profile)
            .into(holder.binding.ivCategoryImage)
        holder.binding.tvCategoryName.text = model.category_name

        holder.itemView.setOnClickListener {
            onClick.onItemClick(position, categoryModel = categoryList[position])
        }

        /*
                holder.binding.apply {
                    rvInnerCategory.layoutManager=GridLayoutManager(requireContext,3)
                    rvInnerCategory.setHasFixedSize(true)
                    adapter=CategoryInnerAdapter(requireContext,categoryList)
                    rvInnerCategory.adapter=adapter
                    adapter?.notifyDataSetChanged()
                }
        */

    }

    fun updateList(newCategoryList: ArrayList<CategoryModel>) {
        categoryList = newCategoryList
        notifyDataSetChanged()
    }

    class MyViewHolder(val binding: AdpAllCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    interface onItemClick{
        fun onItemClick(pos:Int,categoryModel: CategoryModel)
    }
}