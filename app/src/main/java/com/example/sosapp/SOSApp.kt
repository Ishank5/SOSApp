package com.example.sosapp

import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun SOSApp(viewModel: SosViewModel,username: String) {
    val context = LocalContext.current
    val deviceId = android.provider.Settings.Secure.getString(
        context.contentResolver,
        android.provider.Settings.Secure.ANDROID_ID
    )

    LaunchedEffect(Unit) {
        val ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val ringtone: Ringtone = RingtoneManager.getRingtone(context, ringtoneUri)

        ringtone.isLooping = true // Set ringtone to loop

        viewModel.listenForSosEvent { sosEvent ->
            if (sosEvent.triggered && sosEvent.deviceId != deviceId) {  // Check if deviceId matches
                Toast.makeText(context, "SOS Triggered!", Toast.LENGTH_LONG).show()
                if (!ringtone.isPlaying) {
                    ringtone.play()
                }
            } else {
                if (ringtone.isPlaying) {
                    ringtone.stop()
                }
            }
        }
    }






    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = { viewModel.sendSosEvent() },
            colors = ButtonDefaults.buttonColors(Color.Red),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "SOS",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Button(
            onClick = { viewModel.stopSosEvent() },
            colors = ButtonDefaults.buttonColors(Color.Gray),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Stop SOS",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

}