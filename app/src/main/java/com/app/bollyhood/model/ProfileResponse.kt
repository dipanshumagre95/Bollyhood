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
    val is_verify: String,
    val status: String,
    val collaborate: String,
    val promotion: String,
    val average_like: String,
    val average_reel_like: String,
    val instagram_link: String,
    val facebook_link: String,
    val youtube_link: String,
    val achievements: String,
    val languages: String,
    val genre: String,
    val dancer_form: String,
    val what_i_do: String,
    val events: String,
    var is_bookmarked: Int,
    val category_name: String,
    val skin_color:String,
    val body_type:String,
    val location:String,
    val tag_name:String,
    val age:String,
    val height:String,
    val passport:String,
    val imagefile:ArrayList<String>,
    val work_links: ArrayList<WorkLinkProfileData>,
    val videos_url: ArrayList<VideoLink>,
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
    val worklink_url: String,
)