package com.jesusdev.tarjetabipcompleta.viewmodel

import android.app.Application
import android.text.Editable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.jesusdev.tarjetabipcompleta.pojo.TarjetaBip
import com.jesusdev.tarjetabipcompleta.remote.RepoBip
import kotlinx.coroutines.launch

class ViewModelSaldo(application: Application) :AndroidViewModel(application) {

    private var repoBip: RepoBip
   val allTarjetabip : LiveData<TarjetaBip>

   init {
       repoBip = RepoBip()
       allTarjetabip = repoBip.liveDataSaldo
   }

    fun getSaldoById(id: String) = viewModelScope.launch{

        repoBip.getPhotoFromInternetRepo(id)

    }
    suspend fun getTarjeta(id :String ) {

        repoBip.getPhotoFromInternetRepo(id)
    }
}