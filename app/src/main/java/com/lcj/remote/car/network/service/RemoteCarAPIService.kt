package com.lcj.remote.car.network.service

import com.google.gson.JsonObject
import com.lcj.remote.car.model.ControlResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface RemoteCarAPIService {
    @GET("status")
    fun status(): Single<ControlResponse>

    @Headers("Content-Type: application/json")
    @POST("motor")
    fun motor(@Body body: JsonObject): Single<ControlResponse>

    @Headers("Content-Type: application/json")
    @POST("light")
    fun light(@Body body: JsonObject): Single<ControlResponse>

    @Headers("Content-Type: application/json")
    @POST("sound")
    fun sound(@Body body: JsonObject): Single<ControlResponse>
}