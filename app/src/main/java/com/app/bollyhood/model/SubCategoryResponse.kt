package com.app.bollyhood.model

data class SubCategoryResponse(
    val status: String,
    val msg: String,
    val result: ArrayList<SubcategoryModel>

)

data class SubcategoryModel(
    val sub_cat_id: String,
    val category_id: String,
    val sub_cat_name: String
)
