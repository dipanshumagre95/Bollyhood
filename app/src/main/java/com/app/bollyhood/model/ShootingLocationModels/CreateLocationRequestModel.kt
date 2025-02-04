package com.app.bollyhood.model.ShootingLocationModels

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part

data class CreateLocationRequestModel(
    @Part var uid:RequestBody?,
    @Part var locationId:RequestBody?,
    @Part var locationName:RequestBody?,
    @Part var locationDescription:RequestBody?,
    @Part var email:RequestBody?,
    @Part var parking:RequestBody?,
    @Part var location:RequestBody?,
    @Part var securityAmount:RequestBody?,
    @Part var shiftTime:RequestBody?,
    @Part var amount:RequestBody?,
    @Part var careTaker:RequestBody?,
    @Part var acCount: RequestBody?,
    @Part var locationImage: ArrayList<MultipartBody.Part>,
)
