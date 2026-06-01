package com.khaiqueng_finalterm.busticketbooking.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatbotMessageRequestDTO(
    val message: String
)

@Serializable
data class ChatbotMessageResponseDTO(
    val reply: String
)
