package com.app.bollyhood.model


data class CastingBookMarkResponse(
    val status: String,
    val msg: String,
    val result: ArrayList<CastingBookMarkModel>
)

data class CastingBookMarkModel(
    val id: String,
    val company_logo: String,
    val company_name: String,
    val organization: String,
    val shifting: String,
    val date: String,
    val description: String,
    val requirement: String,
    val skill: String,
    val role: String,
    val location: String,
    val document: String,
    val type: String,
    val price_discussed: String,
    val price: String,
    val is_casting_apply: Int,
    val is_casting_bookmark: Int

)