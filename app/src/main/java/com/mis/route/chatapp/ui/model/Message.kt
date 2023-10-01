package com.mis.route.chatapp.ui.model

import com.google.firebase.Timestamp

data class Message(
    val content: String? = null,
    val senderId: String? = null,
    val receiverId: String? = null,
    val dateTime: Timestamp? = null
)
