package com.kel7.bookinghotel.data.model

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String,
    val phoneNumber: String = "",
    val profileImageUrl: String = ""
)

@Serializable
data class Hotel(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val pricePerNight: Double,
    val rating: Float,
    val location: String,
    val amenities: List<String>,
    val roomTypes: List<RoomType>
)

@Serializable
data class RoomType(
    val id: String,
    val name: String,
    val description: String,
    val pricePerNight: Double,
    val maxOccupancy: Int,
    val amenities: List<String>,
    val imageUrls: List<String>
)

@Serializable
data class Booking(
    val id: String,
    val userId: String,
    val hotelId: String,
    val hotelName: String,
    val roomTypeId: String,
    val roomTypeName: String,
    val checkInDate: String,
    val checkOutDate: String,
    val totalPrice: Double,
    val status: BookingStatus,
    val guestCount: Int,
    val specialRequests: String = "",
    val bookingDate: String
)

@Serializable
enum class BookingStatus {
    CONFIRMED,
    CANCELLED,
    COMPLETED,
    PENDING
}

@Serializable
data class PaymentMethod(
    val id: String,
    val name: String,
    val type: PaymentType,
    val icon: String
)

@Serializable
enum class PaymentType {
    CREDIT_CARD,
    DEBIT_CARD,
    E_WALLET,
    BANK_TRANSFER
}
