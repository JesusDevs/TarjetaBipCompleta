package com.jesusdev.tarjetabipcompleta.remote

import com.jesusdev.tarjetabipcompleta.pojo.TarjetaBip
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IRetrofitService {

    @GET("solicitudes.json")
    suspend fun getSaldoFromNet(@Query("bip") id:String) : Response<TarjetaBip>

}