package com.machina.siband.admin.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.machina.siband.R
import com.machina.siband.admin.viewmodel.AdminViewModel
import com.machina.siband.databinding.FragmentAdminTambahAreaRuanganBinding
import com.machina.siband.model.AreaRuangan

class AdminTambahAreaRuanganFragment : Fragment() {

    private var _binding: FragmentAdminTambahAreaRuanganBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminTambahAreaRuanganBinding.inflate(inflater, container, false)

        binding.fragmentAdminTambahAreaRuanganButton.setOnClickListener {
            onTambahArea()
        }

        return binding.root
    }

    private fun onTambahArea() {
        val id = binding.fragmentAdminTambahAreaRuanganRadio.checkedRadioButtonId
        val nama = binding.fragmentAdminTambahAreaRuanganNama.editText?.text.toString()
        var warna = ""

        if (id == View.NO_ID || nama.isBlank()) {
            Toast.makeText(requireContext(), "Nama dan Warna cannot be empty", Toast.LENGTH_LONG).show()
            return
        }

        when (id) {
            binding.fragmentAdminTambahAreaRuanganMerah.id -> warna = "#ff99a5"
            binding.fragmentAdminTambahAreaRuanganHijau.id -> warna = "#99ffb4"
            binding.fragmentAdminTambahAreaRuanganKuning.id -> warna = "#fff599"
            binding.fragmentAdminTambahAreaRuanganBiru.id -> warna = "#99fff3"
            binding.fragmentAdminTambahAreaRuanganUngu.id -> warna = "#e6a8ff"
            binding.fragmentAdminTambahAreaRuanganOren.id -> warna = "#ffc1a8"
        }

        val areaRuangan = AreaRuangan(nama, warna)
        viewModel.addAreaRuangan(areaRuangan)
        findNavController().navigateUp()
    }

    companion object {
        private const val TAG = "AdminTambahAreaRuangan"
    }
}