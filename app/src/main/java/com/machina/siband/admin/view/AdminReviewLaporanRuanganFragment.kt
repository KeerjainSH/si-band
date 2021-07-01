package com.machina.siband.admin.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.StorageReference
import com.machina.siband.R
import com.machina.siband.admin.viewmodel.AdminViewModel
import com.machina.siband.databinding.FragmentAdminReviewLaporanRuanganBinding
import com.machina.siband.module.GlideApp
import com.machina.siband.repository.FirebaseStorageRepo
import com.machina.siband.user.view.UserFormLaporanFragment

class AdminReviewLaporanRuanganFragment : Fragment() {

    private var _binding: FragmentAdminReviewLaporanRuanganBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminViewModel by activityViewModels()
    private val args: AdminReviewLaporanRuanganFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminReviewLaporanRuanganBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        resolveForm()
        val status = resources.getStringArray(R.array.status)
        val mArrayAdapter = ArrayAdapter(requireContext(), R.layout.item_list_dropdown, status)
        (binding.fragmentAdminReviewLaporanStatus.editText as? AutoCompleteTextView)?.setAdapter(mArrayAdapter)

        binding.fragmentAdminReviewLaporanDokumentasiPilihFoto.setOnClickListener {
            selectImage()
        }
        binding.fragmentAdminReviewLaporanUpdate.setOnClickListener {
            onSubmitLaporan()
        }
    }

    private fun onSubmitLaporan() {
        val laporanRuangan = args.laporanRuangan
        val images = viewModel.getImagesUri().toList()
        val lastImages = laporanRuangan.dokumentasiPerbaikan
        val status = binding.fragmentAdminReviewLaporanStatus.editText?.text.toString()

        val count = if (images.isNotEmpty()) {
            images.size
        } else {
            lastImages
        }

        val newLaporanRuangan = laporanRuangan.copy(
            dokumentasiPerbaikan = count,
            status = status
        )

        viewModel.putNewLaporanRuangan(newLaporanRuangan, images)
        findNavController().navigateUp()
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

    private fun resolveForm() {
        val mLaporanRuangan = args.laporanRuangan
        val mLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            800
        ).also { it.setMargins(0, 20, 0, 20) }
        val email = mLaporanRuangan.email
        val lokasi = mLaporanRuangan.lokasi
        val nama = mLaporanRuangan.nama
        val tanggal = mLaporanRuangan.tanggal
        val tipe = mLaporanRuangan.tipe
        val status = mLaporanRuangan.status
        val keterangan = mLaporanRuangan.keterangan
        val dok = mLaporanRuangan.dokumentasi
        val dokPerbaikan = mLaporanRuangan.dokumentasiPerbaikan

        binding.fragmentAdminReviewLaporanLokasi.text = lokasi
        binding.fragmentAdminReviewLaporanItem.text = nama
        binding.fragmentAdminReviewLaporanTanggal.text = tanggal
        binding.fragmentAdminReviewLaporanTipe.editText?.setText(tipe)
        binding.fragmentAdminReviewLaporanKeterangan.editText?.setText(keterangan)
        binding.fragmentAdminReviewLaporanStatus.editText?.setText(status)

        if (dok > 0) {
            binding.fragmentAdminReviewLaporanDokumentasiIcon.visibility = View.GONE
            repeat(dok) {
                val storageRef = FirebaseStorageRepo.getLaporanImageRef(email, tanggal, lokasi, "${nama}$it")
                loadImageInternet(mLayoutParams, binding.fragmentAdminReviewLaporanDokumentasiContainer, storageRef)
            }
        }

        if (dokPerbaikan > 0) {
            binding.fragmentAdminReviewLaporanDokumentasiPerbaikanIcon.visibility = View.GONE
            repeat(dokPerbaikan) {
                val storageRef = FirebaseStorageRepo.getLaporanPerbaikanImageRef(email, tanggal, lokasi, "${nama}$it")
                loadImageInternet(mLayoutParams, binding.fragmentAdminReviewLaporanDokumentasiPerbaikanContainer, storageRef)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val mLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                800
            ).also { it.setMargins(0, 20, 0, 20) }

            removeExistImage(binding.fragmentAdminReviewLaporanDokumentasiPerbaikanContainer)
            viewModel.clearImagesUri()

            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    loadImageLocally(imageUri, mLayoutParams, binding.fragmentAdminReviewLaporanDokumentasiPerbaikanContainer)
                    viewModel.addImageToImagesUri(imageUri)

                    Log.d(TAG, "$imageUri")
                }
            } else if (data.data != null){
                val imageUri = data.data
                if (imageUri != null) {
                    loadImageLocally(imageUri, mLayoutParams, binding.fragmentAdminReviewLaporanDokumentasiPerbaikanContainer)
                    viewModel.addImageToImagesUri(imageUri)
                }
            }
            binding.fragmentAdminReviewLaporanDokumentasiPerbaikanIcon.visibility = View.GONE
        }
    }

    private fun loadImageLocally(imageUri: Uri, mLayoutParams: LinearLayout.LayoutParams, viewGroup: ViewGroup) {
        val imageView = ImageView(context)
        imageView.apply {
            setImageURI(imageUri)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            layoutParams = mLayoutParams
        }
        viewGroup.addView(imageView)
    }

    private fun loadImageInternet(
        mLayoutParams: LinearLayout.LayoutParams,
        viewGroup: ViewGroup,
        storageRef: StorageReference
    ) {
        val imageView = ImageView(context)
        imageView.apply {
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            layoutParams = mLayoutParams
        }
        context?.let {
            GlideApp.with(it)
                .load(storageRef)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView)
        }
        viewGroup.addView(imageView)
    }

    private fun removeExistImage(viewGroup: ViewGroup) {
        viewGroup.removeAllViews()
    }

    companion object {
        private const val TAG = "ReviewLaporanFragment"
        private const val PICK_IMAGE_CODE = 200

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdminReviewLaporanRuanganFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminReviewLaporanRuanganFragment().apply {
            }
    }
}