package com.mis.route.chatapp.data.firebase

import com.google.firebase.auth.FirebaseUser

data class User(
    val id: String? = null,
    val username: String? = null,
    val email: String? = null,
    val profilePhoto: Int? = null
) {
//    companion object {
//        fun fromAuthUser(authUser: FirebaseUser) : User {
//            return User(id = authUser.uid, email = authUser.email)
//        }
//    }
}
