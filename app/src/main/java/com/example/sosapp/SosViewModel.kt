package com.example.sosapp

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.sosapp.SosEvent
import com.example.sosapp.SosRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class SosViewModel(application: Application) : AndroidViewModel(application) {

    private val sosRepository: SosRepository = SosRepository(FirebaseFirestore.getInstance())

    @SuppressLint("HardwareIds")
    private val deviceId: String = Settings.Secure.getString(
        application.contentResolver,
        Settings.Secure.ANDROID_ID
    )

    fun sendSosEvent() {
        val sosEvent = SosEvent(
            triggered = true,
            timestamp = System.currentTimeMillis(),
            deviceId = deviceId // Add this line
        )
        viewModelScope.launch {
            sosRepository.sendSosEvent(sosEvent)

            // Enqueue WorkManager
            val sosWorkRequest = OneTimeWorkRequestBuilder<SOSWorker>().build()
            WorkManager.getInstance(getApplication()).enqueue(sosWorkRequest)
        }
    }
    fun listenForSosEvent(onSosTriggered: (SosEvent) -> Unit) {
        sosRepository.listenForSosEvent(onSosTriggered)
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
            val serviceIntent = Intent(context, RingtoneForegroundService::class.java)
            context.stopService(serviceIntent)
        }
    }

    //...
}

