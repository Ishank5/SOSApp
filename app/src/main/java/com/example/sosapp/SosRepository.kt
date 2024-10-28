package com.example.sosapp

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage

class SosRepository {

    fun sendSosEvent(sosEvent: SosEvent) {
        val data = mapOf(
            "triggered" to sosEvent.triggered.toString(),
            "timestamp" to sosEvent.timestamp.toString(),
            "deviceId" to sosEvent.deviceId,
            "android" to sosEvent.android
        )

        FirebaseMessaging.getInstance().send(
            RemoteMessage.Builder("eb0973b03aac85f7ddf4734024d785649a68e8e2")
                .setMessageId(System.currentTimeMillis().toString())
                .setData(data)
                .build()
        )
    }
}