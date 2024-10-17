package com.example.sosapp

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository (
    private val auth: com.google.firebase.auth.FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun login(email: String, password: String): com.example.sosapp.Result<Boolean> =
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            com.example.sosapp.Result.Success(true)
        } catch (e: Exception) {
            com.example.sosapp.Result.Error(e)
        }
}