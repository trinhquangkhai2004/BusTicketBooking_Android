package com.khaiqueng_finalterm.busticketbooking.data.repository

import com.khaiqueng_finalterm.busticketbooking.data.model.TripDTO
import com.khaiqueng_finalterm.busticketbooking.network.KtorClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class TripRepository {
    private val client = KtorClient.client

    suspend fun searchTrips(fromId: Long, toId: Long, date: String): Result<List<TripDTO>> {
        return try {
            val response: List<TripDTO> = client.get("/api/trips/search") {
                parameter("from", fromId)
                parameter("to", toId)
                parameter("date", date)
            }.body()
            
            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
