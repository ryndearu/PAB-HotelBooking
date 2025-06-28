package com.kel7.bookinghotel.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginScreen

@Serializable
object RegisterScreen

@Serializable
object HomeScreen

@Serializable
object SearchScreen

@Serializable
data class HotelDetailScreen(val hotelId: String)

@Serializable
data class BookingScreen(val hotelId: String, val roomTypeId: String)

@Serializable
data class PaymentScreen(val hotelId: String, val roomTypeId: String)

@Serializable
object BookingHistoryScreen

@Serializable
object ProfileScreen

@Serializable
object EditProfileScreen
