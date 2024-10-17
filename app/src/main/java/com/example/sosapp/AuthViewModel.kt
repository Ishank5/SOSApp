package com.example.sosapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel(){
    private val userRepository1: com.example.sosapp.UserRepository

    init {
        userRepository1 = com.example.sosapp.UserRepository(
            com.google.firebase.auth.FirebaseAuth.getInstance(),
            com.example.sosapp.Injection.instance()
        )
    }

    private val _authResult = MutableLiveData<com.example.sosapp.Result<Boolean>>()
    val authResult: LiveData<com.example.sosapp.Result<Boolean>> get() = _authResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = userRepository1.login(email, password)
    }
}}