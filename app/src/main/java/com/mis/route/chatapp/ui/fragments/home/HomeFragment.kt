package com.mis.route.chatapp.ui.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
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
        initSearchBar()
        initTabLayoutWithViewPager()
        initDrawerLayout()
    }

    private fun initSearchBar() {
        binding.searchLayout.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val viewPager = binding.viewPager
                if (viewPager.currentItem == 0) {
                    Log.e("################", "my rooms")
                    sharedViewModel.filterMyRoomsList(newText.toString())
                } else {
                    Log.e("################", "all rooms")
                    sharedViewModel.filterAllRoomsList(newText.toString())
                }
                return false
            }

        })

        binding.searchIcon.setOnClickListener {
            toggleSearchView(true)
            sharedViewModel.notifySearchStarted()
        }

        binding.searchCloseImage.setOnClickListener {
            toggleSearchView(false)
            sharedViewModel.notifySearchStopped()
        }
    }

    private fun toggleSearchView(show: Boolean) {
        binding.searchView.isVisible = show
        binding.searchIcon.isVisible = !show
        binding.drawerToggle.isVisible = !show
        binding.titleTextview.isVisible = !show
    }

    private fun initTabLayoutWithViewPager() {
        val pagerAdapter = RoomsViewPagerAdapter(this@HomeFragment)
        binding.viewPager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "My Rooms"
                1 -> "Browse"
                else -> null
            }
        }.attach()
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

    fun navigateToCreateRoom() {
        findNavController().navigate(R.id.action_homeFragment_to_createRoomFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
