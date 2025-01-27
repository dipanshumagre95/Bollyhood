package com.app.bollyhood.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.activity.CastingBookMarkDetailActivity
import com.app.bollyhood.databinding.AdpCastingCallsBinding
import com.app.bollyhood.model.CastingCallModel
import com.bumptech.glide.Glide
import com.google.gson.Gson


class CastingBookMarkAdapter(
    val mContext: Context,
    val castingModels: ArrayList<CastingCallModel>
) : RecyclerView.Adapter<CastingBookMarkAdapter.MyViewHolder>() {

    private val backgroundColors = intArrayOf(
        R.drawable.rectangle_castingcall1,
        R.drawable.rectangle_castingcall2,
        R.drawable.rectangle_castingcall3
    )

    private val locationColors = intArrayOf(
        R.drawable.rectangle_loc_blue,
        R.drawable.rectangle_loc_red,
        R.drawable.rectangle_loc_yellow
    )


    private val verifiedColors = intArrayOf(
        R.drawable.rectangle_loc_blue,
        R.drawable.rectangle_loc_red,
        R.drawable.rectangle_loc_yellow
    )


    private val shiftColors = intArrayOf(
        R.drawable.rectangle_loc_blue,
        R.drawable.rectangle_loc_red,
        R.drawable.rectangle_loc_yellow
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            AdpCastingCallsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun getItemCount(): Int {
        return castingModels.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.model = castingModels[position]

        if (castingModels[position].company_logo.isNotEmpty()) {
            Glide.with(mContext).load(castingModels[position].company_logo)
                .into(holder.binding.ivLogo)
        }

        holder.binding.tvShift.text = castingModels[position].shift_time + " Hr Shift"

        holder.binding.tvDesc.text = castingModels[position].description
        holder.binding.tvDate.text = castingModels[position].date

        if (castingModels[position].price_discussed.equals("1")) {
            holder.binding.tvSalary.text = "T B D"
        } else {
            holder.binding.tvSalary.text = "₹" + castingModels[position].price
        }

        /*holder.binding.tvDesc.setCollapsedText("Read More")
        holder.binding.tvDesc.setExpandedText("Read Less")

        holder.binding.tvDesc.setCollapsedTextColor(R.color.white)
        holder.binding.tvDesc.setExpandedTextColor(R.color.white)

        holder.binding.tvDesc.setTrimLines(2) //By Default it will be only 2 lines*/


        when (castingModels[position].type) {
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



        holder.itemView.setOnClickListener {
            mContext.startActivity(
                Intent(mContext, CastingBookMarkDetailActivity::class.java)
                    .putExtra("type", castingModels[position].type)
                    .putExtra("model", Gson().toJson(castingModels[position]))
            )
        }
    }


    class MyViewHolder(val binding: AdpCastingCallsBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {

    }
}