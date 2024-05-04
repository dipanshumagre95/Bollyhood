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
    val mobile: String,
    val description: String,
    val reviews: String,
    val jobs_done: String,
    val experience: String,
    val work_links: ArrayList<WorkLinks>,
    val is_verify: String,
    val status: String,
    val category_type:String,
    var is_bookmarked: Int,
    val is_book: Int,
    val new_work_links: ArrayList<WorkLinks>,
    val categories: ArrayList<ExpertiseCategories>
)