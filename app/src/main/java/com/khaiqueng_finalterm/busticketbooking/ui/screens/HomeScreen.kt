package com.khaiqueng_finalterm.busticketbooking.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khaiqueng_finalterm.busticketbooking.data.repository.AuthSession
import com.khaiqueng_finalterm.busticketbooking.data.repository.BookingSession
import com.khaiqueng_finalterm.busticketbooking.R
import com.khaiqueng_finalterm.busticketbooking.ui.components.OSMapView
import com.khaiqueng_finalterm.busticketbooking.ui.theme.PrimaryBlue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import java.text.SimpleDateFormat
import java.util.*


// Data

val LOCATIONS = listOf("Đà Nẵng", "Huế", "Hội An", "Quy Nhơn", "Quảng Ngãi", "Tam Kỳ")
val DATES = listOf("07 Th04", "08 Th04", "09 Th04", "10 Th04", "11 Th04", "12 Th04", "13 Th04")

// 1. Core Home Screen Orchestrator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSearchClick: (String, String, String) -> Unit,
    onLogoutClick: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        bottomBar = {
            StandardBottomNavigationBar(
                selectedIndex = selectedTabIndex,
                onItemSelected = { selectedTabIndex = it }
            )
        }
    ) { paddingValues ->
        when (selectedTabIndex) {
            0 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item { HeroAndSearchSection(onSearchClick = onSearchClick) }
                    item { TrustBadgesSection() }
                    item { PopularRoutesSection() }
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
            1 -> MyTicketsTab(modifier = Modifier.padding(paddingValues))
            2 -> DestinationsTab(modifier = Modifier.padding(paddingValues))
            3 -> AccountTab(
                modifier = Modifier.padding(paddingValues),
                onLogoutClick = onLogoutClick
            )
        }
    }
}


// 2. Hero Image and Overlapping Search Card

@Composable
fun HeroAndSearchSection(onSearchClick: (String, String, String) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.bus),
                    contentDescription = "Xe khách",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            ),
                            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                        )
                )
                HomeHeaderOverlay()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 220.dp)
                .padding(horizontal = 24.dp)
        ) {
            AdvancedSearchCard(onSearchClick = onSearchClick)
        }
    }
}

// 3. Header Overlay

@Composable
fun HomeHeaderOverlay() {
    var locationExpanded by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf("Đà Nẵng, Việt Nam") }

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Chào buổi sáng! ☀️"
            in 12..17 -> "Chào buổi chiều! 🌤️"
            else -> "Chào buổi tối! 🌙"
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(text = greeting, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { locationExpanded = true }
            ) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Vị trí", tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = currentLocation, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium)
                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Xổ xuống", tint = Color.White, modifier = Modifier.size(16.dp))
                DropdownMenu(expanded = locationExpanded, onDismissRequest = { locationExpanded = false }) {
                    listOf("Đà Nẵng, Việt Nam", "Huế, Việt Nam", "Hội An, Việt Nam").forEach { loc ->
                        DropdownMenuItem(text = { Text(loc) }, onClick = { currentLocation = loc; locationExpanded = false })
                    }
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = "Thông báo", tint = Color(0xFF1F2937), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Image(
                painter = painterResource(id = R.drawable.user_avatar),
                contentDescription = "Ảnh đại diện",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(40.dp).clip(CircleShape).border(2.dp, Color.White, CircleShape)
            )
        }
    }
}


