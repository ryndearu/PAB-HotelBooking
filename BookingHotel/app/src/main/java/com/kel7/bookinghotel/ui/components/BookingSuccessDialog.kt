package com.kel7.bookinghotel.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kel7.bookinghotel.data.model.Booking
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookingSuccessDialog(
    booking: Booking?,
    onDismiss: () -> Unit,
    onGoToHistory: () -> Unit,
    onGoToHome: () -> Unit
) {
    if (booking != null) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Success Icon
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF4CAF50)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                      // Success Title
                    Text(
                        text = "Pemesanan Dikonfirmasi!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Success Message
                    Text(
                        text = "Pemesanan Anda telah berhasil dikonfirmasi. Anda akan menerima email konfirmasi segera.",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Booking Details
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {                            Text(
                                text = "Detail Pemesanan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            BookingDetailRow("ID Pemesanan", booking.id.take(8).uppercase())
                            BookingDetailRow("Hotel", booking.hotelName)
                            BookingDetailRow("Kamar", booking.roomTypeName)
                            
                            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                            BookingDetailRow("Total Jumlah", currencyFormat.format(booking.totalPrice))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {                        OutlinedButton(
                            onClick = onGoToHistory,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Lihat Riwayat")
                        }
                        
                        Button(
                            onClick = onGoToHome,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Ke Beranda")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingDetailRow(label: String, value: String) {
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
