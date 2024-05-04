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
    val description: String,
    val jobs_done: String,
    val experience: String,
    val is_verify: String,
    val work_links: ArrayList<WorkLinkProfileData>,
    val categories: ArrayList<ProfileCategoryModel>
)

data class ProfileCategoryModel(
    val category_id: String,
    val category_name: String,
    val type: String,
    val cat_image: String
)

data class WorkLinkProfileData(
    val worklink_name: String,
    val worklink_url: String
)