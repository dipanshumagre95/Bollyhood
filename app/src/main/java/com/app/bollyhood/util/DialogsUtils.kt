package com.app.bollyhood.util

import android.app.Dialog
import android.content.Context
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.app.bollyhood.R
import com.app.bollyhood.model.WorkLinkProfileData

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

        // Pre-fill values if available
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
                Toast.makeText(context, "Add at least one work link", Toast.LENGTH_SHORT).show()
            }
        }

        dialogView.show()
    }

    private fun isValidYouTubeUrl(url: String): Boolean {
        val youtubeRegex = "^(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+\$".toRegex()
        return youtubeRegex.matches(url)
    }

}