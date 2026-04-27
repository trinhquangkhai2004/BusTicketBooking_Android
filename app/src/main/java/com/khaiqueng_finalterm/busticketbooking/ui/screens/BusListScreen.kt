package com.khaiqueng_finalterm.busticketbooking.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khaiqueng_finalterm.busticketbooking.ui.components.BusTicketCard
import com.khaiqueng_finalterm.busticketbooking.ui.theme.PrimaryBlue

// ---------------------------------------------------------------------------
// Mock Data Model
// ---------------------------------------------------------------------------
data class BusTrip(
    val departureTime: String,
    val arrivalTime: String,
    val from: String,
    val to: String,
    val duration: String,
    val busType: String,
    val statusText: String,
    val price: String
)

val ALL_TRIPS = listOf(
    BusTrip("08:40 SA", "11:10 SA", "Đà Nẵng", "Huế",       "2h 30m • Đi thẳng",   "Xe Khách Đi Thẳng", "Sắp hết chỗ",   "150.000đ"),
    BusTrip("10:00 SA", "12:30 CH", "Đà Nẵng", "Huế",       "2h 30m • Đi thẳng",   "Xe Giường Nằm",     "Còn nhiều chỗ", "180.000đ"),
    BusTrip("14:00 CH", "16:30 CH", "Đà Nẵng", "Huế",       "2h 30m • Đi thẳng",   "Xe Khách Đi Thẳng", "Còn nhiều chỗ", "150.000đ"),
    BusTrip("08:00 SA", "08:45 SA", "Đà Nẵng", "Hội An",    "0h 45m • Đi thẳng",   "Xe Khách Đi Thẳng", "Sắp hết chỗ",   "50.000đ"),
    BusTrip("10:30 SA", "11:15 SA", "Đà Nẵng", "Hội An",    "0h 45m • Đi thẳng",   "Xe Khách Đi Thẳng", "Còn nhiều chỗ", "50.000đ"),
    BusTrip("09:00 SA", "02:30 CH", "Đà Nẵng", "Quy Nhơn",  "5h 30m • Đi thẳng",   "Xe Giường Nằm",     "Chỉ còn 3 chỗ", "250.000đ"),
    BusTrip("22:00 CH", "03:30 SA", "Đà Nẵng", "Quy Nhơn",  "5h 30m • Đêm",        "Xe Giường Nằm VIP", "Còn nhiều chỗ", "280.000đ"),
    BusTrip("07:30 SA", "09:30 SA", "Đà Nẵng", "Quảng Ngãi","2h 00m • Đi thẳng",   "Xe Khách Đi Thẳng", "Còn nhiều chỗ", "120.000đ"),
    BusTrip("06:30 SA", "08:00 SA", "Đà Nẵng", "Tam Kỳ",    "1h 30m • Đi thẳng",   "Xe Khách Đi Thẳng", "Còn nhiều chỗ", "80.000đ"),
    BusTrip("07:00 SA", "09:30 SA", "Huế",      "Đà Nẵng",   "2h 30m • Đi thẳng",  "Xe Khách Đi Thẳng", "Sắp hết chỗ",   "150.000đ"),
    BusTrip("13:00 CH", "15:30 CH", "Huế",      "Đà Nẵng",   "2h 30m • Đi thẳng",  "Xe Giường Nằm",     "Còn nhiều chỗ", "180.000đ"),
    BusTrip("09:00 SA", "10:30 SA", "Hội An",   "Đà Nẵng",   "0h 45m • Đi thẳng",  "Xe Khách Đi Thẳng", "Còn nhiều chỗ", "50.000đ"),
    BusTrip("08:00 SA", "01:30 CH", "Huế",      "Quy Nhơn",  "5h 30m • Đi thẳng",  "Xe Giường Nằm",     "Còn nhiều chỗ", "280.000đ"),
    BusTrip("08:00 SA", "10:00 SA", "Hội An",   "Huế",       "2h 00m • Đi thẳng",  "Xe Khách Đi Thẳng", "Còn nhiều chỗ", "130.000đ"),
)

// ---------------------------------------------------------------------------
// BusListScreen
// ---------------------------------------------------------------------------
@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusListScreen(
    fromLocation: String,
    toLocation: String,
    selectedDate: String,
    onBackClick: () -> Unit,
    onBusClick: () -> Unit
) {
    val backgroundColor = Color(0xFFF5F7FA)
    var currentDate by remember { mutableStateOf(selectedDate) }

    // Filter trips — so sánh không phân biệt hoa thường, có thể nhập một phần tên
    val fromTrimmed = fromLocation.trim()
    val toTrimmed = toLocation.trim()
    val filteredTrips = ALL_TRIPS.filter {
        (fromTrimmed.isEmpty() || it.from.contains(fromTrimmed, ignoreCase = true)) &&
        (toTrimmed.isEmpty() || it.to.contains(toTrimmed, ignoreCase = true))
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("$fromLocation → $toLocation", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                        Text(currentDate, fontSize = 14.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(40.dp)
                            .background(Color.White, CircleShape)
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color(0xFF1F2937))
                    }
                },
                actions = { Spacer(modifier = Modifier.width(56.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Horizontal Date Selector
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(DATES) { date ->
                    DateChip(text = date, isSelected = date == currentDate, onClick = { currentDate = date })
                }
            }

            // Header and Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chuyến đi (${filteredTrips.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Row(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .clickable { },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Tune, contentDescription = "Bộ lọc", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Bộ lọc", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1F2937))
                }
            }

            // Bus List
            if (filteredTrips.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("😔", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Không có chuyến xe\n$fromLocation → $toLocation",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredTrips) { trip ->
                        BusTicketCard(
                            departureTime = trip.departureTime,
                            arrivalTime = trip.arrivalTime,
                            departureLocation = trip.from,
                            arrivalLocation = trip.to,
                            duration = trip.duration,
                            busType = trip.busType,
                            statusText = trip.statusText,
                            price = trip.price,
                            onClick = onBusClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateChip(text: String, isSelected: Boolean, onClick: () -> Unit = {}) {
    val bgColor = if (isSelected) PrimaryBlue else Color.Transparent
    val textColor = if (isSelected) Color.White else Color.Gray

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
