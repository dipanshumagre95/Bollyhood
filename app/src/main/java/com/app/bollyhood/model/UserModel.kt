package com.app.bollyhood.model

data class UserModel(
    val apply_create_date: String,
    val apply_images: ArrayList<String>,
    val id: String,
    val image: String,
    val name: String,
    val is_bookmarked: Int,
    val videoUrl: String
)