package com.machina.siband.admin.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.R
import com.machina.siband.admin.recycler.AdminListItemAdapter
import com.machina.siband.admin.recycler.AdminListLetakRuanganAdapter
import com.machina.siband.admin.viewmodel.AdminViewModel
import com.machina.siband.databinding.FragmentAdminListAreaRuanganBinding
import com.machina.siband.databinding.FragmentAdminListItemBinding
import com.machina.siband.model.AreaRuangan

class AdminListAreaRuanganFragment : Fragment() {

    private var _binding: FragmentAdminListAreaRuanganBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminViewModel by activityViewModels()

    private lateinit var mAdapter: AdminListLetakRuanganAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminListAreaRuanganBinding.inflate(inflater, container, false)

        setupRecycler()
        setupObserver()
        binding.fragmentAdminListLetakRuanganFab.setOnClickListener {
            val action = AdminListAreaRuanganFragmentDirections.actionAdminListLetakRuanganFragmentToAdminTambahAreaRuanganFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }

    private fun setupRecycler() {
        mAdapter = AdminListLetakRuanganAdapter(this::onDeleteAreaRuangan)

        val recycler = binding.fragmentAdminListLetakRuanganRecycler
        val mLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewModel.getListAreaRuangan()

        recycler.apply {
            adapter = mAdapter
            layoutManager = mLayoutManager
        }
    }

    private fun setupObserver() {
        viewModel.listAreaRuangan.observe(viewLifecycleOwner) {
            mAdapter.setData(it)
        }
    }

    private fun onDeleteAreaRuangan(areaRuangan: AreaRuangan) {
        val title = "Yakin ingin menghapus ${areaRuangan.nama}?"
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setPositiveButton("Hapus") { dialog, which ->
                viewModel.deleteAreaRuangan(areaRuangan)
                dialog.dismiss()
            }
            .setNegativeButton("Batalkan") { dialog, _ ->
                dialog.dismiss()
            }

        dialog.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "AdminListAreaRuangan"
    }
}