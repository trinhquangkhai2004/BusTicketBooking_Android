package com.khaiqueng_finalterm.busticketbooking.data.repository

import com.khaiqueng_finalterm.busticketbooking.data.model.ApiErrorResponse
import com.khaiqueng_finalterm.busticketbooking.data.model.ChatbotMessageRequestDTO
import com.khaiqueng_finalterm.busticketbooking.data.model.ChatbotMessageResponseDTO
import com.khaiqueng_finalterm.busticketbooking.network.KtorClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class ChatbotRepository {
    private val client = KtorClient.client

    suspend fun sendMessage(message: String): Result<ChatbotMessageResponseDTO> {
        return try {
            val response = client.post("/api/chatbot/message") {
                setBody(ChatbotMessageRequestDTO(message))
            }
            if (!response.status.isSuccess()) {
                throw IllegalStateException(parseBackendError(response, "Không thể gửi tin nhắn"))
            }
            Result.success(response.body())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private suspend fun parseBackendError(response: HttpResponse, fallbackMessage: String): String {
        val text = response.bodyAsText()
        return runCatching {
            json.decodeFromString<ApiErrorResponse>(text).message
        }.getOrNull()?.takeIf { it.isNotBlank() }
            ?: "$fallbackMessage (${response.status.value})"
    }

    private companion object {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }
}
