package com.machina.siband.user.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.machina.siband.R
import com.machina.siband.databinding.FragmentSwipeLaporanBinding
import com.machina.siband.recycler.SwipeViewAdapter

class SwipeLaporanFragment: Fragment() {
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    private lateinit var mSwipeViewAdapter: SwipeViewAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private var _binding: FragmentSwipeLaporanBinding? = null
    private val binding get() = _binding!!

    private val tabTitleList = listOf("No Progress Yet", "On Progress", "Done")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSwipeLaporanBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mSwipeViewAdapter = SwipeViewAdapter(this)
        viewPager = binding.fragmentSwipeLaporanPager
        viewPager.adapter = mSwipeViewAdapter
        tabLayout = binding.fragmentSwipeLaporanTab
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitleList[position]
        }.attach()
    }
}
// No Progress Yet, On Progress, dan Done