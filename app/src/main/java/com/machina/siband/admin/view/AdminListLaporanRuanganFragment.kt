package com.machina.siband.admin.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.admin.viewmodel.AdminViewModel
import com.machina.siband.databinding.FragmentAdminListLaporanRuanganBinding
import com.machina.siband.model.LaporanRuangan
import com.machina.siband.user.recycler.ListLaporanUserAdapter
import com.machina.siband.user.viewModel.UserHomeViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [AdminListLaporanRuanganFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminListLaporanRuanganFragment(private val position: Int) : Fragment() {

    private var _binding: FragmentAdminListLaporanRuanganBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminViewModel by activityViewModels()

    private lateinit var mAdapter: ListLaporanUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminListLaporanRuanganBinding.inflate(layoutInflater, container, false)

        setupRecycler()
        setupObserver()

        return binding.root
    }

    private fun setupObserver() {
        when (position) {
            0 -> {
                viewModel.listLaporanNoProgressYet.observe(viewLifecycleOwner, { mAdapter.setData(it) })
            }
            1 -> {
                viewModel.listLaporanOnProgress.observe(viewLifecycleOwner, { mAdapter.setData(it) })
            }
            2 -> {
                viewModel.listLaporanDone.observe(viewLifecycleOwner, { mAdapter.setData(it) })
            }
        }
    }

    private fun setupRecycler() {
        mAdapter = ListLaporanUserAdapter(this::onItemLaporanClick)
        val recyclerView = binding.fragmentAdminListLaporanRecycler
        val mLinearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.apply {
            adapter = mAdapter
            layoutManager = mLinearLayoutManager
        }
    }

    private fun onItemLaporanClick(data: LaporanRuangan) {
        val action = AdminSwipeLaporanFragmentDirections
            .actionAdminSwipeLaporanFragmentToAdminReviewLaporanRuanganFragment(data)

        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "AdminListLaporanRuanganFragment"
    }
}