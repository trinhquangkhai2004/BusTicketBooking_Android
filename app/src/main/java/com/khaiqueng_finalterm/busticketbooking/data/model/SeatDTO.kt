package com.khaiqueng_finalterm.busticketbooking.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SeatDTO(
    val id: Long,
    val seatNumber: String,
    val isBooked: Boolean
)
