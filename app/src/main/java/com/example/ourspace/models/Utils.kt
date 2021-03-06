package com.example.ourspace.models

import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {
        var dateFormat = SimpleDateFormat("dd-MM-yy HH-mm-ss")


        private fun getCurrentTime(): String {
            dateFormat.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
            val today = Calendar.getInstance().time
            return dateFormat.format(today).toString()
        }

        fun getTimeAgo(time: String): String {
            val now = getCurrentTime()
            return when {
                now.slice(6..7) > time.slice(6..7) -> "days ago"
                now.slice(3..4) > time.slice(3..4) -> "days ago"
                now.slice(0..1).toInt() - time.slice(0..1).toInt() > 1 -> "days ago"
                now.slice(0..1).toInt() - time.slice(0..1).toInt() == 1 -> "Yesterday"
                now.slice(9..10).toInt() > time.slice(9..10).toInt() -> if (now.slice(9..10)
                        .toInt() - time.slice(9..10).toInt() == 1
                ) "1 hour ago" else "${
                    now.slice(9..10).toInt() - time.slice(9..10).toInt()
                } hours ago"
                now.slice(12..13).toInt() > time.slice(12..13).toInt() -> if (now.slice(12..13)
                        .toInt() - time.slice(12..13).toInt() == 1
                ) "1 minute ago" else "${
                    now.slice(12..13).toInt() - time.slice(12..13).toInt()
                } minutes ago"
                now.slice(15..16).toInt() > time.slice(15..16).toInt() -> "Just now"
                else -> ""
            }


        }


    }
}