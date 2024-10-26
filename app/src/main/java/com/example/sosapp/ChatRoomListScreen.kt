package com.example.sosapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun ChatRoomListScreen(
    roomViewModel: RoomViewModel = viewModel(),
    navController: NavController
) {
    val rooms by roomViewModel.rooms.observeAsState(emptyList())

    // Background gradient in dark army colors
    val gradientColors = listOf(Color(0xFF2F4F4F), Color.Black)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(gradientColors)) // Gradient background
            .padding(16.dp)
    ) {
        // Title text
        Text(
            text = "Chat Rooms",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White, // White text to stand out
            modifier = Modifier.align(Alignment.CenterHorizontally) // Center the title
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Display a list of chat rooms
        LazyColumn {
            items(rooms) { room ->
                RoomItem(room = room, onRoomClicked = {
                    navController.navigate("chatDetailScreen/${room.name}/${room.date}/${room.time}")
                })
            }
        }
    }
}

@Composable
fun RoomItem(room: Room, onRoomClicked: (Room) -> Unit) {
    // Card with no background color, but wrapping the content with a background modifier
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onRoomClicked(room) },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .background(Color.DarkGray) // Set background color here
                .padding(16.dp)
        ) {
            // Room name in dark red to highlight
            Text(
                text = room.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black // Dark red for room name
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Room details in light gray for contrast
            Text(
                text = "Date: ${room.date}",
                fontSize = 14.sp,
                color = Color.LightGray // Light gray for date
            )
            Text(
                text = "Time: ${room.time}",
                fontSize = 14.sp,
                color = Color.LightGray // Light gray for time
            )
        }
    }
}
