package com.mis.route.chatapp.ui

import android.app.AlertDialog
import androidx.fragment.app.Fragment

object Extensions {
    fun Fragment.showMessage(message: Message) {
        message.apply {
            AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(posMessage, posAction)
                .setNegativeButton(negMessage, negAction)
                .setCancelable(cancelable)
                .show()
        }
    }

    fun Fragment.buildProgressDialog(message: Message): AlertDialog {
        message.apply {
            return AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(posMessage, posAction)
                .setNegativeButton(negMessage, negAction)
                .setCancelable(cancelable)
                .create()
        }
    }
}