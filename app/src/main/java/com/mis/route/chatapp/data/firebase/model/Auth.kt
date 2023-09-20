package com.mis.route.chatapp.data.firebase.model

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Auth() {
    private lateinit var auth: Auth

//    fun getInstance(): Auth {
//        if (auth != null) return auth
//        else {
//            auth = Auth()
//        }
//    }

//    ##########################################################

//    // Initialize Firebase Auth
//    private var auth: FirebaseAuth = Firebase.auth
//
//    init {
//        // Initialize Firebase Auth
//    }
//
//    // called from onStart()
//    fun getCurrentUser(): FirebaseUser? {
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
////            reload() // TODO: check error here later
//        }
//        return currentUser
//    }
//
//
//    fun createAccount(email: String, password: String) {
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "createUserWithEmail:success")
//                    val user = auth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        baseContext,
//                        "Authentication failed.",
//                        Toast.LENGTH_SHORT,
//                    ).show()
//                    updateUI(null)
//                }
//            }
//    }
}