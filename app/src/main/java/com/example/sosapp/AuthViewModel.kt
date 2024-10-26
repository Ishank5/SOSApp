package com.example.sosapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel(){
    private val userRepository1: UserRepository

    init {
        userRepository1 = UserRepository(
            com.google.firebase.auth.FirebaseAuth.getInstance(),
            Injection.instance()
        )
    }

    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult: LiveData<Result<Boolean>> get() = _authResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = userRepository1.login(email, password)
    }
}}