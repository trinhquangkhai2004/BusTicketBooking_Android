package com.khaiqueng_finalterm.busticketbooking.data.repository

import com.khaiqueng_finalterm.busticketbooking.data.model.BookingRequestDTO
import com.khaiqueng_finalterm.busticketbooking.data.model.BookingResponseDTO
import com.khaiqueng_finalterm.busticketbooking.data.model.ApiErrorResponse
import com.khaiqueng_finalterm.busticketbooking.data.model.PaymentStatusResponseDTO
import com.khaiqueng_finalterm.busticketbooking.data.model.SeatDTO
import com.khaiqueng_finalterm.busticketbooking.data.model.TripDTO
import com.khaiqueng_finalterm.busticketbooking.data.model.VnpayPaymentRequestDTO
import com.khaiqueng_finalterm.busticketbooking.data.model.VnpayPaymentResponseDTO
import com.khaiqueng_finalterm.busticketbooking.network.KtorClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class BookingRepository {
    private val client = KtorClient.client

    suspend fun getSeatsForTrip(tripId: Long): Result<List<SeatDTO>> {
        return try {
            val response = client.get("/api/trips/$tripId/seats")
            if (!response.status.isSuccess()) {
                throw IllegalStateException(parseBackendError(response, "Không thể tải sơ đồ ghế"))
            }
            Result.success(response.body())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun createBooking(request: BookingRequestDTO): Result<BookingResponseDTO> {
        return try {
            val response = client.post("/api/bookings") {
                setBody(request)
            }
            if (!response.status.isSuccess()) {
                throw IllegalStateException(parseBackendError(response, "Không thể tạo booking"))
            }
            Result.success(response.body())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getBookingsForUser(userId: Long): Result<List<BookingResponseDTO>> {
        return try {
            val response = client.get("/api/users/$userId/bookings")
            if (!response.status.isSuccess()) {
                throw IllegalStateException(parseBackendError(response, "Không thể tải danh sách vé"))
            }
            Result.success(response.body())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    suspend fun createVnpayPayment(request: VnpayPaymentRequestDTO): Result<VnpayPaymentResponseDTO> {
        return try {
            val response = client.post("/api/payments/vnpay") {
                setBody(request)
            }
            if (!response.status.isSuccess()) {
                throw IllegalStateException(parseBackendError(response, "Không thể khởi tạo thanh toán VNPay"))
            }
            Result.success(response.body())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getPaymentStatus(bookingId: Long): Result<PaymentStatusResponseDTO> {
        return try {
            val response = client.get("/api/payments/bookings/$bookingId/status")
            if (!response.status.isSuccess()) {
                throw IllegalStateException(parseBackendError(response, "Không thể kiểm tra trạng thái thanh toán"))
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

object BookingSession {
    var selectedTrip: TripDTO? = null
    var selectedSeatIds: List<Long> = emptyList()
    var selectedSeatNumbers: List<String> = emptyList()
    
    // Lưu trạng thái các dịch vụ thêm đã chọn ở Checkout
    var hasLuggage: Boolean = false
    var hasInsurance: Boolean = false
    var hasMeal: Boolean = false
    var hasPickup: Boolean = false

    var lastBookingResponse: BookingResponseDTO? = null

    fun getExtraServicesPrice(): Double {
        var extraPrice = 0.0
        if (hasLuggage) extraPrice += 20000.0
        if (hasInsurance) extraPrice += 15000.0
        if (hasMeal) extraPrice += 35000.0
        if (hasPickup) extraPrice += 50000.0
        return extraPrice
    }

    fun clear() {
        selectedTrip = null
        selectedSeatIds = emptyList()
        selectedSeatNumbers = emptyList()
        hasLuggage = false
        hasInsurance = false
        hasMeal = false
        hasPickup = false
        lastBookingResponse = null
    }
}
