package com.machina.siband.user.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.machina.siband.R
import com.machina.siband.databinding.FragmentUserFormPelaporanBinding
import com.machina.siband.model.LaporanBase
import com.machina.siband.model.LaporanRuangan
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
        binding.fragmentPelaporanDokumentasiPilihFoto.setOnClickListener { selectImage() }
    }


    private fun selectImage() {
        val intent = Intent()
        intent.apply {
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
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
            val mLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                800
            ).also { it.setMargins(0, 20, 0, 20) }

            removeExistImage()
            viewModel.clearImagesUri()

            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    loadImageLocally(imageUri, mLayoutParams)
                    viewModel.addImageToImagesUri(imageUri)

                    Log.d(TAG, "$imageUri")
                }
            } else if (data.data != null){
                val imageUri = data.data
                if (imageUri != null) {
                    loadImageLocally(imageUri, mLayoutParams)
                    viewModel.addImageToImagesUri(imageUri)
                }
            }
            binding.fragmentPelaporanDokumentasiIconContainer.visibility = View.GONE
        }
    }

    private fun loadImageLocally(imageUri: Uri, mLayoutParams: LinearLayout.LayoutParams) {
        val imageView = ImageView(context)
        imageView.apply {
            setImageURI(imageUri)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            layoutParams = mLayoutParams
        }
        binding.fragmentPelaporanDokumentasiContainer.addView(imageView)
    }

    private fun removeExistImage() {
        binding.fragmentPelaporanDokumentasiContainer.removeAllViews()
    }


    private fun onSubmit() {
        val email = "admin@gmail.com"
        val tanggal = "29-04-2021"
        val lokasi = binding.fragmentPelaporanLokasi.editText?.text.toString()
        val dokumentasi = viewModel.getImagesUri().size
        val item = binding.fragmentPelaporanItem.editText?.text.toString()
        val tipe = binding.fragmentPelaporanTipe.editText?.text.toString()
        val keterangan = binding.fragmentPelaporanKeterangan.editText?.text.toString()
        val status = UserHomeViewModel.NO_PROGRESS

        if (lokasi.isNotBlank() || item.isNotBlank() || tipe.isNotBlank()) {
            val laporanBase = LaporanBase(lokasi, email, tanggal, true)
            val laporanRuangan = LaporanRuangan(
                item,
                item,
                email,
                lokasi,
                tanggal,
                tipe,
                dokumentasi,
                keterangan,
                status,
                dokumentasiPerbaikan = 0,
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
        private const val PICK_IMAGE_CODE = 200

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