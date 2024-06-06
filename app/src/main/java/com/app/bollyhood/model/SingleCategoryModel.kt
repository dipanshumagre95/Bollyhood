package com.app.bollyhood.model

data class SingleCategoryModel(
    val id: String,
    val name: String,
    val email: String,
    val mobile: String,
    val image: String,
    val description: String,
    val is_verify: String,
    val status: String,
    var is_bookmarked: Int,
    val category_name: String,
    val skin_color:String,
    val body_type:String,
    val location:String,
    val age:String,
    val height:String,
    val passport:String,
    val imagefile:ArrayList<String>,
    val work_links: ArrayList<WorkLinkProfileData>,
    val categories: ArrayList<ProfileCategoryModel>
)