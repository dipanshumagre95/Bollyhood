package com.app.bollyhood.model

data class BannerResponse(
    val status: String,
    val msg: String,
    val result: ArrayList<BannerModel>
)

data class BannerModel(
    val banner_image: String
)