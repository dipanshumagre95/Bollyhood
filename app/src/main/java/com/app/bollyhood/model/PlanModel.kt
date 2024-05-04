package com.app.bollyhood.model

data class PlanModel(
    val plan_id: String,
    val price: String,
    val type: String,
    val title: String,
    val description: String
)

data class PlanResponse(
    val status: String,
    val msg: String,
    val result: ArrayList<PlanModel>
)