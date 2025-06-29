package com.kel7.bookinghotel.ui.screen.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kel7.bookinghotel.data.model.Hotel
import com.kel7.bookinghotel.data.model.RoomType
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    hotel: Hotel,
    roomType: RoomType,
    checkInDate: String,
    checkOutDate: String,
    guestCount: Int,
    specialRequests: String,
    onCheckInDateChange: (String) -> Unit,
    onCheckOutDateChange: (String) -> Unit,
    onGuestCountChange: (Int) -> Unit,
    onSpecialRequestsChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    isLoading: Boolean = false
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    // Calculate number of nights and total price
    val (numberOfNights, totalPrice) = remember(checkInDate, checkOutDate) {
        if (checkInDate.isNotEmpty() && checkOutDate.isNotEmpty()) {
            try {
                val checkIn = LocalDate.parse(checkInDate)
                val checkOut = LocalDate.parse(checkOutDate)
                val nights = ChronoUnit.DAYS.between(checkIn, checkOut).toInt()
                val price = if (nights > 0) nights * roomType.pricePerNight else roomType.pricePerNight
                Pair(nights, price)
            } catch (e: Exception) {
                Pair(1, roomType.pricePerNight)
            }
        } else {
            Pair(1, roomType.pricePerNight)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {        // Top App Bar
        TopAppBar(
            title = { Text("Pesan Kamar") },
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
                // Hotel and Room Summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = hotel.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = roomType.name,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = roomType.description,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                          Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {                            Text(
                                text = "${currencyFormat.format(roomType.pricePerNight)} per malam",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (numberOfNights > 1) {
                                Text(
                                    text = "$numberOfNights malam",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        Text(
                            text = "Total: ${currencyFormat.format(totalPrice)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)                        )
                    }
                }
            }

            item {
                // Check-in Date
                BookingDateField(
                    label = "Tanggal Check-in",
                    date = checkInDate,
                    onDateChange = onCheckInDateChange,
                    displayDateFormat = displayDateFormat
                )
            }

            item {
                // Check-out Date
                BookingDateField(
                    label = "Tanggal Check-out",
                    date = checkOutDate,
                    onDateChange = onCheckOutDateChange,
                    displayDateFormat = displayDateFormat
                )
            }

            item {
                // Guest Count
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {                        Text(
                            text = "Jumlah Tamu",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.People,
                                    contentDescription = "Guests",
                                    modifier = Modifier.size(20.dp),                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "$guestCount ${if (guestCount == 1) "Tamu" else "Tamu"}",
                                    fontSize = 16.sp
                                )
                            }
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { 
                                        if (guestCount > 1) onGuestCountChange(guestCount - 1) 
                                    },
                                    enabled = guestCount > 1,
                                    modifier = Modifier.size(40.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("-", fontSize = 18.sp)
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                OutlinedButton(
                                    onClick = { 
                                        if (guestCount < roomType.maxOccupancy) onGuestCountChange(guestCount + 1) 
                                    },
                                    enabled = guestCount < roomType.maxOccupancy,
                                    modifier = Modifier.size(40.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("+", fontSize = 18.sp)
                                }
                            }
                        }
                          Text(
                            text = "Maksimal ${roomType.maxOccupancy} tamu",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            item {
                // Special Requests
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {                        Text(
                            text = "Permintaan Khusus (Opsional)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        OutlinedTextField(
                            value = specialRequests,
                            onValueChange = onSpecialRequestsChange,
                            placeholder = { Text("Permintaan khusus atau preferensi...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )
                    }
                }
            }
        }

        // Continue Button
        Surface(
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            val isValidBooking = checkInDate.isNotEmpty() && checkOutDate.isNotEmpty() && guestCount > 0
            
            Button(
                onClick = onContinueClick,
                enabled = !isLoading && isValidBooking,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
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
                        Text("Memuat...", fontSize = 16.sp)
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Lanjutkan",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lanjut ke Pembayaran", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            
            // Validation message
            if (!isValidBooking && !isLoading) {
                Text(
                    text = when {
                        checkInDate.isEmpty() -> "Silakan pilih tanggal check-in"
                        checkOutDate.isEmpty() -> "Silakan pilih tanggal check-out"
                        guestCount <= 0 -> "Silakan pilih jumlah tamu"
                        else -> ""
                    },
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun BookingDateField(
    label: String,
    date: String,
    onDateChange: (String) -> Unit,
    displayDateFormat: SimpleDateFormat
) {
    val dateDialogState = rememberMaterialDialogState()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    dateDialogState.show()
                }
                .padding(16.dp)
        ) {
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = "Date",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant                )
                Spacer(modifier = Modifier.width(8.dp))
                if (date.isEmpty()) {
                    Text(
                        text = "Pilih $label",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val displayText = remember(date) {
                        try {
                            val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
                            parsedDate?.let { displayDateFormat.format(it) } ?: date
                        } catch (e: Exception) {
                            date
                        }
                    }
                    Text(
                        text = displayText,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
    
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {            positiveButton(text = "Ok")
            negativeButton(text = "Batal")
        }
    ) {
        datepicker(
            initialDate = if (date.isNotEmpty()) {
                try {
                    LocalDate.parse(date)
                } catch (e: Exception) {
                    LocalDate.now().plusDays(if (label.contains("Check-in")) 1 else 2)
                }
            } else {
                LocalDate.now().plusDays(if (label.contains("Check-in")) 1 else 2)
            },            title = "Pilih $label",
            allowedDateValidator = { selectedDate ->
                if (label.contains("Check-in")) {
                    selectedDate >= LocalDate.now()
                } else {
                    selectedDate > LocalDate.now()
                }
            }
        ) { pickedDate ->
            onDateChange(pickedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        }
    }
}
