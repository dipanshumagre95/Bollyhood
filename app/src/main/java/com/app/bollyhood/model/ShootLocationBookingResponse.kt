package com.app.bollyhood.model

data class ShootLocationBookingResponse(
    val status: String,
    val msg: String,
    val result:ShootLocationBookingLists
)

data class ShootLocationBookingLists(
    val locationNameList:ArrayList<ShootLocationNameModel>,
    val locationBookingList:ArrayList<ShootLocationBookingList>
)

data class ShootLocationNameModel(
    val location_id:String,
    val name:String,
    var isSelected:String,
)

data class ShootLocationBookingList(
    val uid: String,
    val location_id: String,
    val name: String,
    val email: String,
    val mobile: String,
    val locationName:String,
    val booking_reason: String,
    val booking_date: String,
    val image:String,
    val start_booking_time: String,
    val end_booking_time: String
)




