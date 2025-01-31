package com.app.bollyhood.model

data class BookMarkResponse(
    val status: String,
    val msg: String,
    val result: ArrayList<BookMarkModel>
)

data class BookMarkModel(
    val id: String,
    val name: String,
    val email:String,
    val image: String,
    val categories: String,
    val bookmark_time: String,
    val category_name: String,
)