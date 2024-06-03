package com.app.bollyhood.model.actors

import com.app.bollyhood.model.SingleCategoryModel

data class ActorsresponseModel(val status: String,
                               val msg: String,
                               val result: ArrayList<SingleCategoryModel>)