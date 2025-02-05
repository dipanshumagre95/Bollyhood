package com.app.bollyhood.model.ShootingLocationModels

data class ShootLocationListResponseModel(
    val status: String,
    val msg: String,
    val result:ArrayList<ShootLocationModel>
)

data class ShootLocationModel(
    val locationId:String,
    val property_name:String,
    val description:String,
    val email:String,
    val phone:String,
    val parking:String,
    val property_location:String,
    val security_deposit:String,
    val shift_type:String,
    val rating:String,
    val amount:String,
    val care_taker:String,
    val air_conditioner:String,
    val managerimage:String,
    val images:ArrayList<String>,
    val managerId:String,
    val managerName:String,
    val managerProfileName:String,
)

data class ShootLocationResponseModel(
    val status: String,
    val msg: String,
    val result:ShootLocationModel
)