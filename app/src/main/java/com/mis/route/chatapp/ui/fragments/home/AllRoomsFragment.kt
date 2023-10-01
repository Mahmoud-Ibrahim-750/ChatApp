package com.mis.route.chatapp.ui.fragments.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mis.route.chatapp.databinding.FragmentAllRoomsBinding
import com.mis.route.chatapp.model.ChatViewModel
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
        defineObservationActions()
    }

    private fun defineObservationActions() {
        sharedViewModel.allRooms.observe(viewLifecycleOwner, ::handleRoomsChange)
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
        binding.roomsRecycler.adapter = adapter
        sharedViewModel.getAllRooms()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}