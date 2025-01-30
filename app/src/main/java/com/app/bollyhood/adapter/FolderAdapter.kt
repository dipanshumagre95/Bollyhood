package com.app.bollyhood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.databinding.FolderAdapterBinding
import com.app.bollyhood.model.Folder

class FolderAdapter(
    private val folderList: List<Folder>,
    private val listener: OnFolderClickListener
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>(){

    class FolderViewHolder(val binding:FolderAdapterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        return FolderViewHolder(
            FolderAdapterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folderList[position]

        if (!folder.folder_name.isNullOrEmpty()){
            holder.binding.folderName.text=folder.folder_name
        }

    }

    override fun getItemCount(): Int = folderList.size

    interface OnFolderClickListener {
        fun onFolderClick(folder: Folder)
    }

}