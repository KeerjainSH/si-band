package com.machina.siband.user.view

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
import com.bumptech.glide.Glide
import com.machina.siband.R
import com.machina.siband.databinding.FragmentUserFormLaporanBinding
import com.machina.siband.module.GlideApp
import com.machina.siband.user.model.LaporanRuangan
import com.machina.siband.user.repository.UserFirebaseStorageRepo
import com.machina.siband.user.viewModel.UserHomeViewModel
import kotlinx.coroutines.coroutineScope

/**
 * A simple [Fragment] subclass.
 * Use the [UserFormLaporanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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


        binding.fragmentLaporanDokumentasiIcon.setOnClickListener {
            selectImage()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        resolveForm()

        val tipeKerusakan = resources.getStringArray(R.array.tipe)

        val mArrayAdapter = ArrayAdapter(requireContext(), R.layout.item_list_dropdown, tipeKerusakan)
        (binding.fragmentLaporanTipe.editText as? AutoCompleteTextView)?.setAdapter(mArrayAdapter)

        binding.fragmentLaporanSubmit.setOnClickListener {
            onSubmitLaporan()
        }
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
            UserFormPelaporanFragment.PICK_IMAGE_CODE
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UserFormPelaporanFragment.PICK_IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val mLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.setMargins(0, 20, 0, 20) }


            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    loadImageLocally(imageUri, mLayoutParams)
                    viewModel.imagesUri.add(imageUri)

                    Log.d(TAG, "$imageUri")
                }
            } else if (data.data != null){
                val imageUri = data.data
                if (imageUri != null) {
                    loadImageLocally(imageUri, mLayoutParams)
                    viewModel.imagesUri.add(imageUri)
                }
            }
            binding.fragmentLaporanDokumentasiIconContainer.visibility = View.GONE
        }
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

        val email = "admin@gmail.com"
        val tanggal = "29-04-2021"
        val imageRef = UserFirebaseStorageRepo.getLaporanImageRef(email, tanggal, lokasi, "${nama}$index")

        context?.let {
            GlideApp.with(it)
                .load(imageRef)
                .into(imageView)
        }
        binding.fragmentLaporanDokumentasiContainer.addView(imageView)
    }


    private fun onSubmitLaporan() {
        val laporanRuangan = args.laporanRuangan
        if (laporanRuangan != null) {
            val nama = laporanRuangan.nama
            val newTipe = binding.fragmentLaporanTipe.editText?.text.toString()
            val images = viewModel.imagesUri.toList()
            val newKeterangan = binding.fragmentLaporanKeterangan.editText?.text.toString()

            val newLaporanRuangan = laporanRuangan.copy(tipe = newTipe, keterangan =  newKeterangan, dokumentasi = images.size)

            viewModel.putNewLaporanRuangan(newLaporanRuangan, images)
//            viewModel.applyLocalChangeLaporan(nama, newTipe, newKeterangan)
//            viewModel.putNewImage(laporanRuangan, newDokumentasi)
        }
        findNavController().navigateUp()
    }


    // Fill the form if the selected item is already submitted within the same day
    private fun resolveForm() {
        val lokasi = args.laporanRuangan?.lokasi.toString()
        val nama = args.laporanRuangan?.nama.toString()
        val tipeKerusakan = args.laporanRuangan?.tipe.toString()
        val dokumentasi = args.laporanRuangan?.dokumentasi as Int
        val keterangan = args.laporanRuangan?.keterangan.toString()

        binding.fragmentLaporanLokasi.text = lokasi
        binding.fragmentLaporanTipe.editText?.setText(tipeKerusakan)
        binding.fragmentLaporanKeterangan.editText?.setText(keterangan)

        val mLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).also { it.setMargins(0, 20, 0, 20) }

        if (dokumentasi > 0) {
            repeat(dokumentasi) {
                loadImageInternet(lokasi, nama,  mLayoutParams, it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        viewModel.imagesUri.clear()
    }

    companion object {

        const val WITH_DATA = "withData"
        const val WITHOUT_DATA = "withoutData"
        private const val TAG = "userFormLaporanFragment"
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
            UserFormLaporanFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}