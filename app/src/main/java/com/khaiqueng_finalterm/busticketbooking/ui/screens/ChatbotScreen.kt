package com.khaiqueng_finalterm.busticketbooking.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khaiqueng_finalterm.busticketbooking.data.repository.ChatbotRepository
import com.khaiqueng_finalterm.busticketbooking.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch

private data class ChatMessage(
    val text: String,
    val fromUser: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen(
    onBackClick: () -> Unit
) {
    val repository = remember { ChatbotRepository() }
    val scope = rememberCoroutineScope()
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                "Xin chào, mình là trợ lý Bus Go Tickets. Bạn cần hỗ trợ về đặt vé, thanh toán, xem vé hay email xác nhận?",
                false
            )
        )
    }
    var input by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    fun sendMessage(rawMessage: String) {
        val message = rawMessage.trim()
        if (message.isBlank() || isSending) return

        messages.add(ChatMessage(message, true))
        input = ""
        isSending = true

        scope.launch {
            repository.sendMessage(message)
                .onSuccess { response ->
                    messages.add(ChatMessage(response.reply, false))
                }
                .onFailure { exception ->
                    messages.add(
                        ChatMessage(
                            exception.message ?: "Hiện tại chatbot chưa phản hồi được. Bạn vui lòng thử lại sau.",
                            false
                        )
                    )
                }
            isSending = false
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Trợ lý hỗ trợ", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(12.dp)
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Nhập câu hỏi...") },
                        singleLine = false,
                        maxLines = 3,
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            cursorColor = PrimaryBlue
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { sendMessage(input) },
                        enabled = input.isNotBlank() && !isSending,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp)
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Gửi")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                QuickQuestions(onQuestionClick = ::sendMessage)
            }

            items(messages) { message ->
                ChatBubble(message)
            }
        }
    }
}

@Composable
private fun QuickQuestions(onQuestionClick: (String) -> Unit) {
    val questions = listOf(
        "Tôi xem vé ở đâu?",
        "Thanh toán VNPAY thế nào?",
        "Không thấy email xác nhận",
        "Tôi có thể hủy vé không?"
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Gợi ý nhanh", fontSize = 13.sp, color = Color.Gray)
        questions.chunked(2).forEach { rowQuestions ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowQuestions.forEach { question ->
                    AssistChip(
                        onClick = { onQuestionClick(question) },
                        label = { Text(question, fontSize = 12.sp, color = Color(0xFF1F2937)) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color.White,
                            labelColor = Color(0xFF1F2937)
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.fromUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!message.fromUser) {
            Icon(
                Icons.Default.SmartToy,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            modifier = Modifier.fillMaxWidth(0.82f),
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (message.fromUser) 18.dp else 4.dp,
                bottomEnd = if (message.fromUser) 4.dp else 18.dp
            ),
            color = if (message.fromUser) PrimaryBlue else Color.White,
            tonalElevation = if (message.fromUser) 0.dp else 1.dp
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                color = if (message.fromUser) Color.White else Color(0xFF1F2937),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
        }
    }
}
