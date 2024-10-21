package com.example.sosapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatDetailScreen(name: String, date: String, time: String,
    navController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val roomName = backStackEntry.arguments?.getString("name") ?: ""
    val roomDate = backStackEntry.arguments?.getString("date") ?: ""
    val roomTime = backStackEntry.arguments?.getString("time") ?: ""

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(text = roomName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Date: $roomDate", fontSize = 16.sp)
        Text(text = "Time: $roomTime", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { /* Handle See Location */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("See Location")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Push down ChatScreen
        ChatScreen(roomId = roomName) // Assuming roomId is the room name
    }
}