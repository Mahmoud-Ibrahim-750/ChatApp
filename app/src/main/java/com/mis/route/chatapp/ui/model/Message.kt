package com.mis.route.chatapp.ui.model

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class Message(
    val content: String? = null,
    val senderId: String? = null,
    val senderName: String? = null,
    val receiverId: String? = null,
    val dateTime: Timestamp? = null
) {

    fun getFormattedTime(): String? {
        val timeFormatter = SimpleDateFormat("h:m a", Locale.getDefault())
        return dateTime?.toDate()?.let { timeFormatter.format(it) }
    }

    companion object {
        const val dateTimeField = "dateTime"
    }
}
