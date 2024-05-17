package com.app.bollyhood.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.activity.SubscriptionPlanActivity
import com.app.bollyhood.databinding.AdpPlanBinding
import com.app.bollyhood.model.PlanModel

class PlanAdapter(
    val mContext: SubscriptionPlanActivity, val planList: ArrayList<PlanModel>,
    val onclick: onItemClick
) :
    RecyclerView.Adapter<PlanAdapter.MyViewHolder>() {

    var checkedPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            AdpPlanBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun getItemCount(): Int {
        return planList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val planModel = planList[position]
        holder.binding.apply {
            tvTitle.text = planModel.title
            tvPrice.text = "₹ " + planModel.price + " / " + planModel.type
            tvDesc.text = planModel.description
        }

        holder.binding.rbChecked.isChecked = checkedPosition == position

        if (position % 2 == 0) {
            holder.binding.rrMain.setBackgroundResource(R.drawable.rectangle_plan1)
        } else {
            holder.binding.rrMain.setBackgroundResource(R.drawable.rectangle_plan2)
        }

        holder.binding.rrMain.setOnClickListener {
            checkedPosition = position
            onclick.onClick(position,planModel)
            notifyDataSetChanged()
        }
    }

    class MyViewHolder(val binding: AdpPlanBinding) : RecyclerView.ViewHolder(binding.root)

    interface onItemClick {
        fun onClick(position: Int, planModel: PlanModel)
    }
}