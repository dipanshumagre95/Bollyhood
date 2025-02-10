package com.app.bollyhood.util

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.bollyhood.R
import com.app.bollyhood.adapter.FolderAdapter
import com.app.bollyhood.model.Folder
import com.app.bollyhood.model.WorkLinkProfileData
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

object DialogsUtils {

    fun showWorkLinksDialog(
        context: Context,
        workLinkList: MutableList<WorkLinkProfileData>,
        onLinksAdded: (List<WorkLinkProfileData>) -> Unit
    ) {
        val dialogView = Dialog(context)
        dialogView.setContentView(R.layout.add_work_link)
        dialogView.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val workLinkName1 = dialogView.findViewById<EditText>(R.id.edtWorkLinkName1)
        val workLinkName2 = dialogView.findViewById<EditText>(R.id.edtWorkLinkName2)
        val workLinkName3 = dialogView.findViewById<EditText>(R.id.edtWorkLinkName3)
        val linkName1 = dialogView.findViewById<TextView>(R.id.edtAddWorkLink1)
        val linkName2 = dialogView.findViewById<TextView>(R.id.edtAddWorkLink2)
        val linkName3 = dialogView.findViewById<TextView>(R.id.edtAddWorkLink3)
        val addButton = dialogView.findViewById<TextView>(R.id.tvAddLinks)

        if (workLinkList.isNotEmpty()) {
            workLinkList.forEachIndexed { index, worklink ->
                when (index) {
                    0 -> {
                        workLinkName1.setText(worklink.worklink_name)
                        linkName1.setText(worklink.worklink_url)
                    }
                    1 -> {
                        workLinkName2.setText(worklink.worklink_name)
                        linkName2.setText(worklink.worklink_url)
                    }
                    2 -> {
                        workLinkName3.setText(worklink.worklink_name)
                        linkName3.setText(worklink.worklink_url)
                    }
                }
            }
        }

        addButton.setOnClickListener {
            val updatedWorkLinks = mutableListOf<WorkLinkProfileData>()

            if (workLinkName1.text.isNotEmpty() && linkName1.text.isNotEmpty()) {
                if (isValidYouTubeUrl(linkName1.text.toString())) {
                    updatedWorkLinks.add(WorkLinkProfileData(workLinkName1.text.toString(), linkName1.text.toString()))
                } else {
                    Toast.makeText(context, "Invalid ${workLinkName1.text} YouTube URL", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            if (workLinkName2.text.isNotEmpty() && linkName2.text.isNotEmpty()) {
                if (isValidYouTubeUrl(linkName2.text.toString())) {
                    updatedWorkLinks.add(WorkLinkProfileData(workLinkName2.text.toString(), linkName2.text.toString()))
                } else {
                    Toast.makeText(context, "Invalid ${workLinkName2.text} YouTube URL", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            if (workLinkName3.text.isNotEmpty() && linkName3.text.isNotEmpty()) {
                if (isValidYouTubeUrl(linkName3.text.toString())) {
                    updatedWorkLinks.add(WorkLinkProfileData(workLinkName3.text.toString(), linkName3.text.toString()))
                } else {
                    Toast.makeText(context, "Invalid ${workLinkName3.text} YouTube URL", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            if (updatedWorkLinks.isNotEmpty()) {
                onLinksAdded(updatedWorkLinks)
                dialogView.dismiss()
            } else {
                showCustomToast(context,StaticData.actionRequired,"Add at least one work link",StaticData.alert)
            }
        }

        dialogView.show()
    }

    private fun isValidYouTubeUrl(url: String): Boolean {
        val youtubeRegex = "^(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+\$".toRegex()
        return youtubeRegex.matches(url)
    }


    fun createFolderButton(context: Context,
                           onAddFolder: (Folder) -> Unit){
        val dialog= Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.create_folder_dialog)

        val createFolderButton = dialog.findViewById<RelativeLayout>(R.id.createFolderButton)
        val addFolderView = dialog.findViewById<LinearLayout>(R.id.addFolderView)
        val addButton = dialog.findViewById<TextView>(R.id.addButton)
        val folderName = dialog.findViewById<EditText>(R.id.folderName)
        val rvFolderList = dialog.findViewById<RecyclerView>(R.id.rvFolderList)
        val yourListtext = dialog.findViewById<TextView>(R.id.tvtext2)

        try {
            val json = PrefManager(context).getvalue(StaticData.folderData)
            val folderList: ArrayList<Folder> = if (json != null) {
                val type = object : TypeToken<ArrayList<Folder>>() {}.type
                Gson().fromJson(json, type)
            } else {
                ArrayList()
            }

            if (!folderList.isNullOrEmpty()){
                rvFolderList.visibility= View.VISIBLE
                yourListtext.visibility= View.VISIBLE
                rvFolderList.layoutManager = LinearLayoutManager(context)
                val adapter = FolderAdapter(folderList, object : FolderAdapter.OnFolderClickListener {
                    override fun onFolderClick(folder: Folder) {
                        onAddFolder(folder)
                        dialog.dismiss()
                    }
                })
                rvFolderList.adapter = adapter
            }else{
                rvFolderList.visibility= View.GONE
                yourListtext.visibility= View.GONE
            }

            createFolderButton.setOnClickListener(View.OnClickListener {
                addFolderView.visibility= View.VISIBLE
            })

            addButton.setOnClickListener {
                val folderNameText = folderName.text.toString().trim()

                if (folderNameText.isEmpty()) {
                    showCustomToast(context,StaticData.actionRequired,"Enter Folder Name",StaticData.alert)
                    return@setOnClickListener
                }

                val isFolderPresent = folderList.any { folder ->
                    folder.folder_name == folderNameText
                }

                if (isFolderPresent) {
                    showCustomToast(context,StaticData.actionRequired,"Folder Already Exists",StaticData.alert)
                } else {
                    // Add the folder if it doesn't exist
                    onAddFolder(Folder("", folderNameText, "", ""))
                    dialog.dismiss()
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations=R.style.BottomSheetDialogTheme
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    fun showCustomToast(context: Context,title: String, subMessage: String,type:String) {
        val inflater = LayoutInflater.from(context)
        val layout: View = inflater.inflate(R.layout.toast_message_dialog, null)

        val tvMessage = layout.findViewById<TextView>(R.id.tvTitle)
        val tvSubMessage = layout.findViewById<TextView>(R.id.tvmsg)
        val progressBar = layout.findViewById<View>(R.id.progress_line)
        val ivImage = layout.findViewById<View>(R.id.ivImage)

        tvMessage.text = title
        tvSubMessage.text = subMessage

        when(type){
            StaticData.success ->{
                ivImage.setBackgroundResource(R.drawable.right_success_icon)
                progressBar.setBackgroundResource(R.color.Green)
            }

            StaticData.close ->{
                ivImage.setBackgroundResource(R.drawable.close_icon)
                progressBar.setBackgroundResource(R.color.Red)
            }

            StaticData.alert ->{
                ivImage.setBackgroundResource(R.drawable.alert_icon)
                progressBar.setBackgroundResource(R.color.Yellow)
            }
        }

        val toast = Toast(context)
        toast.view = layout
        toast.duration = Toast.LENGTH_LONG
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 80)

        val animation = ObjectAnimator.ofFloat(progressBar, "scaleX", 0f, 1f)
        animation.duration = 1000
        animation.start()

        toast.show()
    }

}