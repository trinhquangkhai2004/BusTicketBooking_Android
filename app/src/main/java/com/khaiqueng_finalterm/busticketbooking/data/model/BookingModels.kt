package com.khaiqueng_finalterm.busticketbooking.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BookingRequestDTO(
    val userId: Long,
    val tripId: Long,
    val seatIds: List<Long>,
    val extraServicesAmount: Double = 0.0
)

@Serializable
data class BookingResponseDTO(
    val id: Long,
    val tripRoute: String,
    val departureTime: String,
    val totalAmount: Double,
    val status: String
)

@Serializable
data class VnpayPaymentRequestDTO(
    val bookingId: Long,
    val bankCode: String? = null,
    val locale: String = "vn"
)

@Serializable
data class VnpayPaymentResponseDTO(
    val bookingId: Long,
    val paymentId: Long,
    val paymentUrl: String,
    val amount: Double,
    val expiresAt: String
)

@Serializable
data class PaymentStatusResponseDTO(
    val bookingId: Long,
    val bookingStatus: String,
    val paymentStatus: String? = null,
    val paid: Boolean
)
