package com.example.sosapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatDetailScreen(
    name: String,
    date: String,
    time: String,
    navController: NavHostController,
    backStackEntry: NavBackStackEntry
) {
    val roomName = backStackEntry.arguments?.getString("name") ?: ""
    val roomDate = backStackEntry.arguments?.getString("date") ?: ""
    val roomTime = backStackEntry.arguments?.getString("time") ?: ""

    // Background gradient in dark army colors
    val gradientColors = listOf(Color(0xFF2F4F4F), Color.Black)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors)) // Gradient background
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp), // Spacing between elements
        horizontalAlignment = Alignment.CenterHorizontally // Center items horizontally
    ) {
        // Room name text
        Text(
            text = roomName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black // Dark red for room name
        )

        // Room date and time details
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Date: $roomDate",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White // White text for better contrast
            )
            Text(
                text = "Time: $roomTime",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White // White text for better contrast
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // "See Location" button with full width
        Button(
            onClick = { /* Handle See Location */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(Color.DarkGray) // Dark gray button color
        ) {
            Text(
                text = "See Location",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chat Screen (Push down)
        ChatScreen(roomId = roomName)
    }
}
