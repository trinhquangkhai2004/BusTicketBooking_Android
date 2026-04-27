package com.khaiqueng_finalterm.busticketbooking.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.khaiqueng_finalterm.busticketbooking.R
import com.khaiqueng_finalterm.busticketbooking.ui.theme.PrimaryBlue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import java.text.SimpleDateFormat
import java.util.*

// ---------------------------------------------------------------------------
// Data
// ---------------------------------------------------------------------------
val LOCATIONS = listOf("Đà Nẵng", "Huế", "Hội An", "Quy Nhơn", "Quảng Ngãi", "Tam Kỳ")
val DATES = listOf("07 Th04", "08 Th04", "09 Th04", "10 Th04", "11 Th04", "12 Th04", "13 Th04")

// ---------------------------------------------------------------------------
// 1. Core Home Screen Orchestrator
// ---------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSearchClick: (String, String, String) -> Unit,
    onLogoutClick: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        bottomBar = { FloatingBottomNavigationBar(onLogoutClick = onLogoutClick) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { HeroAndSearchSection(onSearchClick = onSearchClick) }
            item { PopularRoutesSection() }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

// ---------------------------------------------------------------------------
// 2. Hero Image and Overlapping Search Card
// ---------------------------------------------------------------------------
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
                    painter = painterResource(id = R.drawable.bus_hero),
                    contentDescription = "Xe khách",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f))
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
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

// ---------------------------------------------------------------------------
// 3. Header Overlay
// ---------------------------------------------------------------------------
@Composable
fun HomeHeaderOverlay() {
    var locationExpanded by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf("Đà Nẵng, Việt Nam") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(text = "Xin chào!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
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

// ---------------------------------------------------------------------------
// 4. Advanced Search Card with TextField + Calendar Date Picker
// ---------------------------------------------------------------------------
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
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp), ambientColor = PrimaryBlue.copy(alpha = 0.1f), spotColor = PrimaryBlue.copy(alpha = 0.1f)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Location Inputs with Swap
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(end = 52.dp)) {
                    // FROM text field
                    Text("Điểm đi", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = fromLocation,
                        onValueChange = { fromLocation = it },
                        placeholder = { Text("Ví dụ: Đà Nẵng", color = Color.LightGray) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            autoCorrect = false,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            focusedBorderColor = PrimaryBlue,
                            unfocusedContainerColor = Color(0xFFF9FAFB),
                            focusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // TO text field
                    Text("Điểm đến", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = toLocation,
                        onValueChange = { toLocation = it },
                        placeholder = { Text("Ví dụ: Huế", color = Color.LightGray) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            autoCorrect = false,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            focusedBorderColor = PrimaryBlue,
                            unfocusedContainerColor = Color(0xFFF9FAFB),
                            focusedContainerColor = Color.White
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

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSearchClick(fromLocation.text, toLocation.text, selectedDateLabel) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Tìm chuyến xe", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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

// ---------------------------------------------------------------------------
// 5. Popular Routes Section
// ---------------------------------------------------------------------------
@Composable
fun PopularRoutesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Tuyến đường phổ biến", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
            Text(text = "Xem tất cả", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.clickable { })
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth().height(180.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.map_placeholder),
                    contentDescription = "Bản đồ tuyến đường",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Ghim",
                    tint = PrimaryBlue,
                    modifier = Modifier.align(Alignment.Center).size(48.dp).shadow(4.dp, CircleShape, clip = false)
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 6. Floating Bottom Navigation Bar
// ---------------------------------------------------------------------------
@Composable
fun FloatingBottomNavigationBar(onLogoutClick: () -> Unit) {
    var selectedIndex by remember { mutableStateOf(0) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val navItems = listOf(
        Icons.Default.Home,
        Icons.Default.ConfirmationNumber,
        Icons.Default.PinDrop,
        Icons.Default.Person
    )

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = PrimaryBlue,
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
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Đăng xuất", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Huỷ", color = Color.Gray)
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 24.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(32.dp))
                .background(Color.White, RoundedCornerShape(32.dp))
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, icon ->
                val isSelected = selectedIndex == index
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) PrimaryBlue else Color.Transparent)
                        .clickable {
                            if (index == 3) {
                                // Person tab → show logout dialog
                                showLogoutDialog = true
                            } else {
                                selectedIndex = index
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Nav $index",
                        tint = if (isSelected) Color.White else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}