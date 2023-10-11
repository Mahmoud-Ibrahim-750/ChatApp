package com.mis.route.chatapp.data.firebase.firestore

import android.annotation.SuppressLint
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mis.route.chatapp.data.firebase.DataConstants
import com.mis.route.chatapp.data.firebase.model.UserProfile
import com.mis.route.chatapp.ui.fragments.createroom.model.Room
import com.mis.route.chatapp.ui.model.Message


object FirebaseFirestore {
    const val PROFILE_TAG = "ProfileTag"

    @SuppressLint("StaticFieldLeak")
    private val firestore = Firebase.firestore

    fun getCurrentUserProfile(
        userId: String,
        onCompleteListener: OnCompleteListener<DocumentSnapshot>
    ) {
        val profileRef = firestore.collection(DataConstants.USERS_COLLECTION).document(userId)
        profileRef
            .get()
            .addOnCompleteListener { onCompleteListener.onComplete(it) }
    }

    fun createProfile(
        profile: UserProfile,
        authUser: FirebaseUser,
        onCompleteListener: OnCompleteListener<Void>
    ) {
        firestore.collection(DataConstants.USERS_COLLECTION)
            .document(authUser.uid)
            .set(profile)
            .addOnCompleteListener { task -> onCompleteListener.onComplete(task) }
    }

    fun createNewEmptyRoom(onCompleteListener: OnCompleteListener<DocumentReference>) {
        firestore.collection(DataConstants.ROOMS_COLLECTION)
            .add(Room())
            .addOnCompleteListener { docRef -> onCompleteListener.onComplete(docRef) }
    }

    fun updateRoom(room: Room, onCompleteListener: OnCompleteListener<Void>) {
        firestore.collection(DataConstants.ROOMS_COLLECTION)
            .document(room.id!!)
            .set(room)
            .addOnCompleteListener { roomUpdateTask -> onCompleteListener.onComplete(roomUpdateTask) }
    }

    fun getAllRooms(onCompleteListener: OnCompleteListener<QuerySnapshot>) {
        firestore.collection(DataConstants.ROOMS_COLLECTION)
            .get()
            .addOnCompleteListener { retrievalTask -> onCompleteListener.onComplete(retrievalTask) }
    }

    fun getRoomsContainingUserId(
        authUser: FirebaseUser,
        onCompleteListener: OnCompleteListener<QuerySnapshot>
    ) {
        firestore.collection(DataConstants.ROOMS_COLLECTION)
            .whereArrayContains(Room.membersIdsField, authUser.uid)
            .get()
            .addOnCompleteListener { snapshot -> onCompleteListener.onComplete(snapshot) }
    }

    fun getMessagesByDateDescending(
        roomId: String,
        onCompleteListener: OnCompleteListener<QuerySnapshot>
    ) {
        firestore.collection(DataConstants.ROOMS_COLLECTION)
            .document(roomId)
            .collection(DataConstants.MESSAGES_COLLECTION)
            .orderBy(Message.dateTimeField, Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { snapshot -> onCompleteListener.onComplete(snapshot) }
    }

    fun sendMessage(message: Message, onCompleteListener: OnCompleteListener<DocumentReference>) {
        firestore.collection(DataConstants.ROOMS_COLLECTION)
            .document(message.receiverId!!)
            .collection(DataConstants.MESSAGES_COLLECTION)
            .add(message)
            .addOnCompleteListener { docRef -> onCompleteListener.onComplete(docRef) }
    }

    fun addUserToRoom(
        authUser: FirebaseUser,
        room: Room,
        onCompleteListener: OnCompleteListener<Void>
    ) {
        room.addMember(authUser.uid)
        firestore.collection(DataConstants.ROOMS_COLLECTION)
            .document(room.id!!)
            .set(room)
            .addOnCompleteListener { task -> onCompleteListener.onComplete(task) }
    }

    fun removeUserFromRoom(
        authUser: FirebaseUser,
        room: Room,
        onCompleteListener: OnCompleteListener<Void>
    ) {
        room.removeMember(authUser.uid)
        firestore.collection(DataConstants.ROOMS_COLLECTION)
            .document(room.id!!)
            .set(room)
            .addOnCompleteListener { task -> onCompleteListener.onComplete(task) }
    }
}