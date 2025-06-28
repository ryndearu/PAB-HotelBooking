package com.kel7.bookinghotel.ui.screen.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kel7.bookinghotel.data.model.Booking
import com.kel7.bookinghotel.data.model.BookingStatus
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryScreen(
    bookings: List<Booking>,
    onBackClick: () -> Unit,
    onCancelBooking: (String) -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Booking History") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        if (bookings.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Hotel,
                        contentDescription = "No bookings",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "No bookings yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = "Your booking history will appear here",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bookings.sortedByDescending { it.bookingDate }) { booking ->
                    BookingCard(
                        booking = booking,
                        onCancelClick = { onCancelBooking(booking.id) },
                        currencyFormat = currencyFormat
                    )
                }
            }
        }
    }
}

@Composable
fun BookingCard(
    booking: Booking,
    onCancelClick: () -> Unit,
    currencyFormat: NumberFormat
) {
    val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.hotelName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = booking.roomTypeName,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                BookingStatusChip(status = booking.status)
            }

            Spacer(modifier = Modifier.height(12.dp))            // Booking Details
            BookingDetailRow("Booking ID", booking.id.take(8).uppercase())
            
            val (checkInDisplayText, checkOutDisplayText) = remember(booking.checkInDate, booking.checkOutDate) {
                try {
                    val checkIn = dateFormat.parse(booking.checkInDate)
                    val checkOut = dateFormat.parse(booking.checkOutDate)
                    Pair(
                        checkIn?.let { displayDateFormat.format(it) } ?: booking.checkInDate,
                        checkOut?.let { displayDateFormat.format(it) } ?: booking.checkOutDate
                    )
                } catch (e: Exception) {
                    Pair(booking.checkInDate, booking.checkOutDate)
                }
            }
            
            BookingDetailRow("Check-in", checkInDisplayText)
            BookingDetailRow("Check-out", checkOutDisplayText)
            
            BookingDetailRow("Guests", "${booking.guestCount} ${if (booking.guestCount == 1) "Guest" else "Guests"}")
            
            if (booking.specialRequests.isNotEmpty()) {
                BookingDetailRow("Special Requests", booking.specialRequests)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            // Price and Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Total Paid",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = currencyFormat.format(booking.totalPrice),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (booking.status == BookingStatus.CONFIRMED) {
                    OutlinedButton(
                        onClick = onCancelClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cancel Booking")
                    }
                }
            }
        }
    }
}

@Composable
fun BookingDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun BookingStatusChip(status: BookingStatus) {
    val (backgroundColor, contentColor, icon, text) = when (status) {
        BookingStatus.CONFIRMED -> 
            Quadruple(
                Color(0xFF4CAF50).copy(alpha = 0.1f),
                Color(0xFF4CAF50),
                Icons.Default.CheckCircle,
                "Confirmed"
            )
        BookingStatus.CANCELLED -> 
            Quadruple(
                MaterialTheme.colorScheme.errorContainer,
                MaterialTheme.colorScheme.onErrorContainer,
                Icons.Default.Cancel,
                "Cancelled"
            )
        BookingStatus.COMPLETED -> 
            Quadruple(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.onPrimaryContainer,
                Icons.Default.CheckCircle,
                "Completed"
            )
        BookingStatus.PENDING -> 
            Quadruple(
                Color(0xFFFF9800).copy(alpha = 0.1f),
                Color(0xFFFF9800),
                Icons.Default.Schedule,
                "Pending"
            )
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                icon,
                contentDescription = text,
                modifier = Modifier.size(14.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
