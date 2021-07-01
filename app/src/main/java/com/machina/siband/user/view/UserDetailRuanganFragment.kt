package com.machina.siband.user.view

import android.os.Bundle
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
import com.machina.siband.user.recycler.ListLaporanRuanganAdapter
import com.machina.siband.model.LaporanRuangan
import com.machina.siband.user.viewModel.UserHomeViewModel

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

        setupObserver()
        setupRecycler()

        binding.fragmentDetailRuanganSubmit.setOnClickListener {
            /*
            //    IMPORTANT!!!!
            //    CHANGE ON PRODUCTION
            */
            val email =  viewModel.getCurrentEmail()
            val tanggal = viewModel.getCurrentDate()
            val lokasi = args.lokasi

            viewModel.putLaporanLantai(email, tanggal, lokasi)
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun onItemLaporanClicked(data: LaporanRuangan) {
        val action = UserDetailRuanganFragmentDirections
            .actionDetailRuanganFragmentToLaporanFragment(data)

        findNavController().navigate(action)
    }

    private fun onCheckBoxClicked(data: LaporanRuangan) {
        viewModel.putLaporanRuanganOnCheck(data, args.idLantai)
    }

    // SetUp observer for liveData in this fragment
    private fun setupObserver() {
        viewModel.listLaporanRuangan.observe(viewLifecycleOwner, {
            mAdapter.setData(it)
        })
    }


    private fun setupRecycler() {
        val mLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mAdapter = ListLaporanRuanganAdapter(this::onItemLaporanClicked, this::onCheckBoxClicked)

        binding.fragmentDetailRuanganRecycler.apply {
            adapter = mAdapter
            layoutManager = mLayoutManager
        }

        // On Production change this param into dynamic
        val idLantai = args.idLantai
        val email = viewModel.getCurrentEmail()
        val tanggal = viewModel.getCurrentDate()
        val lokasi = args.lokasi

        viewModel.getListLaporanRuangan(idLantai, email, tanggal, lokasi)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearLaporanRuangan()
        _binding = null
    }

    companion object {
        private const val TAG = "DetailRuanganFragment"
    }
}