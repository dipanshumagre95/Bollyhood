package com.app.bollyhood.model

data class LoginResponse(
    val status: String,
    val msg: String,
    val result: LoginModel

)

data class LoginModel(
    val id: String,
    val name: String,
    val email: String,
    val mobile: String,
    val user_type: String,
    val image: String,
    val status: String,
    val is_verify: String,
    val categories: ArrayList<CategoryModel>,
    val sub_categories: ArrayList<SubcategoryModel>
)