// 4. Advanced Search Card with TextField + Calendar Date Picker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedSearchCard(onSearchClick: (String, String, String) -> Unit) {
    // Dùng TextFieldValue để giữ composing state — fix lỗi gõ tiếng Việt có dấu
    var fromLocation by remember { mutableStateOf(TextFieldValue("Đà Nẵng")) }
    var toLocation by remember { mutableStateOf(TextFieldValue("Huế")) }
    var selectedDateLabel by remember { mutableStateOf("09 Th04 2026") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val sdf = SimpleDateFormat("dd 'Th'MM yyyy", Locale("vi"))
                        selectedDateLabel = sdf.format(Date(millis))
                    }
                }) { Text("Chọn", color = PrimaryBlue, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Hủy", color = Color.Gray) }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                titleContentColor = PrimaryBlue,
                headlineContentColor = PrimaryBlue,
                weekdayContentColor = Color.Gray,
                dayContentColor = Color(0xFF1F2937),
                selectedDayContainerColor = PrimaryBlue,
                selectedDayContentColor = Color.White,
                todayContentColor = PrimaryBlue,
                todayDateBorderColor = PrimaryBlue
            )
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp), ambientColor = PrimaryBlue.copy(alpha = 0.1f), spotColor = PrimaryBlue.copy(alpha = 0.1f))
            .border(1.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Location Inputs with Swap
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(end = 52.dp)) {
                    // FROM text field
                    Text("Nơi xuất phát", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = fromLocation,
                        onValueChange = { fromLocation = it },
                        placeholder = { Text("Ví dụ: Đà Nẵng", color = Color.LightGray) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE5E7EB).copy(alpha = 0.5f),
                            focusedBorderColor = PrimaryBlue,
                            unfocusedContainerColor = Color.White.copy(alpha = 0.5f),
                            focusedContainerColor = Color.White.copy(alpha = 0.9f),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // TO text field
                    Text("Nơi đến", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = toLocation,
                        onValueChange = { toLocation = it },
                        placeholder = { Text("Ví dụ: Huế", color = Color.LightGray) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE5E7EB).copy(alpha = 0.5f),
                            focusedBorderColor = PrimaryBlue,
                            unfocusedContainerColor = Color.White.copy(alpha = 0.5f),
                            focusedContainerColor = Color.White.copy(alpha = 0.9f),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        singleLine = true
                    )
                }

                // Swap Button (aligned to the right center)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(44.dp)
                        .shadow(4.dp, CircleShape)
                        .background(Color.White, CircleShape)
                        .clickable {
                            val temp = fromLocation
                            fromLocation = toLocation
                            toLocation = temp
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.SwapVert, contentDescription = "Đổi chiều", tint = PrimaryBlue)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Date Selection — bấm mở Calendar Dialog
            Text("Ngày đi", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                    .clickable { showDatePicker = true }
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = selectedDateLabel, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1F2937), modifier = Modifier.weight(1f))
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onSearchClick(fromLocation.text, toLocation.text, selectedDateLabel) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Tìm kiếm", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LocationDropdownField(
    label: String,
    value: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    Box {
        Column(modifier = Modifier.clickable { onExpandChange(true) }) {
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1F2937))
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { onExpandChange(false) }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontWeight = if (option == value) FontWeight.Bold else FontWeight.Normal) },
                    onClick = { onSelect(option); onExpandChange(false) }
                )
            }
        }
    }
}


@Composable
fun TrustBadgesSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 24.dp)
            .background(Color(0xFF1E3A8A), RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TrustBadgeItem(Icons.Default.VerifiedUser, "chắc chắn có chỗ")
        TrustBadgeItem(Icons.Default.HeadsetMic, "Hỗ trợ 24/7")
        TrustBadgeItem(Icons.Default.LocalOffer, "Nhiều ưu đãi")
    }
}

@Composable
fun TrustBadgeItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFFFACC15), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = text, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

data class RouteDestination(val name: String, val price: String, val imageRes: Int)

@Composable
fun PopularRoutesSection() {
    val routes = listOf(
        RouteDestination("Hội An - Huế", "Từ 270.000đ", R.drawable.hue),
        RouteDestination("Hội An - Đà Nẵng", "Từ 120.000đ", R.drawable.danang),
        RouteDestination("Huế - Quy Nhơn", "Từ 249.000đ", R.drawable.quynhon)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Tuyến đường phổ biến", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
        }

        Spacer(modifier = Modifier.height(16.dp))

        androidx.compose.foundation.lazy.LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(routes.size) { index ->
                val route = routes[index]
                Card(
                    modifier = Modifier.width(200.dp).height(140.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = route.imageRes),
                            contentDescription = route.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            Text(text = route.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = route.price, fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                        }
                    }
                }
            }
        }
    }
}

data class DestinationMapPoint(
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)

@Composable
fun MyTicketsTab(modifier: Modifier = Modifier) {
    val booking = BookingSession.lastBookingResponse
    val trip = BookingSession.selectedTrip
    val seats = BookingSession.selectedSeatNumbers.joinToString(", ").ifBlank { "Chưa có" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(
            title = "Vé của tôi",
            subtitle = "Thông tin vé gần nhất trong phiên hiện tại"
        )

        if (booking == null) {
            EmptyStateCard(
                icon = Icons.Default.ConfirmationNumber,
                title = "Chưa có vé để hiển thị",
                subtitle = "Sau khi đặt vé hoặc thanh toán thành công, thông tin vé sẽ xuất hiện tại đây."
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Mã vé #${booking.id}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        StatusPill(booking.status)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    InfoRow("Tuyến", booking.tripRoute)
                    InfoRow("Khởi hành", booking.departureTime.replace("T", " "))
                    InfoRow("Ghế", seats)
                    InfoRow("Biển số xe", trip?.bus?.licensePlate ?: "Đang cập nhật")
                    InfoRow("Tổng tiền", "%,.0fđ".format(booking.totalAmount), PrimaryBlue)
                }
            }
        }
    }
}

