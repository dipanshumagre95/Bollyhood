package com.app.bollyhood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.BookinglistUsersAdapterBinding

class BookingListUsersAdapter(val context: Context): RecyclerView.Adapter<BookingListUsersAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: BookinglistUsersAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BookinglistUsersAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int= 5

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    interface OnItemClickListener{
        fun onItemClick()
    }
}