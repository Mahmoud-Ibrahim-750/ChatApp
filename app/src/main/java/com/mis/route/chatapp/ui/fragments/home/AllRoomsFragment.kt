package com.mis.route.chatapp.ui.fragments.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mis.route.chatapp.databinding.FragmentAllRoomsBinding
import com.mis.route.chatapp.model.ChatViewModel
import com.mis.route.chatapp.ui.UiConstants
import com.mis.route.chatapp.ui.activities.RoomJoinChatActivity
import com.mis.route.chatapp.ui.fragments.createroom.model.Room
import com.mis.route.chatapp.ui.fragments.home.adapter.RoomsAdapter

/**
 * A simple [Fragment] subclass.
 */
class AllRoomsFragment : Fragment() {
    private var _binding: FragmentAllRoomsBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ChatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllRoomsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observeLiveData()
    }

    private fun observeLiveData() {
        sharedViewModel.allRooms.observe(viewLifecycleOwner, ::handleRoomsChange)
        // TODO: refer to this later
//        sharedViewModel.roomJoined.observe(viewLifecycleOwner, ::handleRoomJoin)
        sharedViewModel.allRoomsSearch.observe(viewLifecycleOwner, ::handleSearchRooms)
        sharedViewModel.searchViewVisible.observe(viewLifecycleOwner, ::handleSearch)
    }

    private fun handleSearch(searchViewVisible: Boolean) {
        if (!searchViewVisible) {
            stopSearching()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleSearchRooms(rooms: List<Room>) {
        Log.e("#######33", rooms.toString())
        if (rooms.isEmpty()) toggleRecyclerView(false)
        else {
            toggleRecyclerView(true)
            val adapter = (binding.roomsRecycler.adapter as RoomsAdapter)
            adapter.roomsList = rooms
            adapter.notifyDataSetChanged()
        }
    }

    private fun toggleRecyclerView(show: Boolean) {
        binding.roomsRecycler.isVisible = show
        binding.alternativeTextview.isVisible = !show
    }

    @SuppressLint("NotifyDataSetChanged")
    fun stopSearching() {
        Log.e("###########", "stop all rooms search")
        toggleRecyclerView(true)
        val adapter = (binding.roomsRecycler.adapter as RoomsAdapter)
        adapter.roomsList = sharedViewModel.allRooms.value
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleRoomsChange(rooms: List<Room>?) {
        (binding.roomsRecycler.adapter as RoomsAdapter).apply {
            roomsList = rooms
            notifyDataSetChanged()
        }
    }

    private fun initRecyclerView() {
        val adapter = RoomsAdapter(null)
        adapter.onRoomClickListener = RoomsAdapter.OnRoomClickListener { room ->
            sharedViewModel.userProfile.value?.let { user ->
                navigateToJoinChatActivity(room.isMember(user.id!!), room)
            }
        }
        binding.roomsRecycler.adapter = adapter
        sharedViewModel.getAllRooms()
    }

    private fun navigateToJoinChatActivity(isMember: Boolean, room: Room) {
        val intent = Intent(context, RoomJoinChatActivity::class.java)
        intent.putExtra(UiConstants.PASSED_MEMBER_FLAG, isMember)
        intent.putExtra(UiConstants.PASSED_ROOM, room)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}