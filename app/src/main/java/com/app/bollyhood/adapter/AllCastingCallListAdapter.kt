package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.databinding.AllcastingcalllAdapterBinding
import com.app.bollyhood.model.CastingCallModel
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

        if (!castingModel.apply_casting_count.isNullOrEmpty()&&castingModel.apply_casting_count!="0"){
            holder.binding.tvapplyCount.visibility=View.VISIBLE
            holder.binding.tvapplyCount.text=castingModel.apply_casting_count
        }else{
            holder.binding.tvapplyCount.visibility=View.GONE
        }

        holder.binding.llmain.setOnClickListener(View.OnClickListener {
            onitemclick.onItemClick(castingModel)
        })

        holder.binding.threeDots.setOnClickListener(View.OnClickListener {
            showCustomMenu(castingModel.pin_type,holder.binding.threeDots, position,castingModel)
        })
    }

    private fun showCustomMenu(pin_type:String,anchor: View, position: Int,castingModel:CastingCallModel) {

        val inflater = LayoutInflater.from(context)
        val menuView = inflater.inflate(R.layout.menu_options_layout, null)

        val popupWindow = PopupWindow(
            menuView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val pinOption = menuView.findViewById<RelativeLayout>(R.id.menu_pin)
        val editOption = menuView.findViewById<RelativeLayout>(R.id.menu_edit)
        val hideAlertsOption = menuView.findViewById<RelativeLayout>(R.id.menu_hide_alerts)
        val deleteOption = menuView.findViewById<RelativeLayout>(R.id.menu_delete)
        val tvPin = menuView.findViewById<TextView>(R.id.tvPin)

        if (pin_type=="1"){
            tvPin.text="Unpin"
        }


        pinOption.setOnClickListener {
            onitemclick.pinCasting(castingModel)
            popupWindow.dismiss()
        }

        editOption.setOnClickListener {
            Toast.makeText(context, "Edit clicked for item $position", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        hideAlertsOption.setOnClickListener {
            Toast.makeText(context, "Hide Alerts clicked for item $position", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        deleteOption.setOnClickListener {
            Toast.makeText(context, "Delete clicked for item $position", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        popupWindow.isOutsideTouchable = true
        popupWindow.showAsDropDown(anchor)
    }


    interface OnClickInterface
    {
        fun onItemClick(castingModel:CastingCallModel)

        fun pinCasting(castingModel:CastingCallModel)
    }
}