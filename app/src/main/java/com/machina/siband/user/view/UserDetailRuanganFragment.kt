package com.machina.siband.user.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.databinding.FragmentUserDetailRuanganBinding
import com.machina.siband.recycler.ListLaporanRuanganAdapter
import com.machina.siband.user.model.LaporanRuangan
import com.machina.siband.user.viewModel.UserHomeViewModel
import java.util.*

class UserDetailRuanganFragment : Fragment() {

    private var _binding: FragmentUserDetailRuanganBinding? = null
    private val binding get() = _binding!!

    private val args: UserDetailRuanganFragmentArgs by navArgs()
    private val viewModel: UserHomeViewModel by activityViewModels()

    private lateinit var mAdapter: ListLaporanRuanganAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserDetailRuanganBinding.inflate(inflater, container, false)

        setupRecycler()
        setupObserver()

        binding.fragmentDetailRuanganSubmit.setOnClickListener {
            /*
            //    IMPORTANT!!!!
            //    CHANGE ON PRODUCTION
            */
            val email = "admin@gmail.com"
            val tanggal = "29-04-2021"
            val lokasi = args.lokasi

            viewModel.putLaporanLantai(email, tanggal, lokasi)
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun postSomething() {
        val cal = Calendar.getInstance()
        Log.d(TAG, "curr date: ${cal.get(Calendar.DATE)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.YEAR)}")
    }

    private fun onItemLaporanClicked(data: LaporanRuangan) {
        val action = UserDetailRuanganFragmentDirections
            .actionDetailRuanganFragmentToLaporanFragment(data)
        findNavController().navigate(action)
    }

    // SetUp observer for liveData in this fragment
    private fun setupObserver() {
        viewModel.listLaporanRuangan.observe(viewLifecycleOwner, { dataSet ->
            Log.d(TAG, "Adapter dataSet updated")
            mAdapter.setData(dataSet)
        })
    }


    private fun setupRecycler() {
        val mLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mAdapter = ListLaporanRuanganAdapter(this::onItemLaporanClicked)

        binding.fragmentDetailRuanganRecycler.apply {
            adapter = mAdapter
            layoutManager = mLayoutManager
        }

        // On Production change this param into dynamic
        val idLantai = args.idLantai
        val email = "admin@gmail.com"
        val tanggal = "29-04-2021"
        val namaRuangan = args.lokasi

        if (viewModel.listLaporanRuangan.value.isNullOrEmpty())
            viewModel.getListLaporanRuangan(idLantai, email, tanggal, namaRuangan)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearLaporanRuangan()
        _binding = null
    }

    companion object {
        private const val TAG = "DetailRuanganFragment"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailRuanganFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                UserDetailRuanganFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}