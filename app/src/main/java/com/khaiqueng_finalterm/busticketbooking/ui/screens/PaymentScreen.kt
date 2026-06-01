package com.khaiqueng_finalterm.busticketbooking.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.khaiqueng_finalterm.busticketbooking.data.repository.BookingSession
import com.khaiqueng_finalterm.busticketbooking.ui.theme.PrimaryBlue
import com.khaiqueng_finalterm.busticketbooking.ui.viewmodels.BookingViewModel
import com.khaiqueng_finalterm.busticketbooking.ui.viewmodels.PaymentUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onBackClick: () -> Unit,
    onPaymentConfirmClick: () -> Unit,
    viewModel: BookingViewModel = viewModel()
) {
    var selectedMethod by remember { mutableStateOf("VNPay") }
    var vnpayUrl by remember { mutableStateOf<String?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var navigated by remember { mutableStateOf(false) }

    val paymentState by viewModel.paymentUiState.collectAsState()
    val trip = BookingSession.selectedTrip
    val tripId = trip?.id ?: 1L
    val basePrice = trip?.price ?: 120000.0
    val selectedSeatsCount = BookingSession.selectedSeatIds.size
    val seatsPrice = if (selectedSeatsCount > 0) basePrice * selectedSeatsCount else basePrice
    val totalExtras = BookingSession.getExtraServicesPrice()
    val totalAmount = seatsPrice + totalExtras

    LaunchedEffect(paymentState) {
        when (val state = paymentState) {
            is PaymentUiState.Error -> {
                vnpayUrl = null
                errorMessage = state.message
                showErrorDialog = true
            }
            is PaymentUiState.Success -> {
                if (!navigated) {
                    navigated = true
                    viewModel.stopPaymentStatusPolling()
                    onPaymentConfirmClick()
                }
            }
            else -> Unit
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopPaymentStatusPolling() }
    }

    if (vnpayUrl != null) {
        VnpayExternalPaymentScreen(
            paymentUrl = vnpayUrl!!,
            onBackClick = {
                vnpayUrl = null
                viewModel.stopPaymentStatusPolling()
                viewModel.resetPaymentState()
            }
        )
        return
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                viewModel.resetPaymentState()
            },
            title = { Text("Thanh toán thất bại", fontWeight = FontWeight.Bold) },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showErrorDialog = false
                        viewModel.resetPaymentState()
                    }
                ) {
                    Text("Đồng ý", color = PrimaryBlue)
                }
            }
        )
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Thanh toán",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFF5F7FA))
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = {
                        if (BookingSession.selectedSeatIds.isEmpty()) {
                            errorMessage = "Vui lòng chọn ghế trước khi thanh toán."
                            showErrorDialog = true
                            return@Button
                        }
                        if (selectedMethod != "VNPay") {
                            errorMessage = "Hiện tại chỉ hỗ trợ thanh toán qua VNPay."
                            showErrorDialog = true
                            return@Button
                        }
                        viewModel.startVnpayPayment(
                            tripId = tripId,
                            seatIds = BookingSession.selectedSeatIds,
                            extraServicesAmount = totalExtras,
                            onPaymentUrlReady = { payment -> vnpayUrl = payment.paymentUrl }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = paymentState !is PaymentUiState.Loading,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (paymentState is PaymentUiState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            "Thanh toán qua VNPay",
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
                .padding(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tổng cộng", fontSize = 14.sp, color = Color.White.copy(alpha = 0.85f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "%,.0fđ".format(totalAmount),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        if (selectedSeatsCount > 0) "$selectedSeatsCount ghế đã chọn" else "Chưa chọn ghế",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            Text("Phương thức thanh toán", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
            Spacer(modifier = Modifier.height(14.dp))

            PaymentMethodCard(
                title = "VNPay",
                subtitle = "Thanh toán qua cổng VNPay / quét mã QR",
                icon = Icons.Default.QrCode,
                iconTint = Color(0xFF005DAA),
                isSelected = selectedMethod == "VNPay",
                onClick = { selectedMethod = "VNPay" }
            )

            Spacer(modifier = Modifier.height(12.dp))
            PaymentMethodCard(
                title = "Ví khác",
                subtitle = "Chưa hỗ trợ trong phiên bản này",
                icon = Icons.Default.AccountBalanceWallet,
                iconTint = Color(0xFF9CA3AF),
                isSelected = selectedMethod == "Other",
                onClick = { selectedMethod = "Other" }
            )
        }
    }
}

@Composable
private fun PaymentMethodCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) PrimaryBlue else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFF5F7FA), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VnpayExternalPaymentScreen(
    paymentUrl: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    fun openPaymentUrl() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    LaunchedEffect(paymentUrl) {
        openPaymentUrl()
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "VNPay",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.OpenInBrowser,
                contentDescription = "VNPay",
                tint = PrimaryBlue,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Hoàn tất thanh toán trên trình duyệt",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Ứng dụng sẽ tự cập nhật khi VNPay xác nhận giao dịch.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(28.dp))
            CircularProgressIndicator(color = PrimaryBlue)
            Spacer(modifier = Modifier.height(28.dp))
            Button(
                onClick = { openPaymentUrl() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Mở lại VNPay", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(onClick = onBackClick) {
                Text("Hủy thanh toán", color = Color.Gray)
            }
        }
    }
}
