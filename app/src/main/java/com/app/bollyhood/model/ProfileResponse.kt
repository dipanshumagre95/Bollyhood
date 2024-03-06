package com.app.bollyhood.model

data class ProfileResponse(
    val status: String,
    val msg: String,
    val result: ProfileModel
)

data class ProfileModel(
    val id: String,
    val name: String,
    val email: String,
    val mobile: String,
    val image: String,
    val is_verify: String
)