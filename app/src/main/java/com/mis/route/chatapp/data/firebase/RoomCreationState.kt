package com.mis.route.chatapp.data.firebase

// TODO: AuthState, LoginState and RoomCreationState are the same, should they be merged or left for future possible expansion?
enum class RoomCreationState {
    Idle,
    Loading,
    Succeeded,
    Failed
}