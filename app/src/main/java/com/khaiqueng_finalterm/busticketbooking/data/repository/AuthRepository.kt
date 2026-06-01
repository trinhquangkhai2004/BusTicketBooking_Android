package com.khaiqueng_finalterm.busticketbooking.data.repository

import com.khaiqueng_finalterm.busticketbooking.BuildConfig
import com.khaiqueng_finalterm.busticketbooking.data.model.ApiErrorResponse
import com.khaiqueng_finalterm.busticketbooking.data.model.BackendAuthRequest
import com.khaiqueng_finalterm.busticketbooking.data.model.BackendAuthResponse
import com.khaiqueng_finalterm.busticketbooking.data.model.FirebaseAuthRequest
import com.khaiqueng_finalterm.busticketbooking.data.model.FirebaseAuthResponse
import com.khaiqueng_finalterm.busticketbooking.data.model.FirebaseErrorResponse
import com.khaiqueng_finalterm.busticketbooking.data.model.FirebaseUpdateProfileRequest
import com.khaiqueng_finalterm.busticketbooking.data.model.TokenDebugRequest
import com.khaiqueng_finalterm.busticketbooking.data.model.TokenDebugResponse
import com.khaiqueng_finalterm.busticketbooking.network.KtorClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AuthRepository {
    private val backendClient = KtorClient.client
    private val firebaseClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }
        install(io.ktor.client.plugins.HttpTimeout) {
            requestTimeoutMillis = 15000L
            connectTimeoutMillis = 15000L
            socketTimeoutMillis = 15000L
        }
    }

    suspend fun login(email: String, password: String): Result<BackendAuthResponse> {
        return authenticateWithFirebase(
            endpoint = "signInWithPassword",
            firebaseRequest = FirebaseAuthRequest(email = email, password = password)
        )
    }

    suspend fun register(fullName: String, email: String, password: String): Result<BackendAuthResponse> {
        return runCatching {
            ensureFirebaseApiKey()

            val signUpResponse = firebaseClient.post(firebaseAuthUrl("signUp")) {
                contentType(ContentType.Application.Json)
                setBody(FirebaseAuthRequest(email = email, password = password))
            }
            if (!signUpResponse.status.isSuccess()) {
                throw IllegalStateException(parseFirebaseError(signUpResponse))
            }

            val createdAccount = signUpResponse.body<FirebaseAuthResponse>()
            val idToken = createdAccount.idToken
                ?: throw IllegalStateException("Firebase did not return an ID token")

            val updateResponse = firebaseClient.post(firebaseAuthUrl("update")) {
                contentType(ContentType.Application.Json)
                setBody(
                    FirebaseUpdateProfileRequest(
                        idToken = idToken,
                        displayName = fullName.trim()
                    )
                )
            }
            if (!updateResponse.status.isSuccess()) {
                throw IllegalStateException(parseFirebaseError(updateResponse))
            }

            syncWithBackend(idToken.trim(), fullName.trim())
        }
    }

    private suspend fun authenticateWithFirebase(
        endpoint: String,
        firebaseRequest: FirebaseAuthRequest
    ): Result<BackendAuthResponse> {
        return runCatching {
            ensureFirebaseApiKey()

            val firebaseResponse = firebaseClient.post(firebaseAuthUrl(endpoint)) {
                contentType(ContentType.Application.Json)
                setBody(firebaseRequest)
            }
            if (!firebaseResponse.status.isSuccess()) {
                throw IllegalStateException(parseFirebaseError(firebaseResponse))
            }

            val authResponse = firebaseResponse.body<FirebaseAuthResponse>()
            val idToken = authResponse.idToken
                ?: throw IllegalStateException("Firebase did not return an ID token")
            syncWithBackend(idToken.trim())
        }
    }

    private suspend fun syncWithBackend(idToken: String, displayName: String? = null): BackendAuthResponse {
        val normalizedToken = idToken.trim()
        val response = backendClient.post("/api/auth/firebase") {
            setBody(BackendAuthRequest(idToken = normalizedToken, displayName = displayName))
        }

        if (!response.status.isSuccess()) {
            val backendError = parseBackendError(response)
            val tokenDebug = inspectBackendToken(normalizedToken)
            throw IllegalStateException(
                if (tokenDebug == null) {
                    backendError
                } else {
                    "$backendError | tokenAudience=${tokenDebug.tokenAudience}, expected=${tokenDebug.expectedProjectId}, matches=${tokenDebug.projectMatches}, segments=${tokenDebug.segmentCount}, lengths=${tokenDebug.segmentLengths}, alg=${tokenDebug.headerAlgorithm}, kid=${tokenDebug.headerKeyId != null}"
                }
            )
        }

        AuthSession.idToken = normalizedToken
        return response.body()
    }

    private suspend fun inspectBackendToken(idToken: String): TokenDebugResponse? {
        return runCatching {
            backendClient.post("/api/auth/debug-token") {
                setBody(TokenDebugRequest(idToken = idToken))
            }.body<TokenDebugResponse>()
        }.getOrNull()
    }

    private fun ensureFirebaseApiKey() {
        if (BuildConfig.FIREBASE_WEB_API_KEY.isBlank()) {
            throw IllegalStateException("Missing firebase.web.api.key in local.properties")
        }
    }

    private fun firebaseAuthUrl(endpoint: String): String {
        return "https://identitytoolkit.googleapis.com/v1/accounts:$endpoint?key=${BuildConfig.FIREBASE_WEB_API_KEY}"
    }

    private suspend fun parseFirebaseError(response: HttpResponse): String {
        val text = response.bodyAsText()
        return runCatching {
            json.decodeFromString<FirebaseErrorResponse>(text).error?.message
        }.getOrNull()?.let(::mapFirebaseError) ?: "Firebase authentication failed"
    }

    private suspend fun parseBackendError(response: HttpResponse): String {
        val text = response.bodyAsText()
        return runCatching {
            json.decodeFromString<ApiErrorResponse>(text).message
        }.getOrNull() ?: "Backend authentication failed"
    }

    private fun mapFirebaseError(message: String): String {
        return when (message) {
            "EMAIL_EXISTS" -> "Email này đã được đăng ký"
            "EMAIL_NOT_FOUND", "INVALID_PASSWORD", "INVALID_LOGIN_CREDENTIALS" -> "Email hoặc mật khẩu không đúng"
            "INVALID_EMAIL" -> "Email không hợp lệ"
            "WEAK_PASSWORD : Password should be at least 6 characters" -> "Mật khẩu phải có ít nhất 6 ký tự"
            else -> message
        }
    }

    private companion object {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }
}

object AuthSession {
    var idToken: String? = null
    var user: BackendAuthResponse? = null
}
