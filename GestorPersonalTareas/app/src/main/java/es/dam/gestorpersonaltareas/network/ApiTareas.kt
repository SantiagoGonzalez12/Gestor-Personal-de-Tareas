package es.dam.gestorpersonaltareas.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiTareas {
    @GET("todos")
    suspend fun listar(@Query("userId") usuario: Int): List<TareaRemota>
}