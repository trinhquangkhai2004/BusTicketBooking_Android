package com.khaiqueng_finalterm.busticketbooking.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khaiqueng_finalterm.busticketbooking.data.repository.AuthSession
import com.khaiqueng_finalterm.busticketbooking.data.repository.BookingSession
import com.khaiqueng_finalterm.busticketbooking.ui.theme.PrimaryBlue
import java.text.SimpleDateFormat
import java.util.Locale

private val TicketText = Color(0xFF17202A)
private val TicketMuted = Color(0xFF6B7280)
private val TicketLine = Color(0xFFE6EAF0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailsScreen(
    onHomeClick: () -> Unit
) {
    val trip = BookingSession.selectedTrip
    val booking = BookingSession.lastBookingResponse
    val userName = AuthSession.user?.name ?: "Hành khách"
    val seatNumbers = BookingSession.selectedSeatNumbers
        .takeIf { it.isNotEmpty() }
        ?.joinToString(", ")
        ?: "Chưa có"

    val departureTime = formatDisplayTime(trip?.departureTime, "06:00")
    val arrivalTime = formatDisplayTime(trip?.arrivalTime, "08:30")
    val displayDate = formatDisplayDate(trip?.departureTime)
    val duration = trip?.duration ?: "2h 30m"

    Scaffold(
        containerColor = PrimaryBlue,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Vé của bạn",
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        BookingSession.clear()
                        onHomeClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 18.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Về Trang Chủ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .padding(top = 12.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(42.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Thanh toán thành công",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Vé điện tử đã sẵn sàng. Vui lòng xuất trình mã này khi lên xe.",
                color = Color.White.copy(alpha = 0.88f),
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Spacer(modifier = Modifier.height(26.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 22.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "BUS GO TICKETS",
                                color = PrimaryBlue,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Mã vé #${booking?.id ?: "1002"}",
                                color = TicketText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RouteEndpoint(
                                city = trip?.departureLocation?.name ?: "Đà Nẵng",
                                time = departureTime,
                                modifier = Modifier.weight(1f)
                            )
                            RouteMiddle(
                                duration = duration,
                                modifier = Modifier
                                    .width(82.dp)
                                    .padding(horizontal = 8.dp)
                            )
                            RouteEndpoint(
                                city = trip?.arrivalLocation?.name ?: "Huế",
                                time = arrivalTime,
                                modifier = Modifier.weight(1f),
                                alignEnd = true
                            )
                        }

                        Spacer(modifier = Modifier.height(26.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            TicketInfoItem(
                                label = "Ngày đi",
                                value = displayDate,
                                modifier = Modifier.weight(1f)
                            )
                            TicketInfoItem(
                                label = "Biển số xe",
                                value = trip?.bus?.licensePlate ?: "43A-12345",
                                modifier = Modifier.weight(1f),
                                alignEnd = true
                            )
                        }
                    }

                    TicketSeparator()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            TicketInfoItem(
                                label = "Hành khách",
                                value = userName,
                                modifier = Modifier.weight(1f)
                            )
                            TicketInfoItem(
                                label = "Ghế",
                                value = seatNumbers,
                                modifier = Modifier.weight(1f),
                                valueColor = PrimaryBlue,
                                alignEnd = true
                            )
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        Box(
                            modifier = Modifier
                                .size(138.dp)
                                .background(Color(0xFFF7F9FC), RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "QR Code",
                                modifier = Modifier.size(112.dp),
                                tint = TicketText
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Quét mã khi lên xe",
                            color = TicketMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteEndpoint(
    city: String,
    time: String,
    modifier: Modifier = Modifier,
    alignEnd: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start
    ) {
        Text(
            text = city,
            color = TicketText,
            fontSize = 23.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (alignEnd) TextAlign.End else TextAlign.Start
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = time,
            color = TicketMuted,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RouteMiddle(
    duration: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = duration,
            color = TicketMuted,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(22.dp)
                    .height(1.dp)
                    .background(TicketLine)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(18.dp)
            )
            Box(
                modifier = Modifier
                    .width(22.dp)
                    .height(1.dp)
                    .background(TicketLine)
            )
        }
    }
}

@Composable
private fun TicketInfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TicketText,
    alignEnd: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start
    ) {
        Text(
            text = label,
            color = TicketMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(7.dp))
        Text(
            text = value,
            color = valueColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (alignEnd) TextAlign.End else TextAlign.Start
        )
    }
}

@Composable
private fun TicketSeparator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = TicketLine,
                start = Offset(28f, size.height / 2),
                end = Offset(size.width - 28f, size.height / 2),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
            )
        }
        Box(
            modifier = Modifier
                .size(26.dp)
                .align(Alignment.CenterStart)
                .offset(x = (-13).dp)
                .background(PrimaryBlue, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(26.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 13.dp)
                .background(PrimaryBlue, CircleShape)
        )
    }
}

private fun formatDisplayDate(departureTime: String?): String {
    val rawDate = departureTime?.substringBefore("T") ?: "2026-05-11"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        inputFormat.parse(rawDate)?.let(outputFormat::format) ?: rawDate
    } catch (e: Exception) {
        rawDate
    }
}

private fun formatDisplayTime(dateTime: String?, fallback: String): String {
    val time = dateTime?.substringAfter("T", "")?.take(5)?.takeIf { it.isNotBlank() } ?: fallback
    val hour = time.substringBefore(":").toIntOrNull() ?: return time
    val suffix = if (hour < 12) "SA" else "CH"
    return "$time $suffix"
}
