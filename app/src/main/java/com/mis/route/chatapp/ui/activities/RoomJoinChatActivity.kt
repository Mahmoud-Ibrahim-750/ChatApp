package com.mis.route.chatapp.ui.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mis.route.chatapp.R
import com.mis.route.chatapp.databinding.ActivityRoomJoinChatBinding
import com.mis.route.chatapp.model.ChatViewModel
import com.mis.route.chatapp.ui.UiConstants
import com.mis.route.chatapp.ui.fragments.chat.ChatFragment
import com.mis.route.chatapp.ui.fragments.createroom.model.Room
import com.mis.route.chatapp.ui.fragments.home.JoinRoomFragment

class RoomJoinChatActivity : AppCompatActivity() {
    private var _binding: ActivityRoomJoinChatBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ChatViewModel by viewModels()
    private lateinit var room: Room

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_room_join_chat)
        binding.apply {
            lifecycleOwner = this@RoomJoinChatActivity
            room = this@RoomJoinChatActivity.getRoom()
            roomJoinChatActivity = this@RoomJoinChatActivity
        }
        room = getRoom()
        initFragment()
        observeLiveData()
    }

    private fun observeLiveData() {
        sharedViewModel.roomJoined.observe(this, ::handleRoomJoin)
        sharedViewModel.roomLeft.observe(this, ::handleRoomLeft)
    }

    private fun handleRoomJoin(roomJoined: Boolean) {
        if (!roomJoined) return
        showFragment(ChatFragment(room))
    }

    private fun handleRoomLeft(roomLeft: Boolean) {
        if (!roomLeft) return
        returnToHome()
    }

    private fun returnToHome() {
        // TODO: debug this later
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initFragment() {
        val isMember = intent.getBooleanExtra(UiConstants.PASSED_MEMBER_FLAG, false)
        val fragment = if (isMember) ChatFragment(room) else JoinRoomFragment(room)
        showFragment(fragment)
    }

    @Suppress("DEPRECATION")
    private fun getRoom(): Room {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(UiConstants.PASSED_ROOM, Room::class.java)!!
        } else intent.getParcelableExtra(UiConstants.PASSED_ROOM)!!
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun showPopup(view: View) {
        val popup = PopupMenu(this, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_chat_options_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.leave_room -> {
                    sharedViewModel.leaveRoom(room)
                }
            }
            true
        }
        popup.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
