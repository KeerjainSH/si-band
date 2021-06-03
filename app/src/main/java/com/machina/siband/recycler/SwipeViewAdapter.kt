package com.machina.siband.recycler

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.machina.siband.user.view.UserLaporanFragment
import com.machina.siband.user.viewModel.UserHomeViewModel

class SwipeViewAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return UserLaporanFragment(position)
    }
}