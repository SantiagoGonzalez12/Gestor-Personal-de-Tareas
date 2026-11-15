package es.dam.gestorpersonaltareas.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServicioTareas {
    val api: ApiTareas = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiTareas::class.java)
}