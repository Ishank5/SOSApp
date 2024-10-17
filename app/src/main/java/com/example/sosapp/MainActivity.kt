package com.example.sosapp

import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sosapp.ui.theme.SOSAppTheme
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import android.media.RingtoneManager
import android.net.Uri
import android.media.Ringtone
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName

class MainActivity : ComponentActivity() {
    private val sosViewModel: SosViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start the foreground service
        val serviceIntent = Intent(this, RingtoneForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        // Check if it's the first startup and request device admin permission
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        if (!sharedPreferences.getBoolean("device_admin_requested", false)) {
            requestDeviceAdminPermission()
            sharedPreferences.edit().putBoolean("device_admin_requested", true).apply()
        }

        enableEdgeToEdge()
        setContent {
            SOSAppTheme {
                SOSApp(sosViewModel)
                ScheduleSOSWorker()
                disableBatteryOptimization(LocalContext.current)
            }
        }
    }

    private fun requestDeviceAdminPermission() {
        val devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = ComponentName(this, MyDeviceAdminReceiver::class.java)

        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Device Admin Activation Explanation")
        }
        startActivityForResult(intent, 1)
    }

    private fun enableEdgeToEdge() {
        // Implementation of edge-to-edge UI, if applicable
    }
}

fun disableBatteryOptimization(context: Context) {
    val intent = Intent()
    val packageName = context.packageName
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    if (pm.isIgnoringBatteryOptimizations(packageName)) {
        intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
    } else {
        intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        intent.data = Uri.parse("package:$packageName")
    }
    context.startActivity(intent)
}


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun SOSApp(viewModel: SosViewModel) {
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

}////////////////////////////////////////


@Composable
fun ScheduleSOSWorker() {
    LaunchedEffect(Unit) {
        val workRequest = PeriodicWorkRequestBuilder<SOSWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            "SOSWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

}




