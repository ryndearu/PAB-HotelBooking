package com.kel7.bookinghotel.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kel7.bookinghotel.data.model.Hotel
import com.kel7.bookinghotel.data.repository.HotelRepository
import kotlinx.coroutines.launch

class HotelViewModel(private val repository: HotelRepository = HotelRepository()) : ViewModel() {
    var uiState by mutableStateOf(HotelUiState())
        private set

    init {
        loadHotels()
    }

    private fun loadHotels() {
        uiState = uiState.copy(isLoading = true)
        
        viewModelScope.launch {
            try {
                val hotels = repository.getHotels()
                uiState = uiState.copy(
                    hotels = hotels,
                    filteredHotels = hotels,
                    isLoading = false
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load hotels"
                )
            }
        }
    }

    fun searchHotels(query: String) {
        uiState = uiState.copy(searchQuery = query)
        
        viewModelScope.launch {
            try {
                val filteredHotels = if (query.isBlank()) {
                    repository.getHotels()
                } else {
                    repository.searchHotels(query)
                }
                uiState = uiState.copy(filteredHotels = filteredHotels)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message ?: "Search failed")
            }
        }
    }

    fun filterHotels(minPrice: Double?, maxPrice: Double?, minRating: Float?) {
        viewModelScope.launch {
            try {
                val filteredHotels = repository.filterHotels(minPrice, maxPrice, minRating)
                uiState = uiState.copy(
                    filteredHotels = filteredHotels,
                    filterMinPrice = minPrice,
                    filterMaxPrice = maxPrice,
                    filterMinRating = minRating
                )
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message ?: "Filter failed")
            }
        }
    }

    fun clearFilters() {
        uiState = uiState.copy(
            filteredHotels = uiState.hotels,
            filterMinPrice = null,
            filterMaxPrice = null,
            filterMinRating = null
        )
    }

    fun getHotelById(hotelId: String): Hotel? {
        return uiState.hotels.find { it.id == hotelId }
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}

data class HotelUiState(
    val hotels: List<Hotel> = emptyList(),
    val filteredHotels: List<Hotel> = emptyList(),
    val searchQuery: String = "",
    val filterMinPrice: Double? = null,
    val filterMaxPrice: Double? = null,
    val filterMinRating: Float? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
