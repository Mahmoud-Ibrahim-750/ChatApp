package com.mis.route.chatapp.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mis.route.chatapp.databinding.FragmentJoinRoomBinding
import com.mis.route.chatapp.model.ChatViewModel
import com.mis.route.chatapp.ui.fragments.createroom.model.Room

class JoinRoomFragment(private val roomToShow: Room) : Fragment() {
    private var _binding: FragmentJoinRoomBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ChatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = this@JoinRoomFragment
            room = roomToShow
            viewModel = sharedViewModel
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
