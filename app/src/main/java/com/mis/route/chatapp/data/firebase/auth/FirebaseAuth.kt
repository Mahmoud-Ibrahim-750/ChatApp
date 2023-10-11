package com.mis.route.chatapp.data.firebase.auth

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object FirebaseAuth {
    const val AUTH_TAG = "AuthTag"

    private val auth = Firebase.auth

    fun getCurrentAuthUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun createAccount(
        email: String,
        password: String,
        onCompleteListener: OnCompleteListener<AuthResult>
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask -> onCompleteListener.onComplete(authTask) }
    }

    fun singIn(
        email: String,
        password: String,
        onCompleteListener: OnCompleteListener<AuthResult>
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { loginTask -> onCompleteListener.onComplete(loginTask) }
    }

}