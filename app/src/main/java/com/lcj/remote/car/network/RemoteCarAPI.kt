package com.lcj.remote.car.network

import android.content.Context
import com.google.gson.JsonObject
import com.lcj.remote.car.BuildConfig
import com.lcj.remote.car.model.ControlResponse
import com.lcj.remote.car.network.service.RemoteCarAPIService
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RemoteCarAPI {
    private lateinit var retrofit: Retrofit
    private lateinit var service: RemoteCarAPIService

    fun init(context: Context, serverIP: String) {
        retrofit = Retrofit.Builder().baseUrl("http://$serverIP:5000")
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(OkHttpClient.Builder().apply {
                hostnameVerifier { _, _ -> true }
                connectTimeout(30, TimeUnit.SECONDS)
                readTimeout(30, TimeUnit.SECONDS)
                if (BuildConfig.DEBUG) addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }.build())
            .build()
        service = retrofit.create(RemoteCarAPIService::class.java)
    }

    fun status(): Single<ControlResponse> {
        return service.status().subscribeOn(Schedulers.io())
    }

    fun motor(cmd: String, value: Int): Single<ControlResponse> {
        val body = JsonObject().apply {
            addProperty("cmd", cmd)
            addProperty("value", value)
        }
        return service.motor(body).subscribeOn(Schedulers.io())
    }

    fun light(cmd: String, value: Int): Single<ControlResponse> {
        val body = JsonObject().apply {
            addProperty("cmd", cmd)
            addProperty("value", value)
        }
        return service.light(body).subscribeOn(Schedulers.io())
    }

    fun sound(cmd: String, value: Int): Single<ControlResponse> {
        val body = JsonObject().apply {
            addProperty("cmd", cmd)
            addProperty("value", value)
        }
        return service.sound(body).subscribeOn(Schedulers.io())
    }
}