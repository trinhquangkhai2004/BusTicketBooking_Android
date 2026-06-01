package com.khaiqueng_finalterm.busticketbooking.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseAuthRequest(
    val email: String,
    val password: String,
    val returnSecureToken: Boolean = true
)

@Serializable
data class FirebaseUpdateProfileRequest(
    val idToken: String,
    val displayName: String,
    val returnSecureToken: Boolean = true
)

@Serializable
data class FirebaseAuthResponse(
    val idToken: String? = null,
    val email: String? = null,
    val displayName: String? = null,
    val localId: String? = null
)

@Serializable
data class BackendAuthRequest(
    val idToken: String,
    val displayName: String? = null
)

@Serializable
data class BackendAuthResponse(
    val userId: Long,
    val firebaseUid: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val role: String,
    val newUser: Boolean
)

@Serializable
data class ApiErrorResponse(
    val status: Int? = null,
    val error: String? = null,
    val message: String? = null
)

@Serializable
data class TokenDebugRequest(
    val idToken: String
)

@Serializable
data class TokenDebugResponse(
    val tokenLength: Int? = null,
    val segmentCount: Int? = null,
    val segmentLengths: String? = null,
    val headerAlgorithm: String? = null,
    val headerKeyId: String? = null,
    val tokenAudience: String? = null,
    val tokenIssuer: String? = null,
    val expectedProjectId: String? = null,
    val projectMatches: Boolean = false
)

@Serializable
data class FirebaseErrorResponse(
    val error: FirebaseErrorDetail? = null
)

@Serializable
data class FirebaseErrorDetail(
    val message: String? = null,
    @SerialName("code")
    val code: Int? = null
)
