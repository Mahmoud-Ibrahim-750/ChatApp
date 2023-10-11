package com.mis.route.chatapp.ui.fragments.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mis.route.chatapp.databinding.FragmentMyRoomsBinding
import com.mis.route.chatapp.model.ChatViewModel
import com.mis.route.chatapp.ui.UiConstants
import com.mis.route.chatapp.ui.activities.RoomJoinChatActivity
import com.mis.route.chatapp.ui.fragments.createroom.model.Room
import com.mis.route.chatapp.ui.fragments.home.adapter.RoomsAdapter

/**
 * A simple [Fragment] subclass.
 */
class MyRoomsFragment : Fragment() {
    private var _binding: FragmentMyRoomsBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ChatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyRoomsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observeLiveData()
    }

    private fun observeLiveData() {
        sharedViewModel.myRooms.observe(viewLifecycleOwner, ::handleRoomsLoading)
        sharedViewModel.myRoomsSearch.observe(viewLifecycleOwner, ::handleSearchRooms)
        sharedViewModel.searchViewVisible.observe(viewLifecycleOwner, ::handleSearch)
    }

    private fun handleSearch(searchViewVisible: Boolean) {
        if (!searchViewVisible) stopSearching()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleSearchRooms(rooms: List<Room>) {
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
        toggleRecyclerView(true)
        val adapter = (binding.roomsRecycler.adapter as RoomsAdapter)
        adapter.roomsList = sharedViewModel.myRooms.value
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleRoomsLoading(rooms: List<Room>?) {
        (binding.roomsRecycler.adapter as RoomsAdapter).apply {
            rooms?.let {
                if (it.isEmpty()) {
                    toggleRecyclerView(false)
                    return@apply
                }
                toggleRecyclerView(true)
                roomsList = rooms
                notifyDataSetChanged()
            }
        }
    }

    private fun initRecyclerView() {
        toggleRecyclerView(false)
        val adapter = RoomsAdapter(null)
        adapter.onRoomClickListener = RoomsAdapter.OnRoomClickListener { room ->
            navigateToChat(room)
        }
        binding.roomsRecycler.adapter = adapter
        sharedViewModel.getMyRooms()
    }

    private fun navigateToChat(room: Room) {
        val intent = Intent(context, RoomJoinChatActivity::class.java)
        intent.putExtra(UiConstants.PASSED_MEMBER_FLAG, true)
        intent.putExtra(UiConstants.PASSED_ROOM, room)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}