package com.app.bollyhood.adapter

import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.databinding.AdpChathistoryBinding
import com.app.bollyhood.model.ChatModel
import com.app.bollyhood.model.SenderDetails
import com.app.bollyhood.util.DateUtils
import com.app.bollyhood.util.DialogsUtils.showCustomToast
import com.app.bollyhood.util.DownloadReceiver
import com.app.bollyhood.util.StaticData
import com.bumptech.glide.Glide
import com.google.common.io.Files.getFileExtension
import java.io.File

class ChatHistoryAdapter(
    val context: Context,
    val chatModel: ArrayList<ChatModel>,
    val senderDetails: SenderDetails,
    val chatHistoryInterface: ChatHistoryInterface
) : RecyclerView.Adapter<ChatHistoryAdapter.MyViewHolder>() {

    private var downloadReceiver: DownloadReceiver? = null
    private var downloadId: Long = -1

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
            setupSenderView(holder, position)
        } else if (chatModel[position].user_type == 2) {
            setupReceiverView(holder, position)
        }

        holder.binding.ivdownload.setOnClickListener {
            if (!isFileAvailableInDirectory(holder.binding.tvReceiver.text.toString())) {
                if (isDocumentFile(holder.binding.tvReceiver.text.toString())) {
                    holder.binding.ivdownload.visibility = View.GONE
                    holder.binding.pbLoadData.visibility = View.VISIBLE
                    downloadFile(
                        chatModel[position].image,
                        holder.binding.tvReceiver.text.toString(),
                        holder
                    )
                }
            }else{
                holder.binding.ivdownload.visibility = View.GONE
                showCustomToast(context,StaticData.actionRequired,"File already downloaded", StaticData.alert)
            }
        }
    }

    private fun setupSenderView(holder: MyViewHolder, position: Int) {
        holder.binding.llSender.visibility = View.VISIBLE
        holder.binding.llReceiver.visibility = View.GONE
        holder.binding.tvSenderTime.text = DateUtils.getConvertDateTiemFormat(chatModel[position].added_on)

        if (chatModel[position].text.isNotEmpty() && chatModel[position].image.isEmpty()) {
            holder.binding.llsenderImage.visibility = View.GONE
            holder.binding.senderchatBackground.setBackgroundResource(R.drawable.chat_history_background)
            holder.binding.tvSender.visibility = View.VISIBLE
            holder.binding.sendervoicePlayerView.visibility=View.GONE
            holder.binding.tvSender.text = chatModel[position].text
        } else if (chatModel[position].image.isNotEmpty() && chatModel[position].text.isEmpty()) {
            handleSenderImage(holder, position)
        } else if (chatModel[position].image.isNotEmpty() && chatModel[position].text.isNotEmpty()) {
            holder.binding.tvSender.visibility = View.VISIBLE
            holder.binding.llsenderImage.visibility = View.VISIBLE
            holder.binding.sendervoicePlayerView.visibility=View.GONE
            holder.binding.senderchatBackground.setBackgroundResource(R.drawable.chat_history_background)
            Glide.with(context).load(chatModel[position].image).into(holder.binding.ivSendImage)
            holder.binding.tvSender.text = chatModel[position].text
        }
    }

    private fun handleSenderImage(holder: MyViewHolder, position: Int) {
        if (isImageUrl(chatModel[position].image)) {
            holder.binding.tvSender.visibility = View.GONE
            holder.binding.llsenderImage.visibility = View.VISIBLE
            holder.binding.sendervoicePlayerView.visibility=View.GONE
            holder.binding.senderchatBackground.setBackgroundResource(R.drawable.rectangle_app_background)
            Glide.with(context).load(chatModel[position].image).into(holder.binding.ivSendImage)
        } else if (isDocumentFile(chatModel[position].image)) {
            holder.binding.llsenderImage.visibility = View.GONE
            holder.binding.senderchatBackground.setBackgroundResource(R.drawable.chat_history_background)
            holder.binding.tvSender.visibility = View.VISIBLE
            holder.binding.sendervoicePlayerView.visibility=View.GONE
            holder.binding.tvSender.text = getLastName(chatModel[position].image)
        } else if (chatModel[position].image.contains("mp3")){
            holder.binding.llsenderImage.visibility = View.GONE
            holder.binding.senderchatBackground.setBackgroundResource(R.drawable.chat_history_background)
            holder.binding.tvSender.visibility = View.GONE
            holder.binding.sendervoicePlayerView.visibility=View.VISIBLE
            holder.binding.sendervoicePlayerView.setAudio(chatModel[position].image)
        }
    }

    private fun setupReceiverView(holder: MyViewHolder, position: Int) {
        holder.binding.llReceiver.visibility = View.VISIBLE
        holder.binding.llSender.visibility = View.GONE
        Glide.with(context).load(senderDetails.image).placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile).into(holder.binding.ivImage)
        holder.binding.tvName.text = senderDetails.name
        holder.binding.tvReceiverTime.text = DateUtils.getConvertDateTiemFormat(chatModel[position].added_on)

        if (chatModel[position].text.isNotEmpty() && chatModel[position].image.isEmpty()) {
            handleReceiverTextOnly(holder,position)
        } else if (chatModel[position].image.isNotEmpty() && chatModel[position].text.isEmpty()) {
            handleReceiverImage(holder, position)
        } else if (chatModel[position].image.isNotEmpty() && chatModel[position].text.isNotEmpty()) {
            holder.binding.tvReceiver.visibility = View.VISIBLE
            holder.binding.llrecevierImage.visibility = View.VISIBLE
            holder.binding.ivdownload.visibility = View.GONE
            holder.binding.receviervoicePlayerView.visibility=View.GONE
            holder.binding.recevierChatBackground.setBackgroundResource(R.drawable.chat_rectangle_grey)
            Glide.with(context).load(chatModel[position].image).into(holder.binding.ivReceiverImage)
            holder.binding.tvReceiver.text = chatModel[position].text
        }
    }

    private fun handleReceiverTextOnly(holder: MyViewHolder,position:Int) {
        holder.binding.llrecevierImage.visibility = View.GONE
        holder.binding.ivdownload.visibility = View.GONE
        holder.binding.tvReceiver.visibility = View.VISIBLE
        holder.binding.receviervoicePlayerView.visibility=View.GONE
        holder.binding.recevierChatBackground.setBackgroundResource(R.drawable.chat_rectangle_grey)
        holder.binding.tvReceiver.text = chatModel[position].text
    }

    private fun handleReceiverImage(holder: MyViewHolder, position: Int) {
        if (isImageUrl(chatModel[position].image)) {
            holder.binding.tvReceiver.visibility = View.GONE
            holder.binding.ivdownload.visibility = View.GONE
            holder.binding.llrecevierImage.visibility = View.VISIBLE
            holder.binding.receviervoicePlayerView.visibility=View.GONE
            holder.binding.recevierChatBackground.setBackgroundResource(R.drawable.rectangle_app_background)
            Glide.with(context).load(chatModel[position].image).into(holder.binding.ivReceiverImage)
        } else if (isDocumentFile(chatModel[position].image)) {
            holder.binding.llrecevierImage.visibility = View.GONE
            holder.binding.tvReceiver.visibility = View.VISIBLE
            holder.binding.receviervoicePlayerView.visibility=View.GONE
            holder.binding.recevierChatBackground.setBackgroundResource(R.drawable.chat_rectangle_grey)
            holder.binding.tvReceiver.text = getLastName(chatModel[position].image)
                holder.binding.ivdownload.visibility = View.VISIBLE
        }else if (chatModel[position].image.contains("mp3")){
            holder.binding.llrecevierImage.visibility = View.GONE
            holder.binding.tvReceiver.visibility = View.GONE
            holder.binding.recevierChatBackground.setBackgroundResource(R.drawable.chat_rectangle_grey)
            holder.binding.tvReceiver.text = getLastName(chatModel[position].image)
            holder.binding.ivdownload.visibility = View.GONE
            holder.binding.receviervoicePlayerView.visibility=View.VISIBLE
            holder.binding.receviervoicePlayerView.setAudio(chatModel[position].image)
        }
    }

    fun isImageUrl(url: String): Boolean {
        val imageExtensions = listOf("png", "jpg", "jpeg", "gif", "bmp", "webp", "tiff", "svg")
        val extension = url.substringAfterLast('.', "").lowercase()
        return imageExtensions.contains(extension)
    }

    fun isDocumentFile(url: String): Boolean {
        val documentExtensions = listOf("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "rtf", "csv")
        val extension = getFileExtension(url).lowercase()
        return documentExtensions.contains(extension)
    }

    fun getLastName(url: String): String {
        return url.substringAfterLast('/')
    }

    fun isFileAvailableInDirectory(fileName: String): Boolean {
        val directory = File("/storage/emulated/0/Download")
        if (directory.exists() && directory.isDirectory) {
            val file = File(directory, fileName)
            return file.exists()
        }
        return false
    }

    private fun downloadFile(url: String, fileName: String, holder: MyViewHolder) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManager.enqueue(request)

        if (downloadReceiver == null) {
            downloadReceiver = DownloadReceiver { id ->
                if (id == downloadId) {
                    holder.binding.pbLoadData.visibility = View.GONE
                    Toast.makeText(context, "Download Complete", Toast.LENGTH_SHORT).show()
                    // Unregister receiver once download is complete
                    context.unregisterReceiver(downloadReceiver)
                    downloadReceiver = null
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.registerReceiver(downloadReceiver,  IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                    Context.RECEIVER_EXPORTED
                );
            } else {
                context.registerReceiver(
                    downloadReceiver,
                    IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                )
            }
        }
    }

    fun unregisterReceiver() {
        if (downloadReceiver != null) {
            context.unregisterReceiver(downloadReceiver)
            downloadReceiver = null
        }
    }

    interface ChatHistoryInterface {
        fun download(fileName: String)
    }

    class MyViewHolder(val binding: AdpChathistoryBinding) : RecyclerView.ViewHolder(binding.root)
}
