package com.khaiqueng_finalterm.busticketbooking.network

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object KtorClient {
    val client = HttpClient(Android) {
        // Cấu hình Base URL mặc định
        defaultRequest {
            url(NetworkConfig.BASE_URL)
            contentType(ContentType.Application.Json)
        }

        // Cấu hình tự động Parse JSON sang Data Class
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true // Bỏ qua các field thừa từ Backend trả về
            })
        }
    }
}
