package com.mis.route.chatapp.ui

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import com.mis.route.chatapp.ui.model.DialogMessage

object Extensions {
    fun Fragment.showMessage(dialogMessage: DialogMessage) {
        dialogMessage.apply {
            AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(posMessage, posAction)
                .setNegativeButton(negMessage, negAction)
                .setCancelable(cancelable)
                .show()
        }
    }

    fun Fragment.buildProgressDialog(dialogMessage: DialogMessage): AlertDialog {
        dialogMessage.apply {
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