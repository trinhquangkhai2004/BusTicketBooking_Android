package com.khaiqueng_finalterm.busticketbooking.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khaiqueng_finalterm.busticketbooking.data.model.TripDTO
import com.khaiqueng_finalterm.busticketbooking.data.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class BusListUiState {
    object Loading : BusListUiState()
    data class Success(val trips: List<TripDTO>) : BusListUiState()
    data class Error(val message: String) : BusListUiState()
}

class BusListViewModel : ViewModel() {
    private val repository = TripRepository()
    
    private val _uiState = MutableStateFlow<BusListUiState>(BusListUiState.Loading)
    val uiState: StateFlow<BusListUiState> = _uiState.asStateFlow()

    fun searchTrips(fromLocationName: String, toLocationName: String, dateLabel: String) {
        viewModelScope.launch {
            _uiState.value = BusListUiState.Loading
            
            val fromId = getLocationId(fromLocationName)
            val toId = getLocationId(toLocationName)
            val formattedDate = formatDateForBackend(dateLabel)
            
            val result = repository.searchTrips(fromId, toId, formattedDate)
            result.onSuccess { trips ->
                _uiState.value = BusListUiState.Success(trips)
            }.onFailure { exception ->
                _uiState.value = BusListUiState.Error(exception.message ?: "Lỗi kết nối máy chủ")
            }
        }
    }

    private fun getLocationId(name: String): Long {
        return when (name.trim()) {
            "Đà Nẵng" -> 1
            "Huế" -> 2
            "Hội An" -> 3
            "Quy Nhơn" -> 4
            "Quảng Ngãi" -> 5
            "Tam Kỳ" -> 6
            else -> 1 // Default fallback
        }
    }

    private fun formatDateForBackend(vietnameseDate: String): String {
        // Chuyển "09 Th04 2026" thành "2026-04-09"
        try {
            val parts = vietnameseDate.split(" ")
            if (parts.size == 3) {
                val day = parts[0]
                val monthStr = parts[1].replace("Th", "") // "04"
                val year = parts[2]
                return "$year-$monthStr-$day"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "2026-04-09" // fallback an toàn
    }
}
