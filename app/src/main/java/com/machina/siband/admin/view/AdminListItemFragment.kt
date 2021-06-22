package com.machina.siband.admin.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.admin.dialog.DialogAddItem
import com.machina.siband.admin.recycler.AdminListItemAdapter
import com.machina.siband.admin.viewmodel.AdminViewModel
import com.machina.siband.databinding.FragmentAdminListItemBinding

class AdminListItemFragment : Fragment(), DialogAddItem.DialogAddItemListener {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.fragmentAdminListItemFab.setOnClickListener {
            val dialog = DialogAddItem(this as DialogAddItem.DialogAddItemListener, "Tambah Item")
            dialog.show(parentFragmentManager, "AddItemDialog")
        }
    }

    private fun onDeleteItem(itemName: String) {
        viewModel.deleteItem(args.lantai, args.ruangan, itemName)
    }

    private fun setupObserver() {
        viewModel.listItem.observe(viewLifecycleOwner) {
            mAdapter.setData(it)
        }
    }

    private fun setupRecycler() {
        mAdapter = AdminListItemAdapter(this::onDeleteItem)

        val recycler = binding.fragmentAdminListItemRecycler
        val mLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewModel.getListItem(args.lantai, args.ruangan)
        viewModel.setSelectedRuangan(args.ruangan)

        recycler.apply {
            adapter = mAdapter
            layoutManager = mLayoutManager
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, itemName: String) {
        viewModel.addItem(args.lantai, args.ruangan, itemName)
        dialog.dismiss()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
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