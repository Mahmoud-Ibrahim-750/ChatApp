package com.mis.route.chatapp.ui.fragments.createroom.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Room(
    var id: String? = null,
    val ownerId: String? = null,
    val membersIds: MutableList<String?>? = mutableListOf(ownerId),
    val title: String? = null,
    val description: String? = null,
    val category: String? = null,
) : Parcelable {

    fun membersCount(): Int = membersIds?.size ?: 0

    fun addMember(id: String) {
        membersIds!!.add(id)
    }

    fun removeMember(id: String) {
        membersIds!!.remove(id)
    }

    fun isMember(userId: String) = membersIds?.contains(userId) ?: false

    companion object {
        const val membersIdsField = "membersIds"
    }
}
