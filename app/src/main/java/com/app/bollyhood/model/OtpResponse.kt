package com.app.bollyhood.model

data class OtpResponse(
    val status: String,
    val msg: String,
    val result: OtpModel
)

data class OtpModel(
    val otp: String,
    val image:String
)
