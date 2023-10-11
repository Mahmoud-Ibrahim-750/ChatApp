package com.mis.route.chatapp.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.toObject
import com.mis.route.chatapp.data.firebase.auth.FirebaseAuth
import com.mis.route.chatapp.data.firebase.auth.FirebaseAuth.AUTH_TAG
import com.mis.route.chatapp.data.firebase.auth.FirebaseAuth.getCurrentAuthUser
import com.mis.route.chatapp.data.firebase.firestore.FirebaseFirestore
import com.mis.route.chatapp.data.firebase.firestore.FirebaseFirestore.PROFILE_TAG
import com.mis.route.chatapp.data.firebase.model.UserProfile
import com.mis.route.chatapp.data.firebase.model.auth.AuthState
import com.mis.route.chatapp.data.firebase.model.auth.AuthStatus
import com.mis.route.chatapp.data.firebase.model.login.LoginState
import com.mis.route.chatapp.data.firebase.model.login.LoginStatus
import com.mis.route.chatapp.data.firebase.model.roomcreation.RoomCreationState
import com.mis.route.chatapp.data.firebase.model.roomcreation.RoomCreationStatus
import com.mis.route.chatapp.ui.fragments.createroom.model.Room
import com.mis.route.chatapp.ui.model.Message


class ChatViewModel : ViewModel() {
    // properties

    private var _authUser = MutableLiveData<FirebaseUser?>(null)
//    val authUser: LiveData<FirebaseUser?> get() = _authUser

    private var _userProfileProfile = MutableLiveData<UserProfile>(null)
    val userProfile: LiveData<UserProfile> get() = _userProfileProfile

    private var _authStatus = MutableLiveData(AuthStatus(AuthState.Idle))
    val authStatus: LiveData<AuthStatus> get() = _authStatus

    private var _loginStatus = SingleLiveEvent(LoginStatus(LoginState.Idle))
    val loginStatus: LiveData<LoginStatus> get() = _loginStatus

    private var _roomCreationStatus = SingleLiveEvent(RoomCreationStatus(RoomCreationState.Idle))
    val roomCreationStatus: LiveData<RoomCreationStatus> get() = _roomCreationStatus

    private var _allRooms = MutableLiveData<List<Room>?>(null)
    val allRooms: LiveData<List<Room>?> get() = _allRooms

    private var _myRooms = MutableLiveData<List<Room>?>(null)
    val myRooms: LiveData<List<Room>?> get() = _myRooms

    private var _myRoomsSearch = MutableLiveData<List<Room>>(listOf())
    val myRoomsSearch: LiveData<List<Room>> get() = _myRoomsSearch

    private var _allRoomsSearch = MutableLiveData<List<Room>>(listOf())
    val allRoomsSearch: LiveData<List<Room>> get() = _allRoomsSearch

    private var _roomJoined = MutableLiveData(false)
    val roomJoined: LiveData<Boolean> get() = _roomJoined

    private var _roomLeft = MutableLiveData(false)
    val roomLeft: LiveData<Boolean> get() = _roomLeft

    private var _messages = MutableLiveData<MutableList<Message>>(mutableListOf())
    val messages: LiveData<MutableList<Message>> get() = _messages

    private var _searchViewVisible = MutableLiveData(false)
    val searchViewVisible: LiveData<Boolean> get() = _searchViewVisible


    // functions
    fun createAccount(username: String, email: String, password: String) {
        val currentAuthUser = getCurrentAuthUser() ?: return
        _authStatus.value = AuthStatus(AuthState.Loading)
        FirebaseAuth.createAccount(email, password) { authTask ->
            if (authTask.isSuccessful) {
                _authUser.value = currentAuthUser
                Log.i(AUTH_TAG, "user authenticated")
                val userProfile = UserProfile(
                    currentAuthUser.uid,
                    username,
                    currentAuthUser.email,
                    0
                )
                createProfile(userProfile, currentAuthUser)
            } else {
                _authStatus.value =
                    AuthStatus(AuthState.Failed, authTask.exception?.localizedMessage.toString())
            }
        }
    }

    fun getCurrentUserProfile() {
        // get user and proceed or just return
        val userId = getCurrentAuthUser()?.uid.toString()
        FirebaseFirestore.getCurrentUserProfile(userId) { task ->
            if (task.isSuccessful) _userProfileProfile.value = task.result.toObject<UserProfile>()
            else task.exception?.localizedMessage?.let { Log.e(PROFILE_TAG, it) }
        }
    }

