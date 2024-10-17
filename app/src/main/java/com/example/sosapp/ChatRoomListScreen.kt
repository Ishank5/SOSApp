package com.example.sosapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Chat Rooms", fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onRoomClicked(room) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = room.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Date: ${room.date}", fontSize = 14.sp)
            Text(text = "Time: ${room.time}", fontSize = 14.sp)
        }
    }
}