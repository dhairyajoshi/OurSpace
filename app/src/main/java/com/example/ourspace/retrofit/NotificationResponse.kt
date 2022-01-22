package com.example.ourspace.retrofit

data class NotificationResponse(
    val sender: String,
    val receiver: String,
    val post: String,
    val pic: String,
    val date: String,
    val post_id: Int
)
