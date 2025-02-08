package com.app.bollyhood.model

data class ShootLocationBookingResponse(
    val status: String,
    val msg: String,
    val result:ShootLocationBookingLists
)

data class ShootLocationBookingLists(
    val location_name_list:ArrayList<ShootLocationNameModel>,
    val location_booking_list:ArrayList<ShootLocationBookingList>
)

data class ShootLocationNameModel(
    val location_id:String,
    val property_name:String,
    var isSelected:String="0",
)

data class ShootLocationBookingList(
    val id:String,
    val uid: String,
    val location_id: String,
    val property_location: String,
    val name: String,
    val email: String,
    val mobile: String,
    val property_name:String,
    val booking_reason: String,
    val booking_date: String,
    val image:String,
    val start_booking_time: String,
    val end_booking_time: String,
    val created_at: String,
    val modify_at: String
)




