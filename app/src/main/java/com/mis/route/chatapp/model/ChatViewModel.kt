package com.mis.route.chatapp.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.mis.route.chatapp.data.firebase.AuthState
import com.mis.route.chatapp.data.firebase.AuthStatus
import com.mis.route.chatapp.data.firebase.DataConstants
import com.mis.route.chatapp.data.firebase.LoginState
import com.mis.route.chatapp.data.firebase.LoginStatus
import com.mis.route.chatapp.data.firebase.RoomCreationState
import com.mis.route.chatapp.data.firebase.RoomCreationStatus
import com.mis.route.chatapp.data.firebase.User
import com.mis.route.chatapp.ui.fragments.createroom.model.Room
import com.mis.route.chatapp.ui.model.Message

const val AUTH_TAG = "AuthTag"
const val PROFILE_TAG = "ProfileTag"

class ChatViewModel : ViewModel() {
    // properties
    // Initialize auth and fire store
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    private var _authUser = MutableLiveData<FirebaseUser?>(null)
//    val authUser: LiveData<FirebaseUser?> get() = _authUser

    private var _user = MutableLiveData<User>(null)
    val user: LiveData<User> get() = _user

    private var _authStatus = MutableLiveData(AuthStatus(AuthState.Idle))
    val authStatus: LiveData<AuthStatus> get() = _authStatus

    private var _loginStatus = MutableLiveData(LoginStatus(LoginState.Idle))
    val loginStatus: LiveData<LoginStatus> get() = _loginStatus

    private var _roomCreationStatus = MutableLiveData(RoomCreationStatus(RoomCreationState.Idle))
    val roomCreationStatus: LiveData<RoomCreationStatus> get() = _roomCreationStatus

    private var _allRooms = MutableLiveData<List<Room>?>(null)
    val allRooms: LiveData<List<Room>?> get() = _allRooms

    private var _myRooms = MutableLiveData<List<Room>?>(null)
    val myRooms: LiveData<List<Room>?> get() = _myRooms

    // functions
    fun createAccount(username: String, email: String, password: String) {
        _authStatus.value = AuthStatus(AuthState.Loading)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    _authUser.value = auth.currentUser
                    Log.i(AUTH_TAG, "user authenticated")
                    createProfile(username, _authUser)
                } else {
                    _authStatus.value = AuthStatus(
                        AuthState.Failed,
                        authTask.exception?.localizedMessage.toString()
                    )
                }
            }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun getCurrentUserProfile() {
        // get user and proceed or just return
        val user = getCurrentUser() ?: return
        val profileRef = firestore.collection(DataConstants.USERS_COLLECTION).document(user.uid)
        profileRef.get()
            .addOnSuccessListener { documentSnapshot ->
                _user.value = documentSnapshot.toObject<User>()
            }
            .addOnFailureListener { exception ->
                exception.localizedMessage?.let { Log.e(PROFILE_TAG, it) }
            }
    }

    fun singIn(email: String, password: String) {
        _loginStatus.value = LoginStatus(LoginState.Loading)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { loginTask ->
                if (loginTask.isSuccessful) {
                    _authUser.value = auth.currentUser
                    _loginStatus.value = LoginStatus(LoginState.Succeeded)
                } else {
                    _loginStatus.value = LoginStatus(
                        LoginState.Failed,
                        loginTask.exception?.localizedMessage.toString()
                    )
                }
            }
    }

    private fun createProfile(username: String, authUser: MutableLiveData<FirebaseUser?>) {
        val profile = User(
            authUser.value!!.uid,
            username,
            authUser.value!!.email!!,
            0
        )
        firestore.collection(DataConstants.USERS_COLLECTION)
            .document(authUser.value!!.uid)
            .set(profile)
            .addOnSuccessListener {
                _user.value = profile
                _authStatus.value = AuthStatus(AuthState.Succeeded)
            }
            .addOnFailureListener { e ->
                _authStatus.value = AuthStatus(
                    AuthState.Failed,
                    e.localizedMessage
                )
            }
    }

    fun createRoom(room: Room) {
        _roomCreationStatus.value = RoomCreationStatus(RoomCreationState.Loading)
        firestore.collection(DataConstants.ROOMS_COLLECTION)
            .add(Room())
            .addOnSuccessListener {
                room.id = it.id
                updateRoom(room)
            }
            .addOnFailureListener {
                _roomCreationStatus.value =
                    RoomCreationStatus(RoomCreationState.Failed, it.localizedMessage)
            }
    }

    private fun updateRoom(room: Room) {
        firestore.collection(DataConstants.ROOMS_COLLECTION)
            .document(room.id!!)
            .set(room)
            .addOnSuccessListener {
                _roomCreationStatus.value = RoomCreationStatus(RoomCreationState.Succeeded)
            }
            .addOnFailureListener {
                _roomCreationStatus.value =
                    RoomCreationStatus(RoomCreationState.Failed, it.localizedMessage)
            }
    }

    fun getAllRooms() {
        firestore.collection(DataConstants.ROOMS_COLLECTION)
            .get()
            .addOnSuccessListener {
                _allRooms.value = it.toObjects(Room::class.java)
            }
            .addOnFailureListener { exception ->
                exception.localizedMessage?.let { Log.e("RoomsTag", it) }
            }
    }

    fun getMyRooms() {
        firestore.collection(DataConstants.ROOMS_COLLECTION)
            .whereArrayContains("membersIds", getCurrentUser()?.uid.toString())
            .get()
            .addOnSuccessListener {
                _myRooms.value = it.toObjects(Room::class.java)
            }
            .addOnFailureListener { exception ->
                exception.localizedMessage?.let { Log.e("RoomsTag", it) }
            }
    }

    fun getMessages(roomId: String) : MutableList<Message> {
        var messages = mutableListOf<Message>()
        firestore.collection(DataConstants.ROOMS_COLLECTION)
            .document(roomId)
            .collection(DataConstants.MESSAGES_COLLECTION)
            .get()
            .addOnSuccessListener {
                messages = it.toObjects(Message::class.java)
            }
            .addOnFailureListener { exception ->
                exception.localizedMessage?.let { Log.e("MessagesTag", it) }
            }
        return messages
    }

    fun sendMessage(message: Message) {
        firestore.collection(DataConstants.ROOMS_COLLECTION)
            .document(message.receiverId!!)
            .collection(DataConstants.MESSAGES_COLLECTION)
            .add(message)
            .addOnSuccessListener {
                Log.d("MessagesTag", "success")
            }
            .addOnFailureListener { exception ->
                exception.localizedMessage?.let { Log.e("MessagesTag", it) }
            }
    }
}
