package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.AllcastingcalllAdapterBinding

class AllCastingCallListAdapter(val context: Context,val onitemclick:AllCastingCallListAdapter.OnClickInterface): RecyclerView.Adapter<AllCastingCallListAdapter.MyViewHolder>() {


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
       return 5
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.binding.llmain.setOnClickListener(View.OnClickListener {
            onitemclick.onItemClick()
        })
    }

    interface OnClickInterface
    {
        fun onItemClick()
    }
}