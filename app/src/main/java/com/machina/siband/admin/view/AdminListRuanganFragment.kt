package com.machina.siband.admin.view

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.R
import com.machina.siband.admin.dialog.DialogAddRuangan
import com.machina.siband.admin.recycler.AdminListRuanganAdapter
import com.machina.siband.admin.viewmodel.AdminViewModel
import com.machina.siband.databinding.FragmentAdminListRuanganBinding
import com.machina.siband.model.Lantai
import com.machina.siband.model.Ruangan

class AdminListRuanganFragment : Fragment(), DialogAddRuangan.DialogAddItemListener {

  private var _binding: FragmentAdminListRuanganBinding? = null
  private val binding get() = _binding!!

  private val args: AdminListRuanganFragmentArgs by navArgs()
  private val viewModel: AdminViewModel by activityViewModels()

  private lateinit var mAdapter: AdminListRuanganAdapter

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = FragmentAdminListRuanganBinding.inflate(layoutInflater)

    setupRecycler()
    setupObserver()

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    binding.fragmentAdminListRuanganFab.setOnClickListener {
      val listAreaRuangan = viewModel.listAreaRuangan.value
      val temp = mutableListOf<String>()
      if (listAreaRuangan != null) {
        for (areaRuangan in listAreaRuangan) {
          temp.add(areaRuangan.nama)
          Log.d(TAG, "nama [${areaRuangan.nama}]")
        }
      }

      val arrayListAreaRuangan = temp.toTypedArray()

      val dialog = DialogAddRuangan(
        this as DialogAddRuangan.DialogAddItemListener,
        "Tambah Ruangan",
        arrayListAreaRuangan
      )
      dialog.show(parentFragmentManager, ADD_RUANGAN)
    }
  }

  private fun setupObserver() {
    viewModel.listRuangan.observe(viewLifecycleOwner) {
      mAdapter.setData(it)
    }

    viewModel.errorFlag.observe(viewLifecycleOwner) {
      if (it == true) {
        viewModel.errorFlag.value = false
        val message = viewModel.errorMessage
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
      }
    }
  }

  private fun setupRecycler() {
    mAdapter = AdminListRuanganAdapter(this::onItemDelete)

    val recycler = binding.fragmentAdminListRuanganRecycler
    val mLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    viewModel.getListRuangan(args.lantai)
    viewModel.getListAreaRuangan()

    recycler.apply {
      adapter = mAdapter
      layoutManager = mLayoutManager
    }
  }

  private fun onItemDelete(ruangan: Ruangan) {
    val title = "Yakin ingin menghapus ${ruangan.nama}?"
    val dialog = AlertDialog.Builder(requireContext())
      .setTitle(title)
      .setPositiveButton("Hapus") { dialog, which ->
        viewModel.deleteRuangan(args.lantai, ruangan.nama)
        dialog.dismiss()
      }
      .setNegativeButton("Batalkan") { dialog, _ ->
        dialog.dismiss()
      }

    dialog.create().show()

  }

  override fun onDialogPositiveClick(dialog: DialogFragment, itemName: String, area: String) {
    val listItem = arrayListOf<String>()
    val listKelompok = arrayListOf<String>()
    resources.getStringArray(R.array.item).toCollection(listItem)
    resources.getStringArray(R.array.kelompok).toCollection(listKelompok)

    viewModel.addRuangan(args.lantai, itemName, area, listItem, listKelompok)
    dialog.dismiss()
  }

  override fun onDialogNegativeClick(dialog: DialogFragment) {
    dialog.dismiss()
  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
    viewModel.clearListRuangan()
  }

  companion object {
    private const val TAG = "AdminListRuangan"
    const val ADD_RUANGAN = "AddRuanganDialog"
    const val DELETE_RUANGAN = "DeleteRuanganDialog"
  }
}