package com.app.bollyhood.model.ShootingLocationModels

data class ShootLocationListResponseModel(
    val status: String,
    val msg: String,
    val result:ArrayList<ShootLocationModel>
)

data class ShootLocationModel(
    val locationId:String,
    val locationName:String,
    val locationDescription:String,
    val email:String,
    val parking:String,
    val location:String,
    val securityAmount:String,
    val shiftTime:String,
    val rating:String,
    val amount:String,
    val careTaker:String,
    val acCount:String,
    val managerimage:String,
    val locationImage:ArrayList<String>,
    val managerId:String,
    val managerName:String,
    val managerProfileName:String,
)

data class ShootLocationResponseModel(
    val status: String,
    val msg: String,
    val result:ShootLocationModel
)