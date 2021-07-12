package com.machina.siband.user.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.machina.siband.R
import com.machina.siband.databinding.FragmentUserFormPelaporanBinding
import com.machina.siband.model.LaporanBase
import com.machina.siband.model.LaporanRuangan
import com.machina.siband.model.Ruangan.Companion.toRuangan
import com.machina.siband.repository.AdminFirestoreRepo
import com.machina.siband.user.viewModel.UserHomeViewModel
import java.io.File
import java.io.IOException


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
        val arrayLantai = viewModel.arrayListLantai.value
        val tipeKerusakan = resources.getStringArray(R.array.tipe)
        val arrayItem = resources.getStringArray(R.array.item)

        if (arrayLantai.isNullOrEmpty()) {
            return
        }

        val lantaiAdapter = ArrayAdapter(requireContext(), R.layout.item_list_dropdown, arrayLantai)
        (binding.fragmentPelaporanLantai.editText as? AutoCompleteTextView)?.setAdapter(lantaiAdapter)

        val tipeAdapter = ArrayAdapter(requireContext(), R.layout.item_list_dropdown, tipeKerusakan)
        (binding.fragmentPelaporanTipe.editText as? AutoCompleteTextView)?.setAdapter(tipeAdapter)

        val itemAdapter = ArrayAdapter(requireContext(), R.layout.item_list_dropdown, arrayItem)
        (binding.fragmentPelaporanItem.editText as? AutoCompleteTextView)?.setAdapter(itemAdapter)

        binding.fragmentPelaporanLantai.editText?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "current lantai [${s.toString()}]")
                binding.fragmentPelaporanRuangan.editText?.text = null
                binding.fragmentPelaporanRuangan.isEnabled = false

                AdminFirestoreRepo.getListRuanganRef(s.toString())
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val listRuangan = snapshot.mapNotNull { it.toRuangan() }
                        val arrayRuangan = listRuangan.map { it.nama }

                        val ruanganAdapter = ArrayAdapter(requireContext(), R.layout.item_list_dropdown, arrayRuangan)
                        (binding.fragmentPelaporanRuangan.editText as? AutoCompleteTextView)?.setAdapter(ruanganAdapter)

                        binding.fragmentPelaporanRuangan.isEnabled = true
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Something when wrong please try again later",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
            }

        })

        binding.fragmentPelaporanSubmit.setOnClickListener { onSubmit() }
        binding.fragmentPelaporanDokumentasiPilihFoto.setOnClickListener { selectImage() }
        binding.fragmentPelaporanDokumentasiAmbilFoto.setOnClickListener { captureImage() }
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
                    startActivityForResult(takePictureIntent, CAPTURE_IMAGE_CODE)
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

        if (resultCode == Activity.RESULT_OK && data != null && requestCode == PICK_IMAGE_CODE) {
            removeExistImage()
            viewModel.clearImagesUri()
            binding.fragmentPelaporanDokumentasiIconContainer.visibility = View.GONE
            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    loadImageLocally(imageUri, mLayoutParams)
                    viewModel.addImageToImagesUri(imageUri)
                    Log.d(TAG, "$imageUri")
                }
            } else if (data.data != null) {
                val imageUri = data.data
                if (imageUri != null) {
                    loadImageLocally(imageUri, mLayoutParams)
                    viewModel.addImageToImagesUri(imageUri)
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == CAPTURE_IMAGE_CODE) {
            removeExistImage()
            viewModel.clearImagesUri()
            binding.fragmentPelaporanDokumentasiIconContainer.visibility = View.GONE
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
        val email = viewModel.getCurrentEmail()
        val tanggal = viewModel.getCurrentDate()
        val lantai = binding.fragmentPelaporanLantai.editText?.text.toString()
        val lokasi = binding.fragmentPelaporanRuangan.editText?.text.toString()
        val dokumentasi = viewModel.getImagesUri().toList()
        val item = binding.fragmentPelaporanItem.editText?.text.toString()
        val tipe = binding.fragmentPelaporanTipe.editText?.text.toString()
        val keterangan = binding.fragmentPelaporanKeterangan.editText?.text.toString()
        val status = UserHomeViewModel.NO_PROGRESS
        val arrayItem = resources.getStringArray(R.array.item)
        val index = arrayItem.indexOf(item)
        val kelompok = resources.getStringArray(R.array.kelompok)[index]

        if (lokasi.isNotEmpty() || item.isNotEmpty() || tipe.isNotEmpty() || kelompok.isNotEmpty()) {
            val laporanBase = LaporanBase(lokasi, email, tanggal, true)
            val laporanRuangan = LaporanRuangan(
                item,
                item,
                email,
                lokasi,
                tanggal,
                tipe,
                dokumentasi.size,
                keterangan,
                status,
                0,
                kelompok,
                lantai,
                isChecked = true
            )

            viewModel.putFormPelaporan(laporanBase, laporanRuangan, dokumentasi)
        }
        findNavController().navigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        viewModel.clearImagesUri()
//        if (!requireActivity().isChangingConfigurations) {
//            deleteTempFiles(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES))
//        }
    }

    private fun deleteTempFiles(externalFilesDir: File?) {
        if (externalFilesDir == null)
            return

        try {
            val files = externalFilesDir.listFiles()
            for (file in files) {
                if (file.isDirectory) {
                    deleteTempFiles(file)
                } else {
                    file.delete()
                }
            }
        } catch (exception: FileSystemException) {
            Log.e(TAG, "An error occured when attempting to delete TempFiles", exception)
        }

        externalFilesDir.delete()
    }

    companion object {
        private const val PICK_IMAGE_CODE = 200
        private const val CAPTURE_IMAGE_CODE = 204
        private const val REQ_CAMERA_PERMISSION = 122
        private const val TAG = "FormPelaporanFragment"
    }
}