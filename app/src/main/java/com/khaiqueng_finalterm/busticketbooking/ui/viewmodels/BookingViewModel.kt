package com.khaiqueng_finalterm.busticketbooking.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khaiqueng_finalterm.busticketbooking.data.model.BookingRequestDTO
import com.khaiqueng_finalterm.busticketbooking.data.model.BookingResponseDTO
import com.khaiqueng_finalterm.busticketbooking.data.model.SeatDTO
import com.khaiqueng_finalterm.busticketbooking.data.model.VnpayPaymentRequestDTO
import com.khaiqueng_finalterm.busticketbooking.data.model.VnpayPaymentResponseDTO
import com.khaiqueng_finalterm.busticketbooking.data.repository.AuthSession
import com.khaiqueng_finalterm.busticketbooking.data.repository.BookingRepository
import com.khaiqueng_finalterm.busticketbooking.data.repository.BookingSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SeatSelectionUiState {
    object Loading : SeatSelectionUiState()
    data class Success(val seats: List<SeatDTO>) : SeatSelectionUiState()
    data class Error(val message: String) : SeatSelectionUiState()
}

sealed class PaymentUiState {
    object Idle : PaymentUiState()
    object Loading : PaymentUiState()
    data class Success(val booking: BookingResponseDTO) : PaymentUiState()
    data class VnpayReady(val payment: VnpayPaymentResponseDTO) : PaymentUiState()
    data class Error(val message: String) : PaymentUiState()
}

class BookingViewModel : ViewModel() {
    private val repository = BookingRepository()

    private val _seatUiState = MutableStateFlow<SeatSelectionUiState>(SeatSelectionUiState.Loading)
    val seatUiState: StateFlow<SeatSelectionUiState> = _seatUiState.asStateFlow()

    private val _paymentUiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val paymentUiState: StateFlow<PaymentUiState> = _paymentUiState.asStateFlow()
    private var paymentPollingJob: Job? = null

    fun loadSeats(tripId: Long) {
        viewModelScope.launch {
            _seatUiState.value = SeatSelectionUiState.Loading
            val result = repository.getSeatsForTrip(tripId)
            result.onSuccess { seats ->
                _seatUiState.value = SeatSelectionUiState.Success(seats)
            }.onFailure { exception ->
                _seatUiState.value = SeatSelectionUiState.Error(exception.message ?: "Không thể tải sơ đồ ghế")
            }
        }
    }

    fun executeBooking(
        tripId: Long,
        seatIds: List<Long>,
        extraServicesAmount: Double = 0.0,
        onCompleted: () -> Unit
    ) {
        val userId = AuthSession.user?.userId ?: return
        viewModelScope.launch {
            _paymentUiState.value = PaymentUiState.Loading
            val request = BookingRequestDTO(
                userId = userId,
                tripId = tripId,
                seatIds = seatIds,
                extraServicesAmount = extraServicesAmount
            )
            val result = repository.createBooking(request)
            result.onSuccess { response ->
                _paymentUiState.value = PaymentUiState.Success(response)
                BookingSession.lastBookingResponse = response
                onCompleted()
            }.onFailure { exception ->
                // Parse tin nhắn lỗi thân thiện hơn từ API nếu có
                val errMsg = exception.message ?: "Đặt ghế không thành công. Vui lòng thử lại!"
                _paymentUiState.value = PaymentUiState.Error(errMsg)
            }
        }
    }

    fun startVnpayPayment(
        tripId: Long,
        seatIds: List<Long>,
        extraServicesAmount: Double,
        onPaymentUrlReady: (VnpayPaymentResponseDTO) -> Unit
    ) {
        val userId = AuthSession.user?.userId
        if (userId == null) {
            _paymentUiState.value = PaymentUiState.Error("Bạn cần đăng nhập để thanh toán.")
            return
        }

        viewModelScope.launch {
            _paymentUiState.value = PaymentUiState.Loading

            val booking = BookingSession.lastBookingResponse ?: run {
                val bookingRequest = BookingRequestDTO(
                    userId = userId,
                    tripId = tripId,
                    seatIds = seatIds,
                    extraServicesAmount = extraServicesAmount
                )
                val bookingResult = repository.createBooking(bookingRequest)
                bookingResult.getOrElse { exception ->
                    _paymentUiState.value = PaymentUiState.Error(
                        exception.message ?: "Không thể tạo booking để thanh toán."
                    )
                    return@launch
                }.also { BookingSession.lastBookingResponse = it }
            }

            val paymentResult = repository.createVnpayPayment(
                VnpayPaymentRequestDTO(
                    bookingId = booking.id,
                    locale = "vn"
                )
            )

            paymentResult.onSuccess { payment ->
                _paymentUiState.value = PaymentUiState.VnpayReady(payment)
                onPaymentUrlReady(payment)
                startPaymentStatusPolling(payment.bookingId)
            }.onFailure { exception ->
                _paymentUiState.value = PaymentUiState.Error(
                    exception.message ?: "Không thể khởi tạo thanh toán VNPay."
                )
            }
        }
    }

    fun startPaymentStatusPolling(bookingId: Long, onPaid: (() -> Unit)? = null) {
        if (paymentPollingJob?.isActive == true) return

        paymentPollingJob = viewModelScope.launch {
            repeat(120) {
                delay(3000)
                val result = repository.getPaymentStatus(bookingId)
                result.onSuccess { status ->
                    when {
                        status.paid -> {
                            val booking = BookingSession.lastBookingResponse
                            if (booking != null) {
                                val confirmedBooking = booking.copy(status = status.bookingStatus)
                                BookingSession.lastBookingResponse = confirmedBooking
                                _paymentUiState.value = PaymentUiState.Success(confirmedBooking)
                            }
                            onPaid?.invoke()
                            return@launch
                        }
                        status.paymentStatus == "FAILED" || status.bookingStatus == "CANCELLED" -> {
                            _paymentUiState.value = PaymentUiState.Error("Thanh toán VNPay không thành công.")
                            return@launch
                        }
                    }
                }
            }
        }
    }

    fun stopPaymentStatusPolling() {
        paymentPollingJob?.cancel()
        paymentPollingJob = null
    }

    fun resetPaymentState() {
        _paymentUiState.value = PaymentUiState.Idle
    }
}
