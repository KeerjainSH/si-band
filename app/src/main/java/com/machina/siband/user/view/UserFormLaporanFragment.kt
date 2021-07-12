package com.machina.siband.user.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.machina.siband.R
import com.machina.siband.databinding.FragmentUserFormLaporanBinding
import com.machina.siband.module.GlideApp
import com.machina.siband.repository.FirebaseStorageRepo
import com.machina.siband.user.viewModel.UserHomeViewModel
import java.io.File
import java.io.IOException

class UserFormLaporanFragment : Fragment() {

    private var _binding: FragmentUserFormLaporanBinding? = null
    private val binding get() = _binding!!

    private val args: UserFormLaporanFragmentArgs by navArgs()
    private val viewModel: UserHomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserFormLaporanBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        resolveForm()
        val tipeKerusakan = resources.getStringArray(R.array.tipe)
        val mArrayAdapter = ArrayAdapter(requireContext(), R.layout.item_list_dropdown, tipeKerusakan)
        (binding.fragmentLaporanTipe.editText as? AutoCompleteTextView)?.setAdapter(mArrayAdapter)

        binding.fragmentLaporanSubmit.setOnClickListener { onSubmitLaporan() }
        binding.fragmentLaporanDokumentasiPilihFoto.setOnClickListener { selectImage() }
        binding.fragmentLaporanDokumentasiAmbilFoto.setOnClickListener { captureImage() }
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

    private fun captureImage() {
        // Ask Camera use permission to user
        val packageManager = activity?.packageManager ?: return
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                listOf(Manifest.permission.CAMERA).toTypedArray(),
                REQ_CAMERA_PERMISSION
            )
            return
        }

        // Create intent to launch camera app
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also { component ->
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.machina.siband",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent,
                        CAPTURE_IMAGE_CODE
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val mLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            800
        ).also { it.setMargins(0, 20, 0, 20) }

        if (requestCode == PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null) {
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
            binding.fragmentLaporanDokumentasiIconContainer.visibility = View.GONE
        } else if (resultCode == Activity.RESULT_OK && requestCode == CAPTURE_IMAGE_CODE) {
            removeExistImage()
            viewModel.clearImagesUri()
            binding.fragmentLaporanDokumentasiIconContainer.visibility = View.GONE
            val imageUri = Uri.fromFile(File(currentPhotoPath))
            loadImageLocally(imageUri, mLayoutParams)
            viewModel.addImageToImagesUri(imageUri)
        }
    }

    lateinit var currentPhotoPath: String
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val storageDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "temp", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun removeExistImage() {
        binding.fragmentLaporanDokumentasiContainer.removeAllViews()
    }

    private fun onSubmitLaporan() {
        val laporanRuangan = args.laporanRuangan
        val newTipe = binding.fragmentLaporanTipe.editText?.text.toString()
        val newKeterangan = binding.fragmentLaporanKeterangan.editText?.text.toString()
        val images = viewModel.getImagesUri().toList()
        val lastImages = laporanRuangan.dokumentasi
        val count = if (images.isNotEmpty()) {
            images.size
        } else {
            lastImages
        }
        val newLaporanRuangan = laporanRuangan.copy(
            tipe = newTipe,
            keterangan =  newKeterangan,
            dokumentasi = count,
            isChecked = true
        )

        viewModel.putNewLaporanRuangan(newLaporanRuangan, images)
        findNavController().navigateUp()
    }

    private fun loadImageLocally(imageUri: Uri, mLayoutParams: LinearLayout.LayoutParams) {
        val imageView = ImageView(context)
        imageView.apply {
            setImageURI(imageUri)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            layoutParams = mLayoutParams
        }
        binding.fragmentLaporanDokumentasiContainer.addView(imageView)
    }

    private fun loadImageInternet(lokasi: String, nama: String, mLayoutParams: LinearLayout.LayoutParams, index: Int) {
        val imageView = ImageView(context)
        imageView.apply {
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            layoutParams = mLayoutParams
        }

        val email = args.laporanRuangan.email
        val tanggal = args.laporanRuangan.tanggal
        val imageRef = FirebaseStorageRepo.getLaporanImageRef(email, tanggal, lokasi, "${nama}$index")

        context?.let {
            GlideApp.with(it)
                .load(imageRef)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView)
        }
        binding.fragmentLaporanDokumentasiContainer.addView(imageView)
    }


    // Fill the form if the selected item is already submitted within the same day
    private fun resolveForm() {
        val lokasi = args.laporanRuangan.lokasi
        val nama = args.laporanRuangan.nama
        val tipeKerusakan = args.laporanRuangan.tipe
        val dokumentasi = args.laporanRuangan.dokumentasi
        val keterangan = args.laporanRuangan.keterangan

        binding.fragmentLaporanLokasi.text = nama
        binding.fragmentLaporanTipe.editText?.setText(tipeKerusakan)
        binding.fragmentLaporanKeterangan.editText?.setText(keterangan)

        val mLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            800
        ).also { it.setMargins(0, 20, 0, 20) }

        if (dokumentasi > 0) {
            binding.fragmentLaporanDokumentasiIconContainer.visibility = View.GONE
            repeat(dokumentasi) {
                loadImageInternet(lokasi, nama,  mLayoutParams, it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        viewModel.clearImagesUri()
    }

    companion object {
        private const val PICK_IMAGE_CODE = 200
        private const val CAPTURE_IMAGE_CODE = 204
        private const val REQ_CAMERA_PERMISSION = 122
        private const val TAG = "userFormLaporanFragment"
    }
}