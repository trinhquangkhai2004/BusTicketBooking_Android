package com.khaiqueng_finalterm.busticketbooking.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LocationDTO(
    val id: Long,
    val name: String
)
