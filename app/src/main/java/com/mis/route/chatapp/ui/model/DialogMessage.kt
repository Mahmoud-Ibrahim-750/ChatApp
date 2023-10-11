package com.mis.route.chatapp.ui.model

import android.content.DialogInterface

data class DialogMessage(
    val title: String? = null,
    val content: String? = null,
    val posMessage: String? = null,
    val posAction: DialogInterface.OnClickListener? = null,
    val negMessage: String? = null,
    val negAction: DialogInterface.OnClickListener? = null,
    val cancelable: Boolean = true
)