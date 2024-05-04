package com.app.bollyhood.model

data class ChatResponse(
    val status: String,
    val msg: String,
    val result: ArrayList<ChatModel>
)

data class ChatModel(
    val id: String,
    val uid: String,
    val other_uid: String,
    val text: String,
    val image: String,
    val added_on: String,
    val user_type: String
)