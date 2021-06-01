package com.machina.siband.user.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.machina.siband.R
import com.machina.siband.databinding.FragmentFormLaporanBinding
import com.machina.siband.databinding.FragmentFormPelaporanBinding

class FormPelaporanFragment : Fragment() {

    private var _binding: FragmentFormPelaporanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFormPelaporanBinding.inflate(inflater)



        return binding.root
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
            FormPelaporanFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}