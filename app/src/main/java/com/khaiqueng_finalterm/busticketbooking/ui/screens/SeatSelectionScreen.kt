package com.khaiqueng_finalterm.busticketbooking.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.khaiqueng_finalterm.busticketbooking.data.model.SeatDTO
import com.khaiqueng_finalterm.busticketbooking.data.repository.AuthSession
import com.khaiqueng_finalterm.busticketbooking.data.repository.BookingSession
import com.khaiqueng_finalterm.busticketbooking.ui.theme.PrimaryBlue
import com.khaiqueng_finalterm.busticketbooking.ui.theme.SecondaryOrange
import com.khaiqueng_finalterm.busticketbooking.ui.viewmodels.BookingViewModel
import com.khaiqueng_finalterm.busticketbooking.ui.viewmodels.SeatSelectionUiState

private val SeatScreenBackground = Color(0xFFF6F8FC)
private val SeatBookedColor = Color(0xFFC7C7C7)
private val SeatTextMuted = Color(0xFF8C8C8C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    viewModel: BookingViewModel = viewModel()
) {
    val tripId = BookingSession.selectedTrip?.id ?: 1L

    LaunchedEffect(tripId) {
        viewModel.loadSeats(tripId)
    }

    val uiState by viewModel.seatUiState.collectAsState()
    var selectedSeatIds by remember { mutableStateOf<Set<Long>>(BookingSession.selectedSeatIds.toSet()) }
    var selectedSeatNames by remember { mutableStateOf<Set<String>>(BookingSession.selectedSeatNumbers.toSet()) }
    val userName = AuthSession.user?.name ?: "Hành khách"

    Scaffold(
        containerColor = SeatScreenBackground,
        topBar = { SeatSelectionTopBar(onBackClick = onBackClick) },
        bottomBar = {
            SeatSelectionBottomBar(
                selectedCount = selectedSeatIds.size,
                onContinueClick = {
                    BookingSession.selectedSeatIds = selectedSeatIds.toList()
                    BookingSession.selectedSeatNumbers = selectedSeatNames.toList()
                    BookingSession.lastBookingResponse = null
                    onContinueClick()
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SeatScreenBackground)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(horizontal = 12.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                PassengerCard(userName = userName)
                Spacer(modifier = Modifier.height(12.dp))
                SeatLegend()
                Spacer(modifier = Modifier.height(14.dp))
                SeatHeader()
                Spacer(modifier = Modifier.height(4.dp))

                when (val state = uiState) {
                    is SeatSelectionUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryBlue)
                        }
                    }

                    is SeatSelectionUiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.message,
                                color = Color.Red,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    is SeatSelectionUiState.Success -> {
                        val layout = remember(state.seats) { SeatLayout.from(state.seats) }
                        val seatMap = remember(state.seats) { state.seats.associateBy { it.seatNumber } }

                        SeatRowsContent(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            layout = layout,
                            seatMap = seatMap,
                            selectedSeatIds = selectedSeatIds,
                            onSeatClick = { seat ->
                                if (selectedSeatIds.contains(seat.id)) {
                                    selectedSeatIds = selectedSeatIds - seat.id
                                    selectedSeatNames = selectedSeatNames - seat.seatNumber
                                } else {
                                    selectedSeatIds = selectedSeatIds + seat.id
                                    selectedSeatNames = selectedSeatNames + seat.seatNumber
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SeatRowsContent(
    layout: SeatLayout,
    seatMap: Map<String, SeatDTO>,
    selectedSeatIds: Set<Long>,
    onSeatClick: (SeatDTO) -> Unit,
    modifier: Modifier = Modifier
) {
    if (layout.rows.size <= 8) {
        Column(
            modifier = modifier.padding(top = 2.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            layout.rows.forEach { rowNumber ->
                SeatRow(
                    rowNumber = rowNumber,
                    layout = layout,
                    seatMap = seatMap,
                    selectedSeatIds = selectedSeatIds,
                    onSeatClick = onSeatClick
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(layout.rows) { rowNumber ->
                SeatRow(
                    rowNumber = rowNumber,
                    layout = layout,
                    seatMap = seatMap,
                    selectedSeatIds = selectedSeatIds,
                    onSeatClick = onSeatClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeatSelectionTopBar(onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Chọn Ghế",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )
        },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .padding(start = 14.dp)
                    .size(36.dp)
                    .background(Color.White, CircleShape)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Quay lại",
                    tint = Color(0xFF111827),
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = SeatScreenBackground
        )
    )
}

@Composable
private fun PassengerCard(userName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE1E7EF), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(Color(0xFFE8F4FF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = userName,
            color = Color(0xFF1F2937),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SeatLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem("Trống", PrimaryBlue)
        Spacer(modifier = Modifier.width(22.dp))
        LegendItem("Đã đặt", SeatBookedColor)
        Spacer(modifier = Modifier.width(22.dp))
        LegendItem("Đang chọn", SecondaryOrange)
    }
}

@Composable
private fun SeatSelectionBottomBar(
    selectedCount: Int,
    onContinueClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Button(
            onClick = onContinueClick,
            enabled = selectedCount > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue,
                disabledContainerColor = Color(0xFFAEC2DC),
                contentColor = Color.White,
                disabledContentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (selectedCount > 0) "Tiếp tục với $selectedCount ghế" else "Vui lòng chọn ghế",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SeatHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SeatHeaderGroup(listOf("A", "B", "C"), modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(34.dp))
        SeatHeaderGroup(listOf("D", "E", "F"), modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SeatHeaderGroup(labels: List<String>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        labels.forEach { label ->
            Box(modifier = Modifier.size(34.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = label,
                    color = Color(0xFF6B7280),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SeatRow(
    rowNumber: Int,
    layout: SeatLayout,
    seatMap: Map<String, SeatDTO>,
    selectedSeatIds: Set<Long>,
    onSeatClick: (SeatDTO) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SeatGroup(
            columns = listOf("A", "B", "C"),
            rowNumber = rowNumber,
            layout = layout,
            seatMap = seatMap,
            selectedSeatIds = selectedSeatIds,
            onSeatClick = onSeatClick,
            modifier = Modifier.weight(1f)
        )

        Box(modifier = Modifier.width(34.dp), contentAlignment = Alignment.Center) {
            Text(
                text = rowNumber.toString(),
                color = Color(0xFF9CA3AF),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }

        SeatGroup(
            columns = listOf("D", "E", "F"),
            rowNumber = rowNumber,
            layout = layout,
            seatMap = seatMap,
            selectedSeatIds = selectedSeatIds,
            onSeatClick = onSeatClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SeatGroup(
    columns: List<String>,
    rowNumber: Int,
    layout: SeatLayout,
    seatMap: Map<String, SeatDTO>,
    selectedSeatIds: Set<Long>,
    onSeatClick: (SeatDTO) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        columns.forEach { column ->
            val seatNumber = layout.seatNumberFor(column, rowNumber)
            val seat = seatNumber?.let { seatMap[it] }
            if (seat != null) {
                SeatIcon(
                    seatId = seat.seatNumber,
                    isBooked = seat.isBooked,
                    isSelected = selectedSeatIds.contains(seat.id),
                    onSelect = { onSeatClick(seat) }
                )
            } else {
                Spacer(modifier = Modifier.size(34.dp))
            }
        }
    }
}

@Composable
fun LegendItem(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.EventSeat,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text,
            fontSize = 10.sp,
            color = SeatTextMuted,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SeatIcon(seatId: String, isBooked: Boolean, isSelected: Boolean, onSelect: (String) -> Unit) {
    val color = when {
        isSelected -> SecondaryOrange
        isBooked -> SeatBookedColor
        else -> PrimaryBlue
    }

    IconButton(
        onClick = { if (!isBooked) onSelect(seatId) },
        modifier = Modifier.size(34.dp)
    ) {
        Icon(
            imageVector = Icons.Default.EventSeat,
            contentDescription = "Seat $seatId",
            tint = color,
            modifier = Modifier.size(28.dp)
        )
    }
}

private enum class SeatNumbering {
    ColumnLetter,
    RowLetter,
    Sleeper,
    Limousine
}

private data class SeatLayout(
    val rows: List<Int>,
    private val numbering: SeatNumbering
) {
    fun seatNumberFor(column: String, rowNumber: Int): String? {
        return when (numbering) {
            SeatNumbering.ColumnLetter -> {
                if (column in visibleSeatColumns) "$column$rowNumber" else null
            }

            SeatNumbering.RowLetter -> {
                val rowLetter = ('A'.code + rowNumber - 1).toChar()
                val seatIndex = rowLetterColumnIndex[column] ?: return null
                "$rowLetter$seatIndex"
            }

            SeatNumbering.Sleeper -> {
                val rowLetter = ('A'.code + rowNumber - 1).toChar()
                when (column) {
                    "A" -> "L-${rowLetter}1"
                    "B" -> "L-${rowLetter}2"
                    "D" -> "U-${rowLetter}1"
                    "E" -> "U-${rowLetter}2"
                    else -> null
                }
            }

            SeatNumbering.Limousine -> {
                val baseIndex = (rowNumber - 1) * 4
                val offset = limousineColumnOffset[column] ?: return null
                "VIP-${baseIndex + offset}"
            }
        }
    }

    companion object {
        private val visibleSeatColumns = setOf("A", "B", "C", "D", "E", "F")
        private val rowLetterColumnIndex = mapOf(
            "A" to 1,
            "B" to 2,
            "C" to 3,
            "D" to 4,
            "E" to 5,
            "F" to 6
        )
        private val limousineColumnOffset = rowLetterColumnIndex

        fun from(seats: List<SeatDTO>): SeatLayout {
            val seatNumbers = seats.map { it.seatNumber }
            val numbering = when {
                seatNumbers.any { it.startsWith("VIP-") } -> SeatNumbering.Limousine
                seatNumbers.any { it.startsWith("L-") || it.startsWith("U-") } -> SeatNumbering.Sleeper
                seatNumbers.mapNotNull { it.trailingNumber() }.maxOrNull().orZero() > 4 -> SeatNumbering.ColumnLetter
                else -> SeatNumbering.RowLetter
            }

            val rowCount = when (numbering) {
                SeatNumbering.ColumnLetter -> seatNumbers.mapNotNull { it.trailingNumber() }.maxOrNull().orZero()
                SeatNumbering.RowLetter -> seatNumbers.mapNotNull { it.firstLetterIndex() }.maxOrNull().orZero()
                SeatNumbering.Sleeper -> seatNumbers.mapNotNull { it.deckRowLetterIndex() }.maxOrNull().orZero()
                SeatNumbering.Limousine -> ((seatNumbers.size + 5) / 6).coerceAtLeast(1)
            }.coerceAtLeast(1)

            return SeatLayout(rows = (1..rowCount).toList(), numbering = numbering)
        }
    }
}

private fun String.trailingNumber(): Int? {
    val digits = takeLastWhile { it.isDigit() }
    return digits.toIntOrNull()
}

private fun String.firstLetterIndex(): Int? {
    val first = firstOrNull()?.uppercaseChar() ?: return null
    if (first !in 'A'..'Z') return null
    return first - 'A' + 1
}

private fun String.deckRowLetterIndex(): Int? {
    val rowLetter = substringAfter("-", missingDelimiterValue = "").firstOrNull()?.uppercaseChar() ?: return null
    if (rowLetter !in 'A'..'Z') return null
    return rowLetter - 'A' + 1
}

private fun Int?.orZero(): Int = this ?: 0
