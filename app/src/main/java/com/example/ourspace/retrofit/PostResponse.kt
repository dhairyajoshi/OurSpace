package com.example.ourspace.retrofit

data class PostResponse(
    val id: Int, val caption: String, val username: String, val pic: String, val pfp: String,
    var likes: Int, val date: String, val user: Int
)
