package com.app.bollyhood.model

data class BookingResponse(
    val status: String,
    val msg: String,
    val result: ArrayList<BookingModel>

)

data class BookingModel(
    val id:String,
    val property_name:String,
    val property_location:String,
    val booking_date:String,
    val start_booking_time:String,
    val end_booking_time:String,
    val status:String,
    val name:String,
    val email:String,
    val mobile:String,
    val user_image:String
)
