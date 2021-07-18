package com.machina.siband.admin.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.admin.viewmodel.AdminViewModel
import com.machina.siband.databinding.FragmentAdminListLantaiBinding
import com.machina.siband.model.Lantai
import com.machina.siband.admin.recycler.AdminListLantaiAdapter

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.fragmentAdminListLantaiFab.setOnClickListener {
            val action = AdminListLantaiFragmentDirections.actionAdminListLantaiFragmentToAdminAddLantaiFragment()
            findNavController().navigate(action)
        }
    }

    private fun setupRecycler() {
        mAdapter = AdminListLantaiAdapter(this::onItemClick, this::onItemDelete)
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

    private fun onItemDelete(lantai: Lantai) {
        val title = "Yakin ingin menghapus ${lantai.nama}?"
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setPositiveButton("Hapus") { dialog, which ->
                viewModel.deleteLantai(lantai)
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

    }
}