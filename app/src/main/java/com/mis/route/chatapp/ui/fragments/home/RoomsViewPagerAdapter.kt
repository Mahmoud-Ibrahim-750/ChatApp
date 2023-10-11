package com.mis.route.chatapp.ui.fragments.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class RoomsViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyRoomsFragment()
            1 -> AllRoomsFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
