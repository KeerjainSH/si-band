package com.machina.siband.user.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.StorageReference
import com.machina.siband.databinding.FragmentUserReviewLaporanBinding
import com.machina.siband.module.GlideApp
import com.machina.siband.repository.FirebaseStorageRepo
import com.machina.siband.user.viewModel.UserHomeViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [UserReviewLaporanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserReviewLaporanFragment : Fragment() {

    private var _binding : FragmentUserReviewLaporanBinding? = null
    private val binding get() = _binding!!

    private val args: UserReviewLaporanFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserReviewLaporanBinding.inflate(inflater)

        resolveForm()

        return binding.root
    }

    private fun resolveForm() {
        val mLaporanRuangan = args.laporanRuangan
        val mLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            800
        ).also { it.setMargins(0, 20, 0, 20) }
        val lokasi = mLaporanRuangan.lokasi
        val email = mLaporanRuangan.email
        val nama = mLaporanRuangan.nama
        val tanggal = mLaporanRuangan.tanggal
        val tipe = mLaporanRuangan.tipe
        val status = mLaporanRuangan.status
        val keterangan = mLaporanRuangan.keterangan
        val dok = mLaporanRuangan.dokumentasi
        val dokPerbaikan = mLaporanRuangan.dokumentasiPerbaikan

        binding.fragmentUserReviewLaporanLokasi.text = lokasi
        binding.fragmentUserReviewLaporanItem.text = nama
        binding.fragmentUserReviewLaporanTanggal.text = tanggal
        binding.fragmentUserReviewLaporanTipe.editText?.setText(tipe)
        binding.fragmentUserReviewLaporanKeterangan.editText?.setText(keterangan)
        binding.fragmentUserReviewLaporanStatus.editText?.setText(status)


        if (dok > 0) {
            binding.fragmentUserReviewLaporanDokumentasiIcon.visibility = View.GONE
            repeat(dok) {
                val storageRef = FirebaseStorageRepo.getLaporanImageRef(email, tanggal, lokasi, "${nama}$it")
                loadImageInternet(mLayoutParams, binding.fragmentUserReviewLaporanDokumentasiContainer, storageRef)
            }
        }

        if (dokPerbaikan > 0) {
            binding.fragmentUserReviewLaporanDokumentasiPerbaikanIcon.visibility = View.GONE
            repeat(dok) {
                val storageRef = FirebaseStorageRepo.getLaporanPerbaikanImageRef(email, tanggal, lokasi, "${nama}$it")
                loadImageInternet(mLayoutParams, binding.fragmentUserReviewLaporanDokumentasiPerbaikanContainer, storageRef)
            }
        }
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

    companion object {
        private const val TAG = "userReviewLaporanFragment"
    }
}