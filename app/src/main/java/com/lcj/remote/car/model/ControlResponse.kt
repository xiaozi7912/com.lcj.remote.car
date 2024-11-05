package com.lcj.remote.car.model

import com.google.gson.annotations.SerializedName

data class ControlResponse(
    val code: Int,
    val direction: Int,
    @SerializedName("light_status")
    val light: Int,
    val message: String
)
