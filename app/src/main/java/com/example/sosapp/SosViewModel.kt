package com.example.sosapp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class SosViewModel(application: Application) : AndroidViewModel(application) {

    private val sosRepository: SosRepository = SosRepository(FirebaseFirestore.getInstance())
    private val deviceId: String = Globalvariable.username

    @SuppressLint("HardwareIds")
    fun sendSosEvent() {
        val context = getApplication<Application>().applicationContext
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

        val sosEvent = SosEvent(
            triggered = true,
            timestamp = System.currentTimeMillis(),
            deviceId = deviceId,
            android = androidId
        )

        viewModelScope.launch {
            sosRepository.sendSosEvent(sosEvent)
        }
    }

    fun stopSosEvent() {
        val sosEvent = SosEvent(
            triggered = false,
            timestamp = System.currentTimeMillis(),
            deviceId = deviceId
        )

        viewModelScope.launch {
            sosRepository.sendSosEvent(sosEvent)

            // Stop the foreground service
            val context = getApplication<Application>()
//            val serviceIntent = Intent(context, SosService::class.java)
//            context.stopService(serviceIntent)
        }
    }
}
