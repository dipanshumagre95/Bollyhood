package com.app.bollyhood.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.activity.AllAgencyActivity
import com.app.bollyhood.databinding.AdpAgencyBinding
import com.app.bollyhood.model.castinglist.CastingDataModel
import com.bumptech.glide.Glide

class AllAgencyAdapter(val mContext: AllAgencyActivity, val castingApply: ArrayList<CastingDataModel>,val onclick:onItemClick)
    :RecyclerView.Adapter<AllAgencyAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            AdpAgencyBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun getItemCount(): Int {
        return castingApply.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.model = castingApply[position]

        if (castingApply[position].company_logo.isNotEmpty()) {
            Glide.with(mContext).load(castingApply[position].company_logo)
                .into(holder.binding.ivLogo)
        }

        holder.binding.tvShift.text = castingApply[position].shifting + " Hr Shift"

        holder.binding.tvDesc.text = castingApply[position].description
        holder.binding.tvDate.text = castingApply[position].date

        if (castingApply[position].price_discussed.equals("1")) {
            holder.binding.tvSalary.text = "T B D"
        } else {
            holder.binding.tvSalary.text = "₹" + castingApply[position].price
        }

        holder.binding.tvDesc.setCollapsedText("Read More")
        holder.binding.tvDesc.setExpandedText("Read Less")

        holder.binding.tvDesc.setCollapsedTextColor(R.color.white)
        holder.binding.tvDesc.setExpandedTextColor(R.color.white)

        holder.binding.tvDesc.setTrimLines(2) //By Default it will be only 2 lines


        when (castingApply[position].type) {
            "blue" -> {
                holder.binding.llMain.setBackgroundResource(R.drawable.rectangle_castingcall1)
                holder.binding.tvLocation.setBackgroundResource(R.drawable.rectangle_loc_blue)
                holder.binding.tvVerified.setBackgroundResource(R.drawable.rectangle_loc_blue)
                holder.binding.tvShift.setBackgroundResource(R.drawable.rectangle_loc_blue)

            }
            "red" -> {
                holder.binding.llMain.setBackgroundResource(R.drawable.rectangle_castingcall2)
                holder.binding.tvLocation.setBackgroundResource(R.drawable.rectangle_loc_red)
                holder.binding.tvVerified.setBackgroundResource(R.drawable.rectangle_loc_red)
                holder.binding.tvShift.setBackgroundResource(R.drawable.rectangle_loc_red)
            }
            else -> {
                holder.binding.llMain.setBackgroundResource(R.drawable.rectangle_castingcall3)
                holder.binding.tvLocation.setBackgroundResource(R.drawable.rectangle_loc_yellow)
                holder.binding.tvVerified.setBackgroundResource(R.drawable.rectangle_loc_yellow)
                holder.binding.tvShift.setBackgroundResource(R.drawable.rectangle_loc_yellow)
            }
        }



        holder.binding.tvView.paintFlags =
            holder.binding.tvView.paintFlags or Paint.UNDERLINE_TEXT_FLAG;

        holder.binding.tvView.setOnClickListener {
            onclick.onClick(position, castingDataModel = castingApply[position])
        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class MyViewHolder(val binding:AdpAgencyBinding ) :RecyclerView.ViewHolder(binding.root)

    interface onItemClick{
        fun onClick(pos:Int,castingDataModel: CastingDataModel)
    }
}