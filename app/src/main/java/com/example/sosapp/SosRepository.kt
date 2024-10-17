package com.example.sosapp

import com.example.sosapp.SosEvent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SosRepository(private val firestore: FirebaseFirestore) {

    suspend fun sendSosEvent(sosEvent: SosEvent) {
        firestore.collection("sos").document("latest")
            .set(sosEvent)
            .await()
    }

    fun listenForSosEvent(onSosTriggered: (SosEvent) -> Unit) {
        firestore.collection("sos").document("latest")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null || !snapshot.exists()) {
                    return@addSnapshotListener
                }
                val sosEvent = snapshot.toObject(SosEvent::class.java)
                sosEvent?.let { onSosTriggered(it) }
            }
    }
}
