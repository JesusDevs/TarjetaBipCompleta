package com.jesusdev.tarjetabipcompleta.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.jesusdev.tarjetabipcompleta.pojo.TarjetaBip

class RepoBip {

    private val retrofit = ClienteRetrofit.retrofitInstance()

    //variable live data
    val liveDataSaldo = MutableLiveData<TarjetaBip>()

    suspend fun getPhotoFromInternetRepo(id : String ){
        Log.d("REPOSITORY", "UTILIZANDO COROUTINES PARA OBTENER SALDO")
        val service = kotlin.runCatching { retrofit.getSaldoFromNet(id)}
        service.onSuccess {
            liveDataSaldo.value = it.body()
            Log.d("datos","${it.body()}")

        }
        service.onFailure {
            Log.d("ERROR", "$it")
        }}



}
