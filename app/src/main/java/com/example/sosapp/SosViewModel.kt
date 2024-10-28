package com.example.sosapp

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SosViewModel(application: Application) : AndroidViewModel(application) {

    private val sosRepository: SosRepository = SosRepository()
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
        }
    }
}