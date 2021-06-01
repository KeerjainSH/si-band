package com.machina.siband.user.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.machina.siband.R
import com.machina.siband.databinding.FragmentUserReviewLaporanBinding

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

        if (mLaporanRuangan != null) {
            binding.fragmentUserReviewLaporanLokasi.text = mLaporanRuangan.lokasi
            binding.fragmentUserReviewLaporanItem.text = mLaporanRuangan.nama
            binding.fragmentUserReviewLaporanTanggal.text = mLaporanRuangan.tanggal
            binding.fragmentUserReviewLaporanTipe.editText?.setText(mLaporanRuangan.tipe)
            binding.fragmentUserReviewLaporanKeterangan.editText?.setText(mLaporanRuangan.keterangan)
            binding.fragmentUserReviewLaporanStatus.editText?.setText(mLaporanRuangan.status)
        }
    }

    companion object {
        private const val TAG = "userReviewLaporanFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserReviewLaporanFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserReviewLaporanFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}