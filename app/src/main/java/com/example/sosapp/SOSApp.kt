package com.example.sosapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.navigation.NavHostController

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun SOSApp(viewModel: SosViewModel, roomViewModel: RoomViewModel, navController: NavHostController, username: String) {
    // Background gradient in dark army colors (e.g., dark green and black)
    val gradientColors = listOf(Color(0xFF2F4F4F), Color.Black) // Dark Slate Gray to Black

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors)) // Gradient background
            .padding(16.dp)
    ) {
        // Greeting text at the top
        Text(
            text = "$username", // Display the username
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter) // Align the text at the top
                .padding(top = 16.dp)
        )

        // Main content (SOS and Stop SOS buttons) in the center
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp), // Space between buttons
            modifier = Modifier.align(Alignment.Center)
        ) {
            // SOS Button (Dark Red, Circular, Center of Screen)
            Button(
                onClick = {
                    viewModel.sendSosEvent()
                    roomViewModel.createRoom(username)
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF8B0000)), // Dark Red
                shape = CircleShape,
                modifier = Modifier.size(150.dp) // Adjust size as needed
            ) {
                Text(
                    text = "SOS",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Stop SOS Button (Black, Rectangular, just below SOS button)
            Button(
                onClick = { viewModel.stopSosEvent() },
                colors = ButtonDefaults.buttonColors(Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Stop SOS",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // History Button (Full Width, Rectangular, Bottom of Screen)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = { navController.navigate("chatRoomListScreen") },
                colors = ButtonDefaults.buttonColors(Color.Gray),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "SOS History",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
