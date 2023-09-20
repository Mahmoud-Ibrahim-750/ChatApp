package com.mis.route.chatapp.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mis.route.chatapp.data.firebase.AuthState
import com.mis.route.chatapp.data.firebase.AuthStatus
import com.mis.route.chatapp.data.firebase.LoginState
import com.mis.route.chatapp.data.firebase.LoginStatus

class ChatViewModel : ViewModel() {
    // Initialize auth
    private var auth = Firebase.auth

    private var _user = MutableLiveData<FirebaseUser?>(null)
    val user: LiveData<FirebaseUser?> get() = _user

    private var _authStatus = MutableLiveData(AuthStatus(AuthState.Idle))
    val authStatus: LiveData<AuthStatus> get() = _authStatus

    private var _loginStatus = MutableLiveData(LoginStatus(LoginState.Idle))
    val loginStatus: LiveData<LoginStatus> get() = _loginStatus

    fun createAccount(email: String, password: String) {
        _authStatus.value = AuthStatus(AuthState.Loading)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    _user.value = auth.currentUser
                    _authStatus.value = AuthStatus(AuthState.Succeeded)
                } else {
                    _authStatus.value = AuthStatus(
                        AuthState.Failed,
                        authTask.exception?.localizedMessage.toString()
                    )
                }
            }
    }

    // may be called from onStart()
    fun getCurrentUser(): FirebaseUser? {
        // Check if user is signed in (non-null) and update UI accordingly.
        return auth.currentUser
    }

    fun singIn(email: String, password: String) {
        _loginStatus.value = LoginStatus(LoginState.Loading)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { loginTask ->
                if (loginTask.isSuccessful) {
                    _user.value = auth.currentUser
                    _loginStatus.value = LoginStatus(LoginState.Succeeded)
                } else {
                    _loginStatus.value = LoginStatus(
                        LoginState.Failed,
                        loginTask.exception?.localizedMessage.toString()
                    )
                }
            }
    }
}