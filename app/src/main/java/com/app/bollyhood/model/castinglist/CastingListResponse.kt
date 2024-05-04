package com.app.bollyhood.model.castinglist

data class CastingListResponse(
    val status:String,
    val msg:String,
    val result:ArrayList<CastingUserData>
)
data class CastingUserData(
    val id:String,
    val name:String,
    val email:String,
    val image:String,
    val is_verify:String,
    val mobile:String,
    val casting_apply:ArrayList<CastingDataModel>
)
data class CastingDataModel(

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
    val is_casting_bookmark: Int,
    val images:ArrayList<String>
)