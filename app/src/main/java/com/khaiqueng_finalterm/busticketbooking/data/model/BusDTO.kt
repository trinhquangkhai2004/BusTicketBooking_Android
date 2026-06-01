package com.khaiqueng_finalterm.busticketbooking.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BusDTO(
    val id: Long,
    val licensePlate: String,
    val type: String,
    val totalSeats: Int
)
