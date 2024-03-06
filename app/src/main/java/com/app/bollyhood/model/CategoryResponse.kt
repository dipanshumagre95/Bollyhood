package com.app.bollyhood.model

data class CategoryResponse(
    val status: String,
    val msg: String,
    val result: ArrayList<CategoryModel>
)

data class CategoryModel(
    val id: String,
    val category_name: String
)