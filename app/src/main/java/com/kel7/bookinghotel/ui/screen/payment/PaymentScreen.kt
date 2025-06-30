package com.kel7.bookinghotel.ui.screen.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.kel7.bookinghotel.data.model.Hotel
import com.kel7.bookinghotel.data.model.PaymentMethod
import com.kel7.bookinghotel.data.model.RoomType
import com.kel7.bookinghotel.ui.components.BookingSuccessDialog
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    hotel: Hotel,
    roomType: RoomType,
    checkInDate: String,
    checkOutDate: String,
    guestCount: Int,
    paymentMethods: List<PaymentMethod>,
    selectedPaymentMethod: PaymentMethod?,
    onPaymentMethodSelect: (PaymentMethod) -> Unit,
    onBackClick: () -> Unit,
    onConfirmBookingClick: () -> Unit,
    isLoading: Boolean = false,
    isBookingSuccess: Boolean = false,
    errorMessage: String? = null,
    lastBooking: com.kel7.bookinghotel.data.model.Booking? = null,
    onNavigateToHome: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {}
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val context = LocalContext.current
      // Show toast notification when booking is successful
    LaunchedEffect(isBookingSuccess) {
        if (isBookingSuccess) {
            Toast.makeText(context, "Pemesanan berhasil! Pesanan Anda telah dikonfirmasi.", Toast.LENGTH_LONG).show()
        }
    }
    
    // Show error toast when there's an error
    LaunchedEffect(errorMessage) {
        if (errorMessage != null && errorMessage.isNotBlank()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {        // Top App Bar
        TopAppBar(
            title = { Text("Pembayaran") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Booking Summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {                        Text(
                            text = "Ringkasan Pemesanan",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        SummaryRow("Hotel", hotel.name)
                        SummaryRow("Kamar", roomType.name)
                        SummaryRow("Tamu", "$guestCount ${if (guestCount == 1) "Tamu" else "Tamu"}")
                        
                        val (checkInDisplayText, checkOutDisplayText) = remember(checkInDate, checkOutDate) {
                            try {
                                val checkIn = dateFormat.parse(checkInDate)
                                val checkOut = dateFormat.parse(checkOutDate)
                                Pair(
                                    checkIn?.let { displayDateFormat.format(it) } ?: checkInDate,
                                    checkOut?.let { displayDateFormat.format(it) } ?: checkOutDate
                                )
                            } catch (e: Exception) {
                                Pair(checkInDate, checkOutDate)
                            }
                        }
                          SummaryRow("Check-in", checkInDisplayText)
                        SummaryRow("Check-out", checkOutDisplayText)
                        
                        // Calculate number of nights and total price
                        val (numberOfNights, totalPrice) = remember(checkInDate, checkOutDate) {
                            if (checkInDate.isNotEmpty() && checkOutDate.isNotEmpty()) {
                                try {
                                    val checkIn = java.time.LocalDate.parse(checkInDate)
                                    val checkOut = java.time.LocalDate.parse(checkOutDate)
                                    val nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut).toInt().coerceAtLeast(1)
                                    val price = nights * roomType.pricePerNight
                                    Pair(nights, price)
                                } catch (e: Exception) {
                                    Pair(1, roomType.pricePerNight)
                                }
                            } else {
                                Pair(1, roomType.pricePerNight)
                            }
                        }
                        
                        SummaryRow("Duration", "$numberOfNights ${if (numberOfNights == 1) "Night" else "Nights"}")
                        SummaryRow("Price per night", currencyFormat.format(roomType.pricePerNight))
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Price",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currencyFormat.format(totalPrice),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            item {
                // Payment Methods
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {                        Text(
                            text = "Metode Pembayaran",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        paymentMethods.forEach { paymentMethod ->
                            PaymentMethodItem(
                                paymentMethod = paymentMethod,
                                isSelected = selectedPaymentMethod?.id == paymentMethod.id,
                                onSelect = { onPaymentMethodSelect(paymentMethod) }
                            )
                        }
                    }
                }
            }
            
            // Error Message Card
            errorMessage?.let { error ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }        // Confirm Booking Button
        Surface(
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    onConfirmBookingClick()
                },
                enabled = !isLoading && selectedPaymentMethod != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp), // Slightly taller for better touch target
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                if (isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Memproses...", fontSize = 16.sp)
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Konfirmasi",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Konfirmasi Pemesanan", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
    
    // Show success dialog when booking is successful
    if (isBookingSuccess && lastBooking != null) {
        BookingSuccessDialog(
            booking = lastBooking,
            onDismiss = { },
            onGoToHistory = onNavigateToHistory,
            onGoToHome = onNavigateToHome
        )
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
fun PaymentMethodItem(
    paymentMethod: PaymentMethod,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect
            )
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.surface
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Payment,
            contentDescription = paymentMethod.name,
            modifier = Modifier.size(24.dp),
            tint = if (isSelected) MaterialTheme.colorScheme.primary 
                  else MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = paymentMethod.name,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
            color = if (isSelected) MaterialTheme.colorScheme.onSurface
                   else MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (isSelected) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Selected",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
