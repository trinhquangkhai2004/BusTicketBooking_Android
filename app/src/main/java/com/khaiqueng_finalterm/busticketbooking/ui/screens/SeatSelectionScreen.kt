package com.khaiqueng_finalterm.busticketbooking.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.EventSeat
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
import com.khaiqueng_finalterm.busticketbooking.ui.theme.PrimaryBlue
import com.khaiqueng_finalterm.busticketbooking.ui.theme.SecondaryOrange

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val backgroundColor = Color(0xFFF5F7FA)
    var selectedSeat by remember { mutableStateOf<String?>("8D") }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chọn Ghế", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color(0xFF1F2937),
                        modifier = Modifier.fillMaxWidth().padding(end = 48.dp),
                        textAlign = TextAlign.Center
                    )
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
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Back", tint = Color(0xFF1F2937))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Button(
                    onClick = onContinueClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Tiếp tục", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            // Passenger Selected Seat Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PrimaryBlue, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("1. Hành khách 1", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Ghế thường • ${selectedSeat ?: "Chưa chọn"}", fontSize = 14.sp, color = Color.Gray)
                    }
                    if (selectedSeat != null) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = SecondaryOrange)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem("Trống", PrimaryBlue)
                LegendItem("Đã đặt", Color.LightGray)
                LegendItem("Đang chọn", SecondaryOrange)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Seat Layout Header (A B C   D E F)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text("A", fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                    Text("B", fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                    Text("C", fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                }
                Spacer(modifier = Modifier.width(32.dp)) // Middle aisle for numbers
                Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text("D", fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                    Text("E", fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                    Text("F", fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seat Grid
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(11) { rowIndex ->
                    val rowNumber = rowIndex + 1
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left seats (A, B, C)
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                            SeatIcon("${rowNumber}A", isBooked = rowIndex % 3 == 0, selectedSeat, onSelect = { selectedSeat = it })
                            SeatIcon("${rowNumber}B", isBooked = rowIndex % 4 == 0, selectedSeat, onSelect = { selectedSeat = it })
                            SeatIcon("${rowNumber}C", isBooked = false, selectedSeat, onSelect = { selectedSeat = it })
                        }
                        
                        // Middle Number
                        Box(modifier = Modifier.width(32.dp), contentAlignment = Alignment.Center) {
                            Text(rowNumber.toString(), fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                        }

                        // Right seats (D, E, F)
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                            SeatIcon("${rowNumber}D", isBooked = rowIndex % 5 == 0 && rowNumber != 8, selectedSeat, onSelect = { selectedSeat = it })
                            SeatIcon("${rowNumber}E", isBooked = rowIndex % 2 == 0, selectedSeat, onSelect = { selectedSeat = it })
                            SeatIcon("${rowNumber}F", isBooked = false, selectedSeat, onSelect = { selectedSeat = it })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.EventSeat, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SeatIcon(seatId: String, isBooked: Boolean, selectedSeat: String?, onSelect: (String) -> Unit) {
    val isSelected = seatId == selectedSeat
    val color = when {
        isSelected -> SecondaryOrange
        isBooked -> Color.LightGray
        else -> PrimaryBlue
    }

    IconButton(
        onClick = { if (!isBooked) onSelect(seatId) },
        modifier = Modifier.size(32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.EventSeat,
            contentDescription = "Seat $seatId",
            tint = color,
            modifier = Modifier.size(28.dp)
        )
    }
}
