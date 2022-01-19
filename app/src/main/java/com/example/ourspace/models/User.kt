package com.example.ourspace.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val name: String,
    val bio: String,
    val dob: String,
    val gender: String,
    val residence: String,
): Parcelable