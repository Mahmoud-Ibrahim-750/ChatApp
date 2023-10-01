package com.mis.route.chatapp.ui.fragments.createroom.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Room(
    var id: String? = null,
    val ownerId: String? = null,
    val membersIds: MutableList<String?>? = mutableListOf(ownerId),
    val membersCount: Int = membersIds!!.size,
    val title: String? = null,
    val description: String? = null,
    val category: String? = null,
) : Parcelable {

    fun addMember(id: String) {
        membersIds!!.add(id)
    }
}
