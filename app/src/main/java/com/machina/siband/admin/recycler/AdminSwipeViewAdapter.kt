package com.machina.siband.admin.recycler

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.machina.siband.admin.view.AdminListLaporanRuanganFragment

class AdminSwipeViewAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return AdminListLaporanRuanganFragment(position)
    }
}