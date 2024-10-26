package com.example.sosapp


import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.Ringtone
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.sosapp.ui.theme.SOSAppTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private val sosViewModel: SosViewModel by viewModels()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var deviceId: String
    private lateinit var sosDocRef: DocumentReference
    private var ringtone: Ringtone? = null

    companion object {
        lateinit var sharedPreferences: SharedPreferences
        const val REQUEST_CODE_ENABLE_ADMIN = 1
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[android.Manifest.permission.POST_NOTIFICATIONS] == true){
                // Permissions granted
                startSosService()
            } else {
                // Permissions not granted
            }
        }

    //registering receiver
    private val bootReceiver = BootReceiver()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        //registering receiver
        super.onCreate(savedInstanceState)
        registerReceiver(
            bootReceiver,
            IntentFilter(Intent.ACTION_BOOT_COMPLETED)
        )

        // Initialize EncryptedSharedPreferences
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        sharedPreferences = EncryptedSharedPreferences.create(
            "user_prefs",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        requestPermissions()

        val intent = Intent()
        val packageName = packageName
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }

        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            Log.e("FirebaseError", "Failed to initialize Firebase", e)
        }

        // Check if device admin is enabled and asking for permissions
        val devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = ComponentName(this, MyDeviceAdminReceiver::class.java)
        if (!devicePolicyManager.isAdminActive(componentName)) {
            requestDeviceAdmin()

        } else {
            proceedToApp()
        }
    }

    private fun requestDeviceAdmin() {
        val deviceAdminComponent = ComponentName(this, MyDeviceAdminReceiver::class.java)
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminComponent)
            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Your explanation here")
        }
        startActivityForResult(intent,
            REQUEST_CODE_ENABLE_ADMIN
        )
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Device Admin Enabled", Toast.LENGTH_SHORT).show()
                proceedToApp()
            } else {
                Toast.makeText(this, "Device Admin Enabling Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // If the device is running Tiramisu or above, add the POST_NOTIFICATIONS permission to the request list
            permissionsToRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsToRequest.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            // If any of the required permissions are not granted, request them
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // If all permissions are already granted, start the SOS service
            startSosService()

        }
    }
    private fun startSosService() {
        val serviceIntent = Intent(this, SosService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bootReceiver)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun proceedToApp() {
        val username = sharedPreferences.getString("username", "temp")
        Globalvariable.username = username ?: "temp"

        setContent {
            SOSAppTheme {
                NavGraph(sharedPreferences)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun NavGraph(sharedPreferences: SharedPreferences) {
    val navController = rememberNavController()
    LocalContext.current

    val startDestination = if (sharedPreferences.getBoolean("is_logged_in", false)) {
        "sosAppScreen/${sharedPreferences.getString("username", "")}"
    } else {
        "loginScreen"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("loginScreen") {
            LoginScreen(
                authViewModel = AuthViewModel(),
                sharedPreferences = sharedPreferences
            ) { email ->
                sharedPreferences.edit().putString("username", email).commit()
                navController.navigate("sosAppScreen/$email") {
                    popUpTo("loginScreen") { inclusive = true }
                }
            }
        }

        composable("sosAppScreen/{username}") { backStackEntry ->
            val viewModel: SosViewModel = viewModel()
            val username = Globalvariable.username

            if (username != null) {

                SOSApp(viewModel = viewModel, username = username, roomViewModel = RoomViewModel(), navController = navController)
            }
        }

        composable("chatRoomListScreen") {
            val roomViewModel: RoomViewModel = viewModel()
            ChatRoomListScreen(roomViewModel, navController)
        }

        composable("chatDetailScreen/{name}/{date}/{time}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            val date = backStackEntry.arguments?.getString("date")
            val time = backStackEntry.arguments?.getString("time")
            if (name != null && date != null && time != null) {
                ChatDetailScreen(name = name, date = date, time = time, navController = navController, backStackEntry = backStackEntry)
            }
        }
    }
}