@Composable
fun DestinationsTab(modifier: Modifier = Modifier) {
    val points = remember {
        listOf(
            DestinationMapPoint("Đà Nẵng", "Trung tâm du lịch ven biển miền Trung", 16.047079, 108.206230),
            DestinationMapPoint("Huế", "Cố đô với nhiều điểm tham quan lịch sử", 16.463713, 107.590866),
            DestinationMapPoint("Hội An", "Phố cổ, ẩm thực và trải nghiệm văn hóa", 15.880058, 108.338047),
            DestinationMapPoint("Quy Nhơn", "Biển xanh và các tuyến du lịch nghỉ dưỡng", 13.782967, 109.219663)
        )
    }
    var selectedPoint by remember { mutableStateOf(points.first()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(
            title = "Điểm đến",
            subtitle = "Bản đồ tương tác dùng dữ liệu OpenStreetMap"
        )

        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(points.size) { index ->
                val point = points[index]
                FilterChip(
                    selected = point == selectedPoint,
                    onClick = { selectedPoint = point },
                    label = { Text(point.name) },
                    leadingIcon = if (point == selectedPoint) {
                        { Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryBlue.copy(alpha = 0.14f),
                        selectedLabelColor = PrimaryBlue
                    )
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            OSMapView(
                modifier = Modifier.fillMaxSize(),
                latitude = selectedPoint.latitude,
                longitude = selectedPoint.longitude,
                zoomLevel = 13.0,
                markerTitle = selectedPoint.name
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(selectedPoint.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                Spacer(modifier = Modifier.height(6.dp))
                Text(selectedPoint.description, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun AccountTab(
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val user = AuthSession.user

    if (showLogoutDialog) {
        LogoutDialog(
            onConfirm = {
                showLogoutDialog = false
                onLogoutClick()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(
            title = "Tài khoản",
            subtitle = "Thông tin đăng nhập hiện tại"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(PrimaryBlue.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(30.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user?.name ?: "Người dùng", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                        Text(user?.email ?: "Chưa có email", fontSize = 13.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                InfoRow("Vai trò", user?.role ?: "CUSTOMER")
                InfoRow("Số điện thoại", user?.phone ?: "Chưa cập nhật")
            }
        }

        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Đăng xuất", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
        Spacer(modifier = Modifier.height(6.dp))
        Text(subtitle, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
private fun EmptyStateCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(42.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
            Spacer(modifier = Modifier.height(6.dp))
            Text(subtitle, fontSize = 14.sp, color = Color.Gray, lineHeight = 20.sp)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, valueColor: Color = Color(0xFF1F2937)) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

@Composable
private fun StatusPill(status: String) {
    val isConfirmed = status == "CONFIRMED"
    val color = if (isConfirmed) Color(0xFF16A34A) else PrimaryBlue
    Text(
        text = status,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
}

@Composable
private fun LogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(40.dp)
            )
        },
        title = {
            Text(
                "Đăng xuất",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF1F2937)
            )
        },
        text = {
            Text(
                "Bạn có chắc chắn muốn đăng xuất không?",
                color = Color.Gray,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Đăng xuất", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Huỷ", color = Color.Gray)
            }
        }
    )
}


// 6. Floating Bottom Navigation Bar

@Composable
fun StandardBottomNavigationBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val navItems = listOf(
        Pair("Trang Chủ", Icons.Default.Home),
        Pair("Vé của tôi", Icons.Default.ConfirmationNumber),
        Pair("Điểm đến", Icons.Default.LocationOn),
        Pair("Tài khoản", Icons.Default.Person)
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = PrimaryBlue,
        tonalElevation = 8.dp
    ) {
        navItems.forEachIndexed { index, pair ->
            val label = pair.first
            val icon = pair.second
            val isSelected = selectedIndex == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(index) },
                icon = { Icon(imageVector = icon, contentDescription = label) },
                label = { Text(text = label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = PrimaryBlue,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = PrimaryBlue.copy(alpha = 0.1f)
                )
            )
        }
    }
}
