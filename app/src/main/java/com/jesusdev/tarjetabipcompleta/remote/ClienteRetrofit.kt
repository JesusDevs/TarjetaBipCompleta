package com.jesusdev.tarjetabipcompleta.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ClienteRetrofit
{

    companion object{
        const val BASE_URL = "http://bip-servicio.herokuapp.com/api/v1/"
        fun retrofitInstance(): IRetrofitService {
            val retrofitClient = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofitClient.create(IRetrofitService::class.java)
        }
    }
}