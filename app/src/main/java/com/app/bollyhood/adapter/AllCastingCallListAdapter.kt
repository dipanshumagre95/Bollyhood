package com.app.bollyhood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.AllcastingcalllAdapterBinding

class AllCastingCallListAdapter: RecyclerView.Adapter<AllCastingCallListAdapter.MyViewHolder>() {


    class MyViewHolder(val bindfing:AllcastingcalllAdapterBinding) :RecyclerView.ViewHolder(bindfing.root){

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

    }
}