package com.example.sosapp

data class SosEvent(
    val triggered: Boolean = false,
    val timestamp: Long = 0L,
    val deviceId: String = "" ,// Add this line
    val android: String = ""
)