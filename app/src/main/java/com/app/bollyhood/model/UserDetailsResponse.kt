package com.app.bollyhood.model

data class UserDetailsResponse(
    val status: String,
    val msg: String,
    val result: ArrayList<Folder>
)
