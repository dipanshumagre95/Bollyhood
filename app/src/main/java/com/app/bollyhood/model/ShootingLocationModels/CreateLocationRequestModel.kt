package com.app.bollyhood.model.ShootingLocationModels

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part

data class CreateLocationRequestModel(
    @Part var uid:RequestBody?,
    @Part var locationId:RequestBody?,
    @Part var property_name:RequestBody?,
    @Part var description:RequestBody?,
    @Part var phone:RequestBody?,
    @Part var email:RequestBody?,
    @Part var parking:RequestBody?,
    @Part var location:RequestBody?,
    @Part var security_deposit:RequestBody?,
    @Part var shift_type:RequestBody?,
    @Part var amount:RequestBody?,
    @Part var care_taker:RequestBody?,
    @Part var air_conditioner: RequestBody?,
    @Part var images: ArrayList<MultipartBody.Part>,
)
