package com.app.bollyhood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.activity.ChatActivity
import com.app.bollyhood.databinding.AdpBannerlistBinding
import com.app.bollyhood.databinding.AdpChathistoryBinding
import com.app.bollyhood.model.ChatModel
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class ChatHistoryAdapter(val chatActivity: ChatActivity, val chatModel: ArrayList<ChatModel>) :
    RecyclerView.Adapter<ChatHistoryAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return MyViewHolder(
            AdpChathistoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun getItemCount(): Int {
        return chatModel.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.model = chatModel[position]

        if (chatModel[position].user_type == "1") {
            holder.binding.llSender.visibility = View.VISIBLE
            holder.binding.llReceiver.visibility = View.GONE

            holder.binding.tvSenderTime.text =
                getConvertDateTiemFormat(chatModel[position].added_on)


            if (chatModel[position].text.isNotEmpty() && chatModel[position].image.isEmpty()) {
                holder.binding.ivSendImage.visibility = View.GONE
                holder.binding.tvSender.visibility = View.VISIBLE
                holder.binding.tvSender.text = chatModel[position].text


            } else if (chatModel[position].image.isNotEmpty() && chatModel[position].text.isEmpty()) {
                holder.binding.tvSender.visibility = View.GONE
                holder.binding.ivSendImage.visibility = View.VISIBLE
                Glide.with(chatActivity).load(chatModel[position].image)
                    .into(holder.binding.ivSendImage)


            } else if (chatModel[position].image.isNotEmpty() && chatModel[position].text.isNotEmpty()) {
                holder.binding.tvSender.visibility = View.VISIBLE
                holder.binding.ivSendImage.visibility = View.VISIBLE

                Glide.with(chatActivity).load(chatModel[position].image)
                    .into(holder.binding.ivSendImage)
                holder.binding.tvSender.text = chatModel[position].text


            }


        } else {
            holder.binding.llReceiver.visibility = View.VISIBLE
            holder.binding.llSender.visibility = View.GONE

            holder.binding.tvReceiverTime.text =
                getConvertDateTiemFormat(chatModel[position].added_on)

            if (chatModel[position].text.isNotEmpty() && chatModel[position].image.isEmpty()) {
                holder.binding.ivReceiverImage.visibility = View.GONE
                holder.binding.tvReceiver.visibility = View.VISIBLE
                holder.binding.tvReceiver.text = chatModel[position].text


            } else if (chatModel[position].image.isNotEmpty() && chatModel[position].text.isEmpty()) {
                holder.binding.tvReceiver.visibility = View.GONE
                holder.binding.ivReceiverImage.visibility = View.VISIBLE
                Glide.with(chatActivity).load(chatModel[position].image)
                    .into(holder.binding.ivReceiverImage)


            } else if (chatModel[position].image.isNotEmpty() && chatModel[position].text.isNotEmpty()) {
                holder.binding.tvReceiver.visibility = View.VISIBLE
                holder.binding.ivReceiverImage.visibility = View.VISIBLE

                Glide.with(chatActivity).load(chatModel[position].image)
                    .into(holder.binding.ivReceiverImage)
                holder.binding.tvReceiver.text = chatModel[position].text


            }


        }


    }

    class MyViewHolder(val binding: AdpChathistoryBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {

    }

    fun getConvertDateTiemFormat(dateFormat: String): String? {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = simpleDateFormat.parse(dateFormat)
            ?.let { SimpleDateFormat("dd MMMM yyyy hh:mm aa", Locale.getDefault()).format(it) }
        return date
    }
}