    fun singIn(email: String, password: String) {
        _loginStatus.value = LoginStatus(LoginState.Loading)
        FirebaseAuth.singIn(email, password) { loginTask ->
            if (loginTask.isSuccessful) {
                _authUser.value = getCurrentAuthUser()
                _loginStatus.value = LoginStatus(LoginState.Succeeded)
            } else {
                _loginStatus.value = LoginStatus(
                    LoginState.Failed,
                    loginTask.exception?.localizedMessage.toString()
                )
            }
        }
    }

    private fun createProfile(profile: UserProfile, authUser: FirebaseUser) {
        FirebaseFirestore.createProfile(profile, authUser) { task ->
            if (task.isSuccessful) {
                _userProfileProfile.value = profile
                _authStatus.value = AuthStatus(AuthState.Succeeded)
            } else _authStatus.value =
                AuthStatus(AuthState.Failed, task.exception?.localizedMessage)
        }
    }

    fun createRoom(room: Room) {
        _roomCreationStatus.value = RoomCreationStatus(RoomCreationState.Loading)
        FirebaseFirestore.createNewEmptyRoom { roomCreationTask ->
            if (roomCreationTask.isSuccessful) {
                room.id = roomCreationTask.result.id
                updateRoom(room)
            } else {
                _roomCreationStatus.value = RoomCreationStatus(
                    RoomCreationState.Failed,
                    roomCreationTask.exception?.localizedMessage
                )
            }
        }
    }

    private fun updateRoom(room: Room) {
        FirebaseFirestore.updateRoom(room) { roomUpdateTask ->
            if (roomUpdateTask.isSuccessful) {
                _roomCreationStatus.value = RoomCreationStatus(RoomCreationState.Succeeded)
            } else {
                _roomCreationStatus.value =
                    RoomCreationStatus(
                        RoomCreationState.Failed,
                        roomUpdateTask.exception?.localizedMessage
                    )
            }
        }
    }

    fun getAllRooms() {
        FirebaseFirestore.getAllRooms { task ->
            if (task.isSuccessful) _allRooms.value = task.result.toObjects(Room::class.java)
            else task.exception?.localizedMessage?.let { Log.e("RoomsTag", it) }
        }
    }

    fun getMyRooms() {
        val currentAuthUser = getCurrentAuthUser() ?: return
        FirebaseFirestore.getRoomsContainingUserId(currentAuthUser) { task ->
            if (task.isSuccessful) _myRooms.value = task.result.toObjects(Room::class.java)
            else task.exception?.localizedMessage?.let { Log.e("RoomsTag", it) }
        }
    }

    fun getMessages(roomId: String) {
        FirebaseFirestore.getMessagesByDateDescending(roomId) { task ->
            if (task.isSuccessful) {
                val messages = task.result.toObjects(Message::class.java)
                messages.sortBy { message -> message.dateTime }
                _messages.value = messages
            } else task.exception?.localizedMessage?.let { Log.e("MessagesTag", it) }
        }
    }

    fun sendMessage(message: Message) {
        FirebaseFirestore.sendMessage(message) { task ->
            if (task.isSuccessful) Log.d("MessagesTag", "success")
            else task.exception?.localizedMessage?.let { Log.e("MessagesTag", it) }
        }
    }

    fun joinRoom(room: Room) {
        val authUser = getCurrentAuthUser() ?: return
        FirebaseFirestore.addUserToRoom(authUser, room) { task ->
            if (task.isSuccessful) _roomJoined.value = true
            else {
                _roomJoined.value = false
                task.exception?.localizedMessage?.let { Log.e("RoomTag", it) }
            }
        }
    }

    fun leaveRoom(room: Room) {
        val currentAuthUser = getCurrentAuthUser() ?: return
        if (!room.isMember(currentAuthUser.uid)) return
        FirebaseFirestore.removeUserFromRoom(currentAuthUser, room) { task ->
            if (task.isSuccessful) _roomLeft.value = true
            else {
                _roomLeft.value = false
                task.exception?.localizedMessage?.let { Log.e("RoomTag", it) }
            }
        }
    }

    fun filterMyRoomsList(searchQuery: String) {
        filterRoomsList(searchQuery, _myRooms, _myRoomsSearch)
    }

    fun filterAllRoomsList(searchQuery: String) {
        filterRoomsList(searchQuery, _allRooms, _allRoomsSearch)
    }

    private fun filterRoomsList(
        searchQuery: String,
        sourceLiveData: MutableLiveData<List<Room>?>,
        resultsLiveData: MutableLiveData<List<Room>>
    ) {
        val results = mutableListOf<Room>()
        sourceLiveData.value?.forEach {
            if (it.title!!.contains(searchQuery)) results.add(it)
        }
        resultsLiveData.value = results
    }

    fun notifySearchStarted() {
        _searchViewVisible.value = true
    }

    fun notifySearchStopped() {
        _searchViewVisible.value = false
    }
}
