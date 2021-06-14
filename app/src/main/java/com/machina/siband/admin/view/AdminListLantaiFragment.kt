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
import com.machina.siband.databinding.FragmentAdminListLantaiBinding
import com.machina.siband.model.Lantai
import com.machina.siband.user.recycler.AdminListLantaiAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [AdminListLantaiFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminListLantaiFragment : Fragment() {

    private var _binding : FragmentAdminListLantaiBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminViewModel by activityViewModels()

    private lateinit var mAdapter: AdminListLantaiAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminListLantaiBinding.inflate(inflater)

        setupRecycler()
        setupObserver()

        return binding.root
    }

    private fun setupRecycler() {
        mAdapter = AdminListLantaiAdapter(this::onItemClick)
        val recycler = binding.fragmentAdminListLantaiRecycler
        val mLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewModel.getListLantai()

        recycler.apply {
            adapter = mAdapter
            layoutManager = mLayoutManager
        }
    }

    private fun setupObserver() {
        viewModel.listLantai.observe(viewLifecycleOwner) {
            mAdapter.setData(it)
        }
    }

    private fun onItemClick(lantai: Lantai) {
        val action = AdminListLantaiFragmentDirections
            .actionAdminListLantaiFragmentToAdminListRuanganFragment(lantai)

        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminListLantai.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminListLantaiFragment().apply {

            }
    }
}