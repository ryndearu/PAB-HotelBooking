package com.kel7.bookinghotel.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kel7.bookinghotel.data.model.User
import com.kel7.bookinghotel.data.repository.HotelRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: HotelRepository = HotelRepository()) : ViewModel() {
    var uiState by mutableStateOf(AuthUiState())
        private set

    val currentUser = repository.currentUser
    
    // Expose repository for sharing with other ViewModels
    val sharedRepository: HotelRepository = repository

    fun login(email: String, password: String) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            repository.login(email, password)
                .onSuccess { user ->
                    uiState = uiState.copy(isLoading = false, isSuccess = true)
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Login failed"
                    )
                }
        }
    }

    fun register(email: String, password: String, name: String) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            repository.register(email, password, name)
                .onSuccess { user ->
                    uiState = uiState.copy(isLoading = false, isSuccess = true)
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Registration failed"
                    )
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            uiState = AuthUiState()
        }
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun resetSuccess() {
        uiState = uiState.copy(isSuccess = false)
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
