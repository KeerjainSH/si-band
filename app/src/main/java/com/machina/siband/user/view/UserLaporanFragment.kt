package com.machina.siband.user.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.databinding.FragmentUserLaporanBinding
import com.machina.siband.recycler.ListComplaintAdapter
import com.machina.siband.user.model.LaporanRuangan
import com.machina.siband.user.viewModel.UserHomeViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [UserLaporanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserLaporanFragment : Fragment() {

    private var _binding: FragmentUserLaporanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserHomeViewModel by activityViewModels()

    private lateinit var mAdapter: ListComplaintAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserLaporanBinding.inflate(layoutInflater, container, false)

        setupRecycler()
        setupObserver()

        return binding.root
    }

    private fun setupObserver() {
        viewModel.listLaporanRuangan.observe(viewLifecycleOwner, {
            mAdapter.setData(it)
        })
    }

    private fun setupRecycler() {
        val recyclerView = binding.fragmentUserLaporanRecycler
        mAdapter = ListComplaintAdapter(this::onItemLaporanClick)
        val mLinearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.apply {
            adapter = mAdapter
            layoutManager = mLinearLayoutManager
        }
        val email = "admin@gmail.com"
        viewModel.getListLaporanBase(email)
    }

    private fun onItemLaporanClick(data: LaporanRuangan) {
        val action = UserLaporanFragmentDirections.actionUserLaporanFragmentToUserReviewLaporanFragment(data)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearLaporanRuangan()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserComplaintFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserLaporanFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}