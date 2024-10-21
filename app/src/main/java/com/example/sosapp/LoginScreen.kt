package com.example.sosapp

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    sharedPreferences: SharedPreferences,
    onSignInSuccess: (String) -> Unit // Pass email on success
) {
    val result = authViewModel.authResult.observeAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2E3B55),  // Dark Blue-Grey
                        Color(0xFF1C2833),  // Darker Blue-Grey
                        Color(0xFF0B1A30)   // Very Dark Blue-Grey
                    )
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Text(
            text = "Login",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFDFEFE), // Light grey-white color
            modifier = Modifier.padding(bottom = 24.dp)
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = Color(0xFF2C3E50)) }, // Light grey-white color
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFDFEFE), // Light grey-white color
                unfocusedIndicatorColor = Color(0xFFFDFEFE) // Light grey-white color
            )
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = Color(0xFF2C3E50)) }, // Light grey-white color
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color(0xFFFDFEFE), // Light grey-white color
                unfocusedIndicatorColor = Color(0xFFFDFEFE) // Light grey-white color
            )
        )

        Button(
            onClick = {
                if (isInternetAvailable(context)) {
                    Globalvariable.username = email

                    authViewModel.login(email, password)
                } else {
                    Toast.makeText(context, "No internet connection. Please try again.", Toast.LENGTH_LONG).show()
                }
            },
            colors = ButtonDefaults.buttonColors(Color(0xFF28B463)), // Military green
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = "Login",
                fontSize = 16.sp,
                color = Color(0xFFFDFEFE) // Light grey-white color
            )
        }

        LaunchedEffect(result.value) {
            when (result.value) {
                is Result.Success -> {
                    with(sharedPreferences.edit()) {
                        putString("email", email)
                        putString("password", password)
                        putBoolean("is_logged_in", true)
                        apply()
                    }
                    onSignInSuccess(email) // Pass email on success
                }

                is Result.Error -> {
                    Toast.makeText(
                        context,
                        "Incorrect credentials, please try again.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> {}
            }
        }
    }
}

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}