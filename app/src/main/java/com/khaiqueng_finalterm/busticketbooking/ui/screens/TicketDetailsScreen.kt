package com.khaiqueng_finalterm.busticketbooking.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khaiqueng_finalterm.busticketbooking.ui.theme.PrimaryBlue

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailsScreen(
    onHomeClick: () -> Unit
) {
    val backgroundColor = PrimaryBlue
    
    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Vé của bạn", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .padding(24.dp)
            ) {
                Button(
                    onClick = onHomeClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Về Trang Chủ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Thanh toán thành công!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Đây là vé điện tử của bạn. Vui lòng xuất trình khi lên xe.", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
            
            Spacer(modifier = Modifier.height(32.dp))

            // Boarding Pass Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Top Section
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("TRIPWAY BUS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                            Text("VIP-8D", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Đà Nẵng", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                                Text("08:40 SA", fontSize = 14.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("2h 30m", fontSize = 12.sp, color = Color.Gray)
                                Icon(Icons.Default.ArrowRightAlt, contentDescription = null, tint = PrimaryBlue)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Huế", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                                Text("11:10 SA", fontSize = 14.sp, color = Color.Gray)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Ngày đi", fontSize = 12.sp, color = Color.Gray)
                                Text("09 Th04 2026", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Biển số xe", fontSize = 12.sp, color = Color.Gray)
                                Text("43B-567.89", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                            }
                        }
                    }

                    // Dashed Line Separator
                    Box(modifier = Modifier.fillMaxWidth().height(24.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            drawLine(
                                color = Color.LightGray,
                                start = Offset(0f, size.height / 2),
                                end = Offset(size.width, size.height / 2),
                                strokeWidth = 2f,
                                pathEffect = pathEffect
                            )
                        }
                        // Left cutout
                        Box(modifier = Modifier.size(24.dp).align(Alignment.CenterStart).offset(x = (-12).dp).background(PrimaryBlue, CircleShape))
                        // Right cutout
                        Box(modifier = Modifier.size(24.dp).align(Alignment.CenterEnd).offset(x = 12.dp).background(PrimaryBlue, CircleShape))
                    }

                    // Bottom Section
                    Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Hành khách", fontSize = 12.sp, color = Color.Gray)
                                Text("Nguyễn Văn A", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Ghế", fontSize = 12.sp, color = Color.Gray)
                                Text("8D", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Fake QR Code
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "QR Code",
                            modifier = Modifier.size(120.dp),
                            tint = Color(0xFF1F2937)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Scan QR để lên xe", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}
