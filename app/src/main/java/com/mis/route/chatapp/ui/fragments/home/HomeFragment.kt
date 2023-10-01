package com.mis.route.chatapp.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.mis.route.chatapp.R
import com.mis.route.chatapp.databinding.FragmentHomeBinding
import com.mis.route.chatapp.databinding.HeaderNavigationDrawerBinding
import com.mis.route.chatapp.model.ChatViewModel

// TODO: back button and single live event?
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: ChatViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
            homeFragment = this@HomeFragment
        }
        initRoomsViewPager()
        initDrawerLayout()
    }

    private fun initDrawerLayout() {
        binding.drawerToggle.setOnClickListener {
            binding.drawer.open()
        }
        // TODO: debug this later
        val drawerHeaderBinding = HeaderNavigationDrawerBinding.inflate(layoutInflater)
        drawerHeaderBinding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = sharedViewModel
        }
        sharedViewModel.getCurrentUserProfile()

        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.my_rooms_nav_tab -> {
                    true
                }

                R.id.all_rooms_nav_tab -> {
                    true
                }

                else -> {
                    true
                }
            }
        }
    }

    private fun initRoomsViewPager() {
        // TODO: debug the toolbar to show the tab layout later
//        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        val adapter = RoomsViewPagerAdapter(requireActivity().supportFragmentManager)
        adapter.addFragment(MyRoomsFragment(), "My Rooms")
        adapter.addFragment(AllRoomsFragment(), "Browse")
        binding.viewPager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    fun navigateToCreateRoom() {
        findNavController().navigate(R.id.action_homeFragment_to_createRoomFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
