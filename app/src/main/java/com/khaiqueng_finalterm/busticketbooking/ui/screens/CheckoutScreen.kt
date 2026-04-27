package com.khaiqueng_finalterm.busticketbooking.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.TextFieldValue
import com.khaiqueng_finalterm.busticketbooking.ui.theme.PrimaryBlue
import com.khaiqueng_finalterm.busticketbooking.ui.theme.SecondaryOrange

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBackClick: () -> Unit,
    onSelectSeatsClick: () -> Unit
) {
    val backgroundColor = Color(0xFFF5F7FA)
    var firstName by remember { mutableStateOf(TextFieldValue("")) }
    var lastName by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chi tiết chuyến đi", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color(0xFF1F2937),
                        modifier = Modifier.fillMaxWidth().padding(end = 48.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                    onClick = onSelectSeatsClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Tiếp tục Chọn ghế", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            // Route Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Timeline Graphics
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Box(modifier = Modifier.size(12.dp).background(SecondaryOrange, CircleShape))
                        Box(modifier = Modifier.width(2.dp).height(40.dp).background(Color(0xFFE5E7EB)))
                        Box(modifier = Modifier.size(12.dp).background(PrimaryBlue, CircleShape))
                    }

                    // Locations
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Từ", fontSize = 12.sp, color = Color.Gray)
                        Text("Đà Nẵng, Việt Nam", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Đến", fontSize = 12.sp, color = Color.Gray)
                        Text("Huế, Việt Nam", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                    }

                    // Swap Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, CircleShape)
                            .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                            .clickable { /* Swap */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.SwapVert, contentDescription = "Swap", tint = PrimaryBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Passenger Details Section
            SectionHeader(step = "1", title = "Thông tin hành khách", subtitle = "Thông tin bắt buộc")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Người lớn", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
            Spacer(modifier = Modifier.height(12.dp))
            PillTextField(value = firstName, onValueChange = { firstName = it }, hint = "Tên")
            Spacer(modifier = Modifier.height(12.dp))
            PillTextField(value = lastName, onValueChange = { lastName = it }, hint = "Họ")

            Spacer(modifier = Modifier.height(32.dp))

            // Seat Selection Section
            SectionHeader(step = "2", title = "Chọn ghế ngồi", subtitle = "Chọn chỗ ngồi ưng ý")
            Spacer(modifier = Modifier.height(16.dp))
            ActionRow(
                icon = Icons.Default.EventSeat,
                title = "Chọn ghế của bạn",
                subtitle = "Từ 50.000đ",
                onClick = onSelectSeatsClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Extras Section
            SectionHeader(step = "3", title = "Dịch vụ thêm", subtitle = "Các dịch vụ khác")
            Spacer(modifier = Modifier.height(16.dp))
            ActionRow(
                icon = Icons.Default.Luggage,
                title = "Thêm hành lý",
                subtitle = "Từ 20.000đ",
                onClick = { /* Handle luggage */ }
            )
        }
    }
}

@Composable
fun SectionHeader(step: String, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFE3F2FD), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(step, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
            Text(subtitle, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun PillTextField(value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit, hint: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.White,
            focusedBorderColor = PrimaryBlue,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
fun ActionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                Text(subtitle, fontSize = 14.sp, color = Color.Gray)
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Go", tint = Color.Gray)
        }
    }
}
