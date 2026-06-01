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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khaiqueng_finalterm.busticketbooking.ui.theme.PrimaryBlue
import com.khaiqueng_finalterm.busticketbooking.ui.theme.SecondaryOrange
import com.khaiqueng_finalterm.busticketbooking.data.repository.BookingSession

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBackClick: () -> Unit,
    onSelectSeatsClick: () -> Unit
) {
    val backgroundColor = Color(0xFFF5F7FA)

    // Lấy thông tin chuyến đi đã chọn từ BookingSession
    val trip = BookingSession.selectedTrip
    val basePrice = trip?.price ?: 120000.0
    val selectedSeatsCount = BookingSession.selectedSeatIds.size
    
    // Nếu chưa chọn ghế nào thì tính mặc định là 1 ghế để hiển thị tạm tính
    val seatsPrice = if (selectedSeatsCount > 0) basePrice * selectedSeatsCount else basePrice

    // Dịch vụ thêm — đồng bộ trạng thái từ BookingSession
    var hasLuggage by remember { mutableStateOf(BookingSession.hasLuggage) }
    var hasInsurance by remember { mutableStateOf(BookingSession.hasInsurance) }
    var hasMeal by remember { mutableStateOf(BookingSession.hasMeal) }
    var hasPickup by remember { mutableStateOf(BookingSession.hasPickup) }

    // Cập nhật ngược lại BookingSession khi thay đổi trạng thái dịch vụ thêm
    LaunchedEffect(hasLuggage, hasInsurance, hasMeal, hasPickup) {
        BookingSession.hasLuggage = hasLuggage
        BookingSession.hasInsurance = hasInsurance
        BookingSession.hasMeal = hasMeal
        BookingSession.hasPickup = hasPickup
    }

    val luggagePrice = 20000.0
    val insurancePrice = 15000.0
    val mealPrice = 35000.0
    val pickupPrice = 50000.0

    val totalExtras = (if (hasLuggage) luggagePrice else 0.0) +
            (if (hasInsurance) insurancePrice else 0.0) +
            (if (hasMeal) mealPrice else 0.0) +
            (if (hasPickup) pickupPrice else 0.0)
            
    val totalAmount = seatsPrice + totalExtras

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
            // Order Summary + CTA Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Price breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (selectedSeatsCount > 0) "Giá vé gốc ($selectedSeatsCount ghế)" else "Giá vé gốc", 
                        fontSize = 14.sp, 
                        color = Color.Gray
                    )
                    Text(
                        text = "%,.0fđ".format(seatsPrice),
                        fontSize = 14.sp,
                        color = Color(0xFF1F2937)
                    )
                }
                if (totalExtras > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Dịch vụ thêm", fontSize = 14.sp, color = Color.Gray)
                        Text(
                            text = "+%,.0fđ".format(totalExtras),
                            fontSize = 14.sp,
                            color = SecondaryOrange,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE5E7EB))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Tổng cộng", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = "%,.0fđ".format(totalAmount),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                    Button(
                        onClick = onSelectSeatsClick,
                        modifier = Modifier.height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.EventSeat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedSeatsCount > 0) "Chọn lại ghế" else "Chọn ghế", 
                            fontSize = 16.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // ── 1. Route Summary Card ──────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Header hành trình
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Thông tin chuyến đi", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE3F2FD)
                        ) {
                            Text(
                                text = trip?.bus?.type ?: "Ghế ngồi", 
                                fontSize = 12.sp, 
                                color = PrimaryBlue, 
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFF3F4F6))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Timeline route
                    val depTimeStr = trip?.departureTime?.split("T")?.lastOrNull()?.take(5) ?: "06:00"
                    val arrTimeStr = trip?.arrivalTime?.split("T")?.lastOrNull()?.take(5) ?: "08:30"
                    
                    Row(verticalAlignment = Alignment.Top) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 4.dp, end = 16.dp)
                        ) {
                            Box(modifier = Modifier.size(10.dp).background(SecondaryOrange, CircleShape))
                            Box(modifier = Modifier.width(2.dp).height(36.dp).background(Color(0xFFE5E7EB)))
                            Box(modifier = Modifier.size(10.dp).background(PrimaryBlue, CircleShape))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(trip?.departureLocation?.name ?: "Đà Nẵng", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                            Text("$depTimeStr · Bến xe Trung tâm ${trip?.departureLocation?.name ?: "Đà Nẵng"}", fontSize = 13.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(trip?.arrivalLocation?.name ?: "Huế", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                            Text("$arrTimeStr · Bến xe phía Nam ${trip?.arrivalLocation?.name ?: "Huế"}", fontSize = 13.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFF3F4F6))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Trip meta info
                    val rawDate = trip?.departureTime?.split("T")?.firstOrNull() ?: "2026-05-11"
                    val displayDate = try {
                        val inputSdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        val outputSdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                        val parsed = inputSdf.parse(rawDate)
                        if (parsed != null) outputSdf.format(parsed) else rawDate
                    } catch (e: Exception) {
                        rawDate
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TripMetaChip(icon = Icons.Default.CalendarToday, text = displayDate)
                        TripMetaChip(icon = Icons.Default.Person, text = if (selectedSeatsCount > 0) "$selectedSeatsCount hành khách" else "1 hành khách")
                        TripMetaChip(icon = Icons.Default.ConfirmationNumber, text = "%,.0fđ/ghế".format(basePrice))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── 2. Seat Selection ─────────────────────────────────
            SectionHeader(step = "1", title = "Chọn ghế ngồi", subtitle = "Bấm nút bên dưới để chọn chỗ ngồi ưng ý")
            Spacer(modifier = Modifier.height(14.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                onClick = onSelectSeatsClick
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.EventSeat, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        val selectedSeatText = if (selectedSeatsCount > 0) {
                            "Đã chọn ghế: ${BookingSession.selectedSeatNumbers.joinToString(", ")}"
                        } else {
                            "Chưa chọn ghế"
                        }
                        Text(selectedSeatText, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (selectedSeatsCount > 0) SecondaryOrange else Color(0xFF1F2937))
                        Text(
                            text = if (selectedSeatsCount > 0) "Nhấn để thay đổi lựa chọn ghế" else "Xem sơ đồ và chọn chỗ ngồi ưng ý nhất", 
                            fontSize = 13.sp, 
                            color = Color.Gray
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── 3. Extra Services ─────────────────────────────────
            SectionHeader(step = "2", title = "Dịch vụ thêm", subtitle = "Tăng thêm trải nghiệm chuyến đi của bạn")
            Spacer(modifier = Modifier.height(14.dp))

            ExtraServiceCard(
                icon = Icons.Default.Luggage,
                title = "Hành lý thêm",
                subtitle = "Tối đa 20kg hành lý bổ sung",
                price = "+20.000đ",
                isChecked = hasLuggage,
                onToggle = { hasLuggage = !hasLuggage }
            )
            Spacer(modifier = Modifier.height(10.dp))
            ExtraServiceCard(
                icon = Icons.Default.HealthAndSafety,
                title = "Bảo hiểm chuyến đi",
                subtitle = "Bồi thường tối đa 50 triệu đồng",
                price = "+15.000đ",
                isChecked = hasInsurance,
                onToggle = { hasInsurance = !hasInsurance }
            )
            Spacer(modifier = Modifier.height(10.dp))
            ExtraServiceCard(
                icon = Icons.Default.Restaurant,
                title = "Suất ăn trên xe",
                subtitle = "Hộp cơm gà / hộp cơm chay",
                price = "+35.000đ",
                isChecked = hasMeal,
                onToggle = { hasMeal = !hasMeal }
            )
            Spacer(modifier = Modifier.height(10.dp))
            ExtraServiceCard(
                icon = Icons.Default.LocalTaxi,
                title = "Đưa đón tận nơi",
                subtitle = "Bán kính tối đa 5km từ bến xe",
                price = "+50.000đ",
                isChecked = hasPickup,
                onToggle = { hasPickup = !hasPickup }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ── Composables hỗ trợ ────────────────────────────────────────────────────

@Composable
fun TripMetaChip(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun ExtraServiceCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    price: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isChecked) 1.5.dp else 0.dp,
                color = if (isChecked) PrimaryBlue else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onToggle() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isChecked) Color(0xFFF0F7FF) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = if (isChecked) PrimaryBlue.copy(alpha = 0.1f) else Color(0xFFF3F4F6),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isChecked) PrimaryBlue else Color.Gray,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1F2937))
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = price,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isChecked) PrimaryBlue else Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Switch(
                    checked = isChecked,
                    onCheckedChange = { onToggle() },
                    modifier = Modifier.height(24.dp),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryBlue,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFE5E7EB)
                    )
                )
            }
        }
    }
}

@Composable
fun SectionHeader(step: String, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(PrimaryBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(step, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
            Text(subtitle, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

@Composable
fun PillTextField(value: androidx.compose.ui.text.input.TextFieldValue, onValueChange: (androidx.compose.ui.text.input.TextFieldValue) -> Unit, hint: String) {
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
            modifier = Modifier.fillMaxWidth().padding(20.dp),
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
