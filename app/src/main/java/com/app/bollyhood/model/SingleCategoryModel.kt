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
    val events: String,
    val dancer_form: String,
    val what_i_do: String,
    val tag_name: String,
    var is_bookmarked: Int,
    val category_name: String,
    val skin_color:String,
    val body_type:String,
    val location:String,
    val age:String,
    val height:String,
    val passport:String,
    val averageviews:String,
    val followers:String,
    val platforms:String,
    val skills:String,
    val softwares:String,
    val available:String,
    val imagefile:ArrayList<String>,
    val work_links: ArrayList<WorkLinkProfileData>,
    val videos_url: ArrayList<VideoLink>,
    val categories: ArrayList<ProfileCategoryModel>
)