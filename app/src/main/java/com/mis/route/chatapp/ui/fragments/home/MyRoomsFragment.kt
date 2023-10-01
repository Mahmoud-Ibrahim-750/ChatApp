package com.mis.route.chatapp.ui.fragments.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.mis.route.chatapp.R
import com.mis.route.chatapp.data.firebase.DataConstants
import com.mis.route.chatapp.databinding.FragmentMyRoomsBinding
import com.mis.route.chatapp.model.ChatViewModel
import com.mis.route.chatapp.ui.UiConstants
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
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleRoomsLoading(rooms: List<Room>?) {
        (binding.roomsRecycler.adapter as RoomsAdapter).apply {
            roomsList = rooms
            notifyDataSetChanged()
        }
    }

    private fun initRecyclerView() {
        val adapter = RoomsAdapter(null)
        adapter.onRoomClickListener = RoomsAdapter.OnRoomClickListener { room ->
            navigateToChat(room)
        }
        binding.roomsRecycler.adapter = adapter
        sharedViewModel.getMyRooms()
    }

    private fun navigateToChat(room: Room) {
        val args = Bundle().apply {
            putParcelable(UiConstants.PASSED_ROOM, room)
        }
        findNavController().navigate(R.id.action_homeFragment_to_chatFragment, args)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}