package com.app.bollyhood.model

data class SendMessageResponse(
    val status: String,
    val msg: String,
    val result: ChatModel
)

data class SendMessageModel(
    val id: String,
    val uid: String,
    val other_uid: String,
    val text: String,
    val image: String,
    val added_on: String,
    val user_type: Int
)