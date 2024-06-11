package com.app.bollyhood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.activity.ChatActivity
import com.app.bollyhood.databinding.AdpChathistoryBinding
import com.app.bollyhood.model.ChatModel
import com.app.bollyhood.model.SenderDetails
import com.app.bollyhood.util.DateUtils
import com.bumptech.glide.Glide

class ChatHistoryAdapter(val chatActivity: ChatActivity, val chatModel: ArrayList<ChatModel>,val senderDetails: SenderDetails) :
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

        if (chatModel[position].user_type == 1) {
            holder.binding.llSender.visibility = View.VISIBLE
            holder.binding.llReceiver.visibility = View.GONE

            holder.binding.tvSenderTime.text =
                DateUtils.getConvertDateTiemFormat(chatModel[position].added_on)


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


        } else if (chatModel[position].user_type == 2) {
            holder.binding.llReceiver.visibility = View.VISIBLE
            holder.binding.llSender.visibility = View.GONE
            Glide.with(chatActivity).load(senderDetails.image).placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile).into(holder.binding.ivImage)
            holder.binding.tvName.text=senderDetails.name

            holder.binding.tvReceiverTime.text =
                DateUtils.getConvertDateTiemFormat(chatModel[position].added_on)

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

}