package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.databinding.AllcastingcalllAdapterBinding

class AllCastingCallListAdapter(val context: Context,val onitemclick:AllCastingCallListAdapter.OnClickInterface,val type:Int): RecyclerView.Adapter<AllCastingCallListAdapter.MyViewHolder>() {


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
       return 3
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        when(position){
            0 ->{
                if (type==2){
                    holder.binding.tvcastingStatus.text="Casting Call is Closed"
                    holder.binding.tvcastingStatus.setTextColor(ContextCompat.getColor(context, R.color.colorred))
                    holder.binding.ivstatusIcon.visibility=View.GONE
                }
                holder.binding.tvcastingName.text="Duet Dance Reality Show"
                holder.binding.tvapplyCount.text="100+"
                holder.binding.tvtime.text="10 min ago"
            }

            1 ->{
                if (type==2){
                    holder.binding.tvcastingStatus.text="Casting Call is Closed"
                    holder.binding.tvcastingStatus.setTextColor(ContextCompat.getColor(context, R.color.colorred))
                    holder.binding.ivstatusIcon.visibility=View.GONE
                }
                holder.binding.tvcastingName.text="Actors For Drama Film"
                holder.binding.tvapplyCount.text="10"
                holder.binding.tvtime.text="2 days ago"
            }

            2 ->{
                if (type==2){
                    holder.binding.tvcastingStatus.text="Casting Call is Closed"
                    holder.binding.tvcastingStatus.setTextColor(ContextCompat.getColor(context, R.color.colorred))
                    holder.binding.ivstatusIcon.visibility=View.GONE
                }
                holder.binding.tvcastingName.text="Female Actors Crime Thriller "
                holder.binding.tvapplyCount.visibility=View.GONE
                holder.binding.tvtime.text="1 month ago"
            }
        }

        holder.binding.llmain.setOnClickListener(View.OnClickListener {
            onitemclick.onItemClick(holder.binding.tvcastingName.text.toString(),holder.binding.tvapplyCount.text.toString(),holder.binding.tvtime.text.toString())
        })
    }

    interface OnClickInterface
    {
        fun onItemClick(name:String,count:String,time:String)
    }
}