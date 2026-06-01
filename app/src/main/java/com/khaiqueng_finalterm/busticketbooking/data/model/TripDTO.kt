package com.khaiqueng_finalterm.busticketbooking.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TripDTO(
    val id: Long,
    val departureLocation: LocationDTO,
    val arrivalLocation: LocationDTO,
    val bus: BusDTO,
    val departureTime: String, // Dạng chuỗi ISO-8601 từ Backend
    val arrivalTime: String,
    val price: Double,
    val duration: String,
    val availableSeats: Int
)
