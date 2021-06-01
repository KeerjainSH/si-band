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
import androidx.navigation.fragment.navArgs
import com.machina.siband.R
import com.machina.siband.databinding.FragmentFormLaporanBinding
import com.machina.siband.user.viewModel.UserHomeViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [FormLaporanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FormLaporanFragment : Fragment() {

    private var _binding: FragmentFormLaporanBinding? = null
    private val binding get() = _binding!!

    private val args: FormLaporanFragmentArgs by navArgs()
    private val viewModel: UserHomeViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFormLaporanBinding.inflate(inflater, container, false)

        resolveForm()


        val tipeKerusakan = resources.getStringArray(R.array.tipe)

        val mArrayAdapter = ArrayAdapter(requireContext(), R.layout.item_list_dropdown, tipeKerusakan)
        (binding.fragmentLaporanTipe.editText as? AutoCompleteTextView)?.setAdapter(mArrayAdapter)

        binding.fragmentLaporanSubmit.setOnClickListener {
            onSubmitLaporan()
        }

        return binding.root
    }


    private fun onSubmitLaporan() {
        val nama = args.laporanRuangan?.nama
        val newTipe = binding.fragmentLaporanTipe.editText?.text.toString()
        val newKeterangan = binding.fragmentLaporanKeterangan.editText?.text.toString()

        if (nama != null) {
            viewModel.applyLocalChangeLaporan(nama, newTipe, newKeterangan)
        }
        findNavController().navigateUp()
    }


    // Fill the form if the selected item is already submitted within the same day
    private fun resolveForm() {
        val lokasi = args.laporanRuangan?.lokasi.toString()
        val tipeKerusakan = args.laporanRuangan?.tipe.toString()
        val dokumentasi = args.laporanRuangan?.dokumentasi.toString()
        val keterangan = args.laporanRuangan?.keterangan.toString()


        binding.fragmentLaporanLokasi.text = lokasi
        binding.fragmentLaporanTipe.editText?.setText(tipeKerusakan)
        binding.fragmentLaporanKeterangan.editText?.setText(keterangan)
    }

    companion object {

        const val WITH_DATA = "withData"
        const val WITHOUT_DATA = "withoutData"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LaporanFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FormLaporanFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}