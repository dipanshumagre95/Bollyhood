package com.app.bollyhood.model

data class ExpertiseResponse(
    val status: String,
    val msg: String,
    val result: ArrayList<ExpertiseModel>
)

data class ExpertiseModel(
    val id: String,
    val name: String,
    val email: String,
    val image: String,
    val mobile: String,
    val description: String,
    val reviews: String,
    val jobs_done: String,
    val experience: String,
    val category_type: String,
    val work_links: ArrayList<WorkLinkProfileData>,
    val is_verify: String,
    val status: String,
    val catt: String,
    var is_bookmarked: Int,
    var is_online: String,
    val is_book: Int,
    val last_message:String,
    val last_time:String,
    val new_work_links: ArrayList<WorkLinkProfileData>,
    val categories: ArrayList<ExpertiseCategories>
)


data class WorkLinks(
    val worklink_name: String,
    val worklink_url: String
)

data class ExpertiseCategories(
    val category_name: String
)