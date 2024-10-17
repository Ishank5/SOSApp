package com.example.sosapp

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sosapp.ui.theme.SOSAppTheme
import com.example.sosapp.ui.theme.ScheduleSOSWorker
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val sosViewModel: SosViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val roomViewModel: RoomViewModel by viewModels()

        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            Log.e("FirebaseError", "Failed to initialize Firebase", e)
        }

        // Check if the username is 'temp'
        val username = sharedPreferences.getString("username", "")
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (username == "temp") {
            // Navigate to the login screen
            setContent {
                SOSAppTheme {
                    NavGraph(startDestination = "loginScreen")
                    ScheduleSOSWorker()
                }
            }
        } else {
            // Check if user is already logged in
            if (isLoggedIn) {
                // Navigate to the main screen directly
                setContent {
                    SOSAppTheme {
                        NavGraph(startDestination = "sosAppScreen/${username}")
                        ScheduleSOSWorker()
                    }
                }
            } else {
                setContent {
                    SOSAppTheme {
                        NavGraph(startDestination = "loginScreen")
                        ScheduleSOSWorker()
                    }
                }
            }

            // Start the foreground service
            val serviceIntent = Intent(this, RingtoneForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }

            // Check if it's the first startup and request device admin permission
            val appPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            if (!appPrefs.getBoolean("device_admin_requested", false)) {
                requestDeviceAdminPermission()
                appPrefs.edit().putBoolean("device_admin_requested", true).apply()
            }
        }
    }

    private fun requestDeviceAdminPermission() {
        val devicePolicyManager =
            getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = ComponentName(this, MyDeviceAdminReceiver::class.java)

        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
            putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Device Admin Activation Explanation"
            )
        }
        startActivityForResult(intent, 1)
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun NavGraph(startDestination: String) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

    NavHost(navController = navController, startDestination = startDestination) {
        composable("loginScreen") {
            LoginScreen(
                authViewModel = AuthViewModel(),
                sharedPreferences = sharedPreferences
            ) {
                sharedPreferences.edit().putString("username", it).apply()
                sharedPreferences.edit().putBoolean("is_logged_in", true).apply()
                navController.navigate("sosAppScreen/$it") {
                    popUpTo("loginScreen") { inclusive = true }
                }
            }
        }

        composable("sosAppScreen/{username}") { backStackEntry ->
            val viewModel: SosViewModel = viewModel()
            val username = backStackEntry.arguments?.getString("username")
            if (username != null) {
                SOSApp(viewModel = viewModel, username = username, roomViewModel = RoomViewModel(), navController = navController)
            }
        }

        composable("chatRoomListScreen") {
            val roomViewModel: RoomViewModel = viewModel()
            ChatRoomListScreen(roomViewModel, navController)
        }
    }
}
