package com.app.bollyhood.model

data class BookingResponse(
    val status: String,
    val msg: String,
    val result: ArrayList<BookingModel>

)

data class BookingModel(
    val property_name:String,
    val property_location:String,
    val booking_date:String,
    val start_booking_time:String,
    val end_booking_time:String,
    val status:String,
    val name:String,
    val image:String,
    val phone:String,
    val email:String
)
