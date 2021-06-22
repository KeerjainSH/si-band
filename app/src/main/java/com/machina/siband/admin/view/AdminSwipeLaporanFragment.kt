package com.machina.siband.admin.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.machina.siband.admin.recycler.AdminSwipeViewAdapter
import com.machina.siband.admin.viewmodel.AdminViewModel
import com.machina.siband.databinding.FragmentAdminSwipeLaporanBinding
import com.machina.siband.databinding.FragmentUserSwipeLaporanBinding
import com.machina.siband.user.recycler.SwipeViewAdapter

class AdminSwipeLaporanFragment: Fragment() {
    private lateinit var mSwipeViewAdapter: AdminSwipeViewAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private var _binding: FragmentAdminSwipeLaporanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminViewModel by activityViewModels()

    private val tabTitleList = listOf("No Progress Yet", "On Progress", "Done")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminSwipeLaporanBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mSwipeViewAdapter = AdminSwipeViewAdapter(this)
        viewPager = binding.fragmentAdminSwipeLaporanPager
        viewPager.adapter = mSwipeViewAdapter
        tabLayout = binding.fragmentAdminSwipeLaporanTab

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitleList[position]
        }.attach()

        viewModel.getListLaporanBase()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}