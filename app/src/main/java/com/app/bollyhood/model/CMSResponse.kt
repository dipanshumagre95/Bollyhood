package com.app.bollyhood.model

data class CMSResponse(
    val status:String,
    val msg:String,
    val result:ArrayList<CMSModel>
)
data class CMSModel(
    val type:String,
    val description:String
)
