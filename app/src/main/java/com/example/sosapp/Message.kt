package com.example.sosapp


data class Message(
    val SenderFirstName : String = "",
    val SenderID : String = "",
    val text : String = "" ,
    val timestamp: Long = System.currentTimeMillis(),
    val isSentByCurrentUser : Boolean = false

)
