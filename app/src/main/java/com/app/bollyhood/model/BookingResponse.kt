package com.app.bollyhood.model

data class BookingResponse(
    val status: String,
    val msg: String,
    val result: ArrayList<BookingModel>

)

data class BookingModel(
    val id: String,
    val name: String,
    val email:String,
    val image: String,
    val mobile: String,
    val description: String,
    val reviews: String,
    val jobs_done: String,
    val experience: String,
    val work_links: ArrayList<WorkLinkProfileData>,
    val is_verify: String,
    val status: String,
    val catt:String,
    var is_bookmarked: Int,
    val is_book: Int,
    val new_work_links: ArrayList<WorkLinkProfileData>,
    val categories: ArrayList<ExpertiseCategories>
)
