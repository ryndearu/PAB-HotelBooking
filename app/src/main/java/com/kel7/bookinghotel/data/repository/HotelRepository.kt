package com.kel7.bookinghotel.data.repository

import com.kel7.bookinghotel.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class HotelRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Mock data
    private val hotels = listOf(
        Hotel(
            id = "1",
            name = "Grand Royal Hotel",
            description = "Luxury hotel in the heart of Jakarta with world-class amenities and exceptional service.",
            imageUrl = "https://images.unsplash.com/photo-1564501049412-61c2a3083791?w=800",
            pricePerNight = 1500000.0,
            rating = 4.8f,
            location = "Jakarta Pusat",
            amenities = listOf("WiFi", "Pool", "Spa", "Restaurant", "Gym", "Parking"),
            roomTypes = listOf(
                RoomType(
                    id = "1-1",
                    name = "Deluxe Room",
                    description = "Spacious room with city view",
                    pricePerNight = 1500000.0,
                    maxOccupancy = 2,
                    amenities = listOf("King Bed", "City View", "Mini Bar", "WiFi"),
                    imageUrls = listOf("https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800")
                ),
                RoomType(
                    id = "1-2",
                    name = "Executive Suite",
                    description = "Premium suite with living area",
                    pricePerNight = 2500000.0,
                    maxOccupancy = 4,
                    amenities = listOf("King Bed", "Living Room", "Kitchen", "Balcony"),
                    imageUrls = listOf("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800")
                )
            )
        ),
        Hotel(
            id = "2",
            name = "Oceanview Resort",
            description = "Beautiful beachfront resort perfect for vacation and relaxation.",
            imageUrl = "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=800",
            pricePerNight = 1200000.0,
            rating = 4.6f,
            location = "Bali",
            amenities = listOf("Beach Access", "WiFi", "Pool", "Restaurant", "Bar"),
            roomTypes = listOf(
                RoomType(
                    id = "2-1",
                    name = "Ocean View Room",
                    description = "Room with stunning ocean view",
                    pricePerNight = 1200000.0,
                    maxOccupancy = 2,
                    amenities = listOf("Ocean View", "Queen Bed", "Balcony", "WiFi"),
                    imageUrls = listOf("https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=800")
                ),
                RoomType(
                    id = "2-2",
                    name = "Beach Villa",
                    description = "Private villa with direct beach access",
                    pricePerNight = 3000000.0,
                    maxOccupancy = 6,
                    amenities = listOf("Private Beach", "Pool", "Kitchen", "Living Room"),
                    imageUrls = listOf("https://images.unsplash.com/photo-1540541338287-41700207dee6?w=800")
                )
            )
        ),
        Hotel(
            id = "3",
            name = "Mountain Lodge",
            description = "Cozy lodge nestled in the mountains with breathtaking views.",
            imageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800",
            pricePerNight = 800000.0,
            rating = 4.4f,
            location = "Bandung",
            amenities = listOf("Mountain View", "WiFi", "Restaurant", "Hiking Trails"),
            roomTypes = listOf(
                RoomType(
                    id = "3-1",
                    name = "Standard Room",
                    description = "Comfortable room with mountain view",
                    pricePerNight = 800000.0,
                    maxOccupancy = 2,
                    amenities = listOf("Mountain View", "Double Bed", "Heater", "WiFi"),
                    imageUrls = listOf("https://images.unsplash.com/photo-1586611292717-f828b167408c?w=800")
                )
            )
        ),
        Hotel(
            id = "4",
            name = "Business Center Hotel",
            description = "Modern business hotel with excellent conference facilities.",
            imageUrl = "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?w=800",
            pricePerNight = 1000000.0,
            rating = 4.5f,
            location = "Jakarta Selatan",
            amenities = listOf("Conference Room", "WiFi", "Business Center", "Restaurant"),
            roomTypes = listOf(
                RoomType(
                    id = "4-1",
                    name = "Business Room",
                    description = "Perfect for business travelers",
                    pricePerNight = 1000000.0,
                    maxOccupancy = 2,
                    amenities = listOf("Work Desk", "King Bed", "Coffee Machine", "WiFi"),
                    imageUrls = listOf("https://images.unsplash.com/photo-1584132967334-10e028bd69f7?w=800")
                )
            )
        ),
        Hotel(
            id = "5",
            name = "Boutique Heritage Hotel",
            description = "Charming boutique hotel with rich cultural heritage and unique design.",
            imageUrl = "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=800",
            pricePerNight = 900000.0,
            rating = 4.7f,
            location = "Yogyakarta",
            amenities = listOf("Cultural Tours", "WiFi", "Traditional Restaurant", "Art Gallery"),
            roomTypes = listOf(
                RoomType(
                    id = "5-1",
                    name = "Heritage Room",
                    description = "Traditional room with modern comfort",
                    pricePerNight = 900000.0,
                    maxOccupancy = 2,
                    amenities = listOf("Traditional Decor", "Queen Bed", "Garden View", "WiFi"),
                    imageUrls = listOf("https://images.unsplash.com/photo-1595576508898-0ad5c879a061?w=800")
                )
            )
        )
    )

    private val paymentMethods = listOf(
        PaymentMethod("1", "Credit Card", PaymentType.CREDIT_CARD, "credit_card"),
        PaymentMethod("2", "Debit Card", PaymentType.DEBIT_CARD, "debit_card"),
        PaymentMethod("3", "OVO", PaymentType.E_WALLET, "ovo"),
        PaymentMethod("4", "GoPay", PaymentType.E_WALLET, "gopay"),
        PaymentMethod("5", "Bank Transfer", PaymentType.BANK_TRANSFER, "bank_transfer")
    )

    suspend fun login(email: String, password: String): Result<User> {
        // Always successful login for demo purposes
        val user = User(
            id = UUID.randomUUID().toString(),
            email = email,
            name = email.substringBefore("@").replaceFirstChar { it.uppercase() },
            phoneNumber = "081234567890"
        )
        _currentUser.value = user
        return Result.success(user)
    }

    suspend fun register(email: String, password: String, name: String): Result<User> {
        // Always successful registration for demo purposes
        val user = User(
            id = UUID.randomUUID().toString(),
            email = email,
            name = name,
            phoneNumber = "081234567890"
        )
        _currentUser.value = user
        return Result.success(user)
    }

    suspend fun logout() {
        _currentUser.value = null
        _bookings.value = emptyList()
    }

    suspend fun getHotels(): List<Hotel> {
        return hotels
    }

    suspend fun getHotelById(id: String): Hotel? {
        return hotels.find { it.id == id }
    }

    suspend fun searchHotels(query: String): List<Hotel> {
        return hotels.filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.location.contains(query, ignoreCase = true) 
        }
    }

    suspend fun filterHotels(
        minPrice: Double?,
        maxPrice: Double?,
        minRating: Float?
    ): List<Hotel> {
        return hotels.filter { hotel ->
            val priceMatch = when {
                minPrice != null && maxPrice != null -> 
                    hotel.pricePerNight >= minPrice && hotel.pricePerNight <= maxPrice
                minPrice != null -> hotel.pricePerNight >= minPrice
                maxPrice != null -> hotel.pricePerNight <= maxPrice
                else -> true
            }
            
            val ratingMatch = minRating?.let { hotel.rating >= it } ?: true
            
            priceMatch && ratingMatch
        }
    }

    suspend fun bookRoom(
        hotelId: String,
        roomTypeId: String,
        checkInDate: String,
        checkOutDate: String,
        guestCount: Int,
        specialRequests: String
    ): Result<Booking> {
        val currentUser = _currentUser.value ?: return Result.failure(Exception("User not logged in"))
        val hotel = getHotelById(hotelId) ?: return Result.failure(Exception("Hotel not found"))
        val roomType = hotel.roomTypes.find { it.id == roomTypeId } 
            ?: return Result.failure(Exception("Room type not found"))        // Calculate total price based on number of nights
        val totalPrice = try {
            val checkIn = java.time.LocalDate.parse(checkInDate)
            val checkOut = java.time.LocalDate.parse(checkOutDate)
            val numberOfNights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut).toInt().coerceAtLeast(1)
            numberOfNights * roomType.pricePerNight
        } catch (e: Exception) {
            roomType.pricePerNight // fallback to 1 night price
        }

        val booking = Booking(
            id = UUID.randomUUID().toString(),
            userId = currentUser.id,
            hotelId = hotelId,
            hotelName = hotel.name,
            roomTypeId = roomTypeId,
            roomTypeName = roomType.name,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            totalPrice = totalPrice,
            status = BookingStatus.CONFIRMED,
            guestCount = guestCount,
            specialRequests = specialRequests,
            bookingDate = dateFormatter.format(Date())
        )

        val currentBookings = _bookings.value.toMutableList()
        currentBookings.add(booking)
        _bookings.value = currentBookings

        return Result.success(booking)
    }

    suspend fun cancelBooking(bookingId: String): Result<Unit> {
        val currentBookings = _bookings.value.toMutableList()
        val bookingIndex = currentBookings.indexOfFirst { it.id == bookingId }
        
        if (bookingIndex == -1) {
            return Result.failure(Exception("Booking not found"))
        }

        currentBookings[bookingIndex] = currentBookings[bookingIndex].copy(status = BookingStatus.CANCELLED)
        _bookings.value = currentBookings

        return Result.success(Unit)
    }

    suspend fun getPaymentMethods(): List<PaymentMethod> {
        return paymentMethods
    }

    suspend fun updateUserProfile(
        name: String,
        phoneNumber: String
    ): Result<User> {
        val currentUser = _currentUser.value ?: return Result.failure(Exception("User not logged in"))
        
        val updatedUser = currentUser.copy(
            name = name,
            phoneNumber = phoneNumber
        )
        
        _currentUser.value = updatedUser
        return Result.success(updatedUser)
    }
}
