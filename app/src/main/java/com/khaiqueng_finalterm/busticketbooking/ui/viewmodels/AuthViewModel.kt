package com.khaiqueng_finalterm.busticketbooking.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khaiqueng_finalterm.busticketbooking.data.repository.AuthRepository
import com.khaiqueng_finalterm.busticketbooking.data.repository.AuthSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (!validateCredentials(email, password)) return

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            repository.login(email.trim(), password)
                .onSuccess { user ->
                    AuthSession.user = user
                    _uiState.value = AuthUiState(isAuthenticated = true)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState(errorMessage = error.message ?: "Dang nhap that bai")
                }
        }
    }

    fun register(fullName: String, email: String, password: String) {
        if (fullName.trim().isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Vui long nhap ho va ten")
            return
        }
        if (!validateCredentials(email, password)) return

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            repository.register(fullName.trim(), email.trim(), password)
                .onSuccess {
                    AuthSession.idToken = null
                    AuthSession.user = null
                    _uiState.value = AuthUiState(
                        successMessage = "Dang ky thanh cong. Vui long dang nhap de tiep tuc."
                    )
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState(errorMessage = error.message ?: "Dang ky that bai")
                }
        }
    }

    fun clearAuthState() {
        _uiState.value = AuthUiState()
    }

    fun logout() {
        AuthSession.idToken = null
        AuthSession.user = null
        _uiState.value = AuthUiState()
    }

    private fun validateCredentials(email: String, password: String): Boolean {
        if (email.trim().isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Vui long nhap email")
            return false
        }
        if (password.isBlank()) {
            _uiState.value = AuthUiState(errorMessage = "Vui long nhap mat khau")
            return false
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState(errorMessage = "Mat khau phai co it nhat 6 ky tu")
            return false
        }
        return true
    }
}
