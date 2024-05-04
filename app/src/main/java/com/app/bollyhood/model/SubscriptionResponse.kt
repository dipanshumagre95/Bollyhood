package com.app.bollyhood.model

data class SubscriptionResponse(
    val status: String,
    val msg: String,
    val result: SubscriptionModel

)

data class SubscriptionModel(
    val is_subscription: String
)