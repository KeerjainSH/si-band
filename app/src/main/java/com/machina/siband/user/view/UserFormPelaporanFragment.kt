package com.machina.siband.user.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.machina.siband.R
import com.machina.siband.databinding.FragmentUserFormPelaporanBinding
import com.machina.siband.user.model.LaporanBase
import com.machina.siband.user.model.LaporanRuangan
import com.machina.siband.user.viewModel.UserHomeViewModel

class UserFormPelaporanFragment : Fragment() {

    private var _binding: FragmentUserFormPelaporanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserHomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserFormPelaporanBinding.inflate(inflater)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tipeKerusakan = resources.getStringArray(R.array.tipe)

        val mArrayAdapter = ArrayAdapter(requireContext(), R.layout.item_list_dropdown, tipeKerusakan)
        (binding.fragmentPelaporanTipe.editText as? AutoCompleteTextView)?.setAdapter(mArrayAdapter)

        binding.fragmentPelaporanSubmit.setOnClickListener { onSubmit() }
    }

    private fun onSubmit() {
        val email = "admin@gmail.com"
        val tanggal = "29-04-2021"
        val lokasi = binding.fragmentPelaporanLokasi.editText?.text.toString()

        val item = binding.fragmentPelaporanItem.editText?.text.toString()
        val tipe = binding.fragmentPelaporanTipe.editText?.text.toString()
        val keterangan = binding.fragmentPelaporanKeterangan.editText?.text.toString()
        val status = UserHomeViewModel.NO_PROGRESS

        if (lokasi.isNotBlank() || item.isNotBlank() || tipe.isNotBlank()) {
            val laporanBase = LaporanBase(lokasi, email, tanggal, true)
            val laporanRuangan = LaporanRuangan(
                item,
                item,
                lokasi,
                tanggal,
                tipe,
                dokumentasi = "",
                keterangan,
                status,
                dokumentasiPerbaikan = "",
                true
            )

            viewModel.putFormPelaporan(laporanBase, laporanRuangan)
        }
        findNavController().navigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "FormPelaporanFragment"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FormPelaporanFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFormPelaporanFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}