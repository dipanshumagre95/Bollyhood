package com.app.bollyhood.model

data class ChatResponse(
    val status: String,
    val msg: String,
    val sender_details: SenderDetails,
    val result: ArrayList<ChatModel>
)

data class SenderDetails(
    val uid:String,
    val name:String,
    val image:String,
    val is_online:String,
    val mobile:String
)

data class ChatModel(
    val id: String,
    val uid: String,
    val other_uid: String,
    val text: String,
    val image: String,
    val added_on: String,
    val user_type: Int
)