package com.app.bollyhood.model


data class CastingCallResponse(
    val status: String,
    val msg: String,
    val result: ArrayList<CastingCallModel>
)

data class CastingCallModel(
    val id: String,
    val company_logo: String,
    val company_name: String,
    val organization: String,
    val shifting: String,
    val date: String,
    val description: String,
    val requirement: String,
    val skill: String,
    val pin_type: String,
    val role: String,
    val modify_date: String,
    val applyed_users:ArrayList<UserModel>,
    val apply_casting_count: String,
    val location: String,
    val document: String,
    val type: String,
    val price_discussed: String,
    val price: String,
    val price_type: String,
    val casting_fee_type: String,
    val create_date: String,
    val is_apply:Int,
    val is_casting_apply: Int,
    val is_verify_casting:String,
    val is_aadhar_verified:String,
    var is_casting_bookmark: Int
    )