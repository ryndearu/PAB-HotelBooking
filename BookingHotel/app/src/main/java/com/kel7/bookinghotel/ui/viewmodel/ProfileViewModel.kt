package com.kel7.bookinghotel.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kel7.bookinghotel.data.model.User
import com.kel7.bookinghotel.data.repository.HotelRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: HotelRepository = HotelRepository()) : ViewModel() {
    var uiState by mutableStateOf(ProfileUiState())
        private set

    val currentUser = repository.currentUser

    fun updateProfile(name: String, phoneNumber: String) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            repository.updateUserProfile(name, phoneNumber)
                .onSuccess { user ->
                    uiState = uiState.copy(
                        isLoading = false,
                        isUpdateSuccess = true
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Update failed"
                    )
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun resetUpdateSuccess() {
        uiState = uiState.copy(isUpdateSuccess = false)
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isUpdateSuccess: Boolean = false,
    val errorMessage: String? = null
)
