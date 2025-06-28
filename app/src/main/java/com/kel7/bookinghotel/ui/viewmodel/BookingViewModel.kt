package com.kel7.bookinghotel.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kel7.bookinghotel.data.model.Booking
import com.kel7.bookinghotel.data.model.PaymentMethod
import com.kel7.bookinghotel.data.repository.HotelRepository
import kotlinx.coroutines.launch

class BookingViewModel(private val repository: HotelRepository = HotelRepository()) : ViewModel() {
    var uiState by mutableStateOf(BookingUiState())
        private set

    val bookings = repository.bookings

    init {
        loadPaymentMethods()
    }

    private fun loadPaymentMethods() {
        viewModelScope.launch {
            try {
                val paymentMethods = repository.getPaymentMethods()
                uiState = uiState.copy(
                    paymentMethods = paymentMethods,
                    selectedPaymentMethod = paymentMethods.firstOrNull() // Auto select first payment method
                )
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message ?: "Failed to load payment methods")
            }
        }
    }

    fun updateBookingDetails(
        checkInDate: String = uiState.checkInDate,
        checkOutDate: String = uiState.checkOutDate,
        guestCount: Int = uiState.guestCount,
        specialRequests: String = uiState.specialRequests
    ) {
        uiState = uiState.copy(
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            guestCount = guestCount,
            specialRequests = specialRequests
        )
    }

    fun selectPaymentMethod(paymentMethod: PaymentMethod) {
        uiState = uiState.copy(selectedPaymentMethod = paymentMethod)
    }

    fun bookRoom(
        hotelId: String,
        roomTypeId: String
    ) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            // Validate booking data before submission
            if (uiState.selectedPaymentMethod == null) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Please select a payment method"
                )
                return@launch
            }
            
            // Provide default values if data is empty (bypass validation)
            val checkInDate = if (uiState.checkInDate.isNotEmpty()) {
                uiState.checkInDate
            } else {
                // Default to tomorrow
                java.time.LocalDate.now().plusDays(1).toString()
            }
            
            val checkOutDate = if (uiState.checkOutDate.isNotEmpty()) {
                uiState.checkOutDate
            } else {
                // Default to day after tomorrow
                java.time.LocalDate.now().plusDays(2).toString()
            }
            
            // Validate check-out date is after check-in date
            try {
                val checkIn = java.time.LocalDate.parse(checkInDate)
                val checkOut = java.time.LocalDate.parse(checkOutDate)
                if (!checkOut.isAfter(checkIn)) {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = "Check-out date must be after check-in date"
                    )
                    return@launch
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Invalid date format"
                )
                return@launch
            }
            
            repository.bookRoom(
                hotelId = hotelId,
                roomTypeId = roomTypeId,
                checkInDate = checkInDate,
                checkOutDate = checkOutDate,
                guestCount = if (uiState.guestCount > 0) uiState.guestCount else 1,
                specialRequests = uiState.specialRequests
            ).onSuccess { booking ->
                uiState = uiState.copy(
                    isLoading = false,
                    isBookingSuccess = true,
                    lastBooking = booking
                )
            }.onFailure { error ->
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Booking failed"
                )
            }
        }
    }

    fun cancelBooking(bookingId: String) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            repository.cancelBooking(bookingId)
                .onSuccess {
                    uiState = uiState.copy(isLoading = false)
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Cancellation failed"
                    )
                }
        }
    }

    fun resetBookingSuccess() {
        uiState = uiState.copy(isBookingSuccess = false, lastBooking = null)
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun resetBookingForm() {
        uiState = BookingUiState()
        loadPaymentMethods()
    }
}

data class BookingUiState(
    val checkInDate: String = "",
    val checkOutDate: String = "",
    val guestCount: Int = 1,
    val specialRequests: String = "",
    val selectedPaymentMethod: PaymentMethod? = null,
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val isLoading: Boolean = false,
    val isBookingSuccess: Boolean = false,
    val lastBooking: Booking? = null,
    val errorMessage: String? = null
) {
    fun calculateNumberOfNights(): Int {
        return if (checkInDate.isNotEmpty() && checkOutDate.isNotEmpty()) {
            try {
                val checkIn = java.time.LocalDate.parse(checkInDate)
                val checkOut = java.time.LocalDate.parse(checkOutDate)
                java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut).toInt().coerceAtLeast(1)
            } catch (e: Exception) {
                1
            }
        } else {
            1
        }
    }
    
    fun calculateTotalPrice(pricePerNight: Double): Double {
        return calculateNumberOfNights() * pricePerNight
    }
    
    // Helper function to check if booking data is valid
    fun isBookingDataValid(): Boolean {
        return checkInDate.isNotEmpty() && checkOutDate.isNotEmpty() && guestCount > 0
    }
}
