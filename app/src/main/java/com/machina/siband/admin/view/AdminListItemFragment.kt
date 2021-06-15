package com.machina.siband.admin.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.R
import com.machina.siband.admin.recycler.AdminListItemAdapter
import com.machina.siband.admin.recycler.AdminListRuanganAdapter
import com.machina.siband.admin.viewmodel.AdminViewModel
import com.machina.siband.databinding.FragmentAdminListItemBinding

class AdminListItemFragment : Fragment() {

    private var _binding: FragmentAdminListItemBinding? = null
    private val binding get() = _binding!!

    private val args: AdminListItemFragmentArgs by navArgs()
    private val viewModel: AdminViewModel by activityViewModels()

    private lateinit var mAdapter: AdminListItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminListItemBinding.inflate(layoutInflater)

        setupRecycler()
        setupObserver()

        return binding.root
    }

    private fun setupObserver() {
        viewModel.listItem.observe(viewLifecycleOwner) {
            mAdapter.setData(it)
        }
    }

    private fun setupRecycler() {
        mAdapter = AdminListItemAdapter()

        val recycler = binding.fragmentAdminListItemRecycler
        val mLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewModel.getListItem(args.lantai, args.ruangan)

        recycler.apply {
            adapter = mAdapter
            layoutManager = mLayoutManager
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        viewModel.clearListItem()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminListItemFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminListItemFragment().apply {

            }
    }
}