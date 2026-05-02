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

import androidx.lifecycle.viewmodel.compose.viewModel
import com.khaiqueng_finalterm.busticketbooking.ui.viewmodels.BusListViewModel
import com.khaiqueng_finalterm.busticketbooking.ui.viewmodels.BusListUiState
import java.text.NumberFormat
import java.util.Locale

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
    
    val viewModel: BusListViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Gọi API khi tham số thay đổi
    LaunchedEffect(fromLocation, toLocation, currentDate) {
        viewModel.searchTrips(fromLocation, toLocation, currentDate)
    }

    val formatCurrency = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

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
                val tripCount = if (uiState is BusListUiState.Success) (uiState as BusListUiState.Success).trips.size else 0
                Text(
                    text = "Chuyến đi ($tripCount)",
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

            when (val state = uiState) {
                is BusListUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
                is BusListUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = Color.Red, fontSize = 16.sp)
                    }
                }
                is BusListUiState.Success -> {
                    if (state.trips.isEmpty()) {
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
                            items(state.trips) { trip ->
                                // Chuyển đổi thời gian backend (vd: "2026-05-09T08:00:00") sang "08:00"
                                val depTimeStr = trip.departureTime.split("T").lastOrNull()?.take(5) ?: trip.departureTime
                                val arrTimeStr = trip.arrivalTime.split("T").lastOrNull()?.take(5) ?: trip.arrivalTime
                                
                                val statusText = if (trip.availableSeats > 5) "Còn nhiều chỗ" 
                                                 else if (trip.availableSeats > 0) "Chỉ còn ${trip.availableSeats} chỗ" 
                                                 else "Đã hết chỗ"

                                BusTicketCard(
                                    departureTime = depTimeStr,
                                    arrivalTime = arrTimeStr,
                                    departureLocation = trip.departureLocation.name,
                                    arrivalLocation = trip.arrivalLocation.name,
                                    duration = trip.duration,
                                    busType = trip.bus.type,
                                    statusText = statusText,
                                    price = formatCurrency.format(trip.price),
                                    onClick = onBusClick
                                )
                            }
                        }
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
