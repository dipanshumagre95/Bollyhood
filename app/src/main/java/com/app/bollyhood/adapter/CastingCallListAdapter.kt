package com.app.bollyhood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.AdapterCastinglistedBinding

class CastingCallListAdapter: RecyclerView.Adapter<CastingCallListAdapter.MyViewHolder>() {

    class MyViewHolder(val bindfing:AdapterCastinglistedBinding) :RecyclerView.ViewHolder(bindfing.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return CastingCallListAdapter.MyViewHolder(
            AdapterCastinglistedBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun onBindViewHolder(holder: CastingCallListAdapter.MyViewHolder, position: Int) {

    }
}