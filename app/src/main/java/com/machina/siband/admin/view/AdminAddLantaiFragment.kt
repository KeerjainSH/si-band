package com.machina.siband.admin.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.machina.siband.R
import com.machina.siband.admin.viewmodel.AdminViewModel
import com.machina.siband.databinding.FragmentAdminAddLantaiBinding
import com.machina.siband.model.Lantai
import com.machina.siband.user.view.UserFormLaporanFragment

class AdminAddLantaiFragment : Fragment() {

    private var _binding: FragmentAdminAddLantaiBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminAddLantaiBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.fragmentAdminAddLantaiSubmit.setOnClickListener {
            onSubmit()
        }

        binding.fragmentAdminAddLantaiPilihFoto.setOnClickListener {
            selectImage()
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.apply {
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent, "Select app for this action"),
            PICK_IMAGE_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null) {

            if (data.data != null){
                val imageUri = data.data
                if (imageUri != null) {
                    binding.fragmentAdminAddLantaiMap.apply {
                        setImageURI(imageUri)
                        visibility = View.VISIBLE
                    }
                    binding.fragmentLaporanDokumentasiIconContainer.visibility = View.GONE
                    viewModel.currentImageUri = imageUri
                }
            }
            binding.fragmentLaporanDokumentasiIconContainer.visibility = View.GONE
        }
    }

    private fun onSubmit() {
        val nama = binding.fragmentAdminAddLantaiNama.editText?.text.toString()
        val newLantai = Lantai(nama, 0)
        val imageUri = viewModel.currentImageUri

        if (!viewModel.isOnlyLetterOrDigit(nama)) {
            val message = "Nama Lantai hanya boleh menggunakan alphabet dan angka"
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            return
        }

        if (nama.isNotEmpty() && imageUri != null) {
            viewModel.addLantai(newLantai, imageUri)
            findNavController().navigateUp()
        } else {
            Toast.makeText(context, "Please fill all the form", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "AdminAddLantaiFragment"
        private const val PICK_IMAGE_CODE = 200
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminAddLantaiFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminAddLantaiFragment().apply {
            }
    }
}