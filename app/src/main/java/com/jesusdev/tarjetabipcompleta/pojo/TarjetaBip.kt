package com.jesusdev.tarjetabipcompleta.pojo


import com.google.gson.annotations.SerializedName

data class TarjetaBip(
    @SerializedName("estadoContrato")
    val estadoContrato: String,
    @SerializedName("fechaSaldo")
    val fechaSaldo: String,
    @SerializedName("id")

    val id: String,
    @SerializedName("saldoTarjeta")
    val saldoTarjeta: String
)