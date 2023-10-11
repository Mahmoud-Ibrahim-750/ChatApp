package com.mis.route.chatapp.data.firebase.model.auth

// TODO: should the state be returned back to Idle or should we use a single live event
enum class AuthState {
    Idle,
    Loading,
    Succeeded,
    Failed
}