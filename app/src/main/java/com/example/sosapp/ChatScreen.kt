package com.example.sosapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(roomId: String, messageViewModel: MessageViewModel = viewModel()) {

    val text = remember { mutableStateOf("") }
    val messages by messageViewModel.messages.observeAsState(emptyList())
    messageViewModel.setRoomId(roomId)

    // Gradient background in dark slate gray and black
    val gradientColors = listOf(Color(0xFF2F4F4F), Color.Black)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
            .padding(16.dp)
    ) {
        // Display the chat messages
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(messages) { message ->
                ChatMessageItem(
                    message = message.copy(isSentByCurrentUser = message.SenderID == messageViewModel.currentUser.value?.email)
                )
            }
        }

        // Chat input field and send icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = text.value,
                onValueChange = { text.value = it },
                textStyle = androidx.compose.ui.text.TextStyle.Default.copy(fontSize = 16.sp, color = Color.White),
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFF2F4F4F), RoundedCornerShape(8.dp)) // Dark slate gray for input field background
                    .padding(8.dp)
            )

            IconButton(
                onClick = {
                    // Send the message when the icon is clicked
                    if (text.value.isNotEmpty()) {
                        messageViewModel.sendMessage((text.value.trim()))
                        text.value = ""
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color(0xFF8B0000)) // Dark red send icon
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatMessageItem(message: Message) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = if (message.isSentByCurrentUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (message.isSentByCurrentUser) Color.Black // Dark red for sent messages
                    else Color.DarkGray, // Dark gray for received messages
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                style = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message.SenderFirstName,
            style = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = Color.Gray)
        )
        Text(
            text = formatTimestamp(message.timestamp),
            style = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = Color.Gray)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTimestamp(timestamp: Long): String {
    val messageDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    val now = LocalDateTime.now()

    return when {
        isSameDay(messageDateTime, now) -> "today ${formatTime(messageDateTime)}"
        isSameDay(messageDateTime.plusDays(1), now) -> "yesterday ${formatTime(messageDateTime)}"
        else -> formatDate(messageDateTime)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun isSameDay(dateTime1: LocalDateTime, dateTime2: LocalDateTime): Boolean {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return dateTime1.format(formatter) == dateTime2.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return formatter.format(dateTime)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDate(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    return formatter.format(dateTime)
}
