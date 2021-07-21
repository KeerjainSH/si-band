package com.machina.siband.admin.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.machina.siband.R
import com.machina.siband.admin.recycler.AdminListUserAdapter
import com.machina.siband.admin.viewmodel.AdminViewModel
import com.machina.siband.databinding.FragmentAdminListUserBinding

/**
 * A simple [Fragment] subclass.
 * Use the [AdminListUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminListUserFragment : Fragment() {

  private var _binding: FragmentAdminListUserBinding? = null
  private val binding get() = _binding!!

  private val viewModel: AdminViewModel by activityViewModels()
  private lateinit var mAdapter: AdminListUserAdapter

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = FragmentAdminListUserBinding.inflate(inflater)

    setupRecycler()
    setupObserver()

    return binding.root
  }

  private fun setupObserver() {
    viewModel.listAccount.observe(viewLifecycleOwner, {
      mAdapter.setData(it)
    })
  }

  private fun setupRecycler() {
    mAdapter = AdminListUserAdapter()
    val mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    val recycler = binding.fragmentAdminListUserRecycler
    viewModel.getListAccount()

    recycler.apply {
      adapter = mAdapter
      layoutManager = mLayoutManager
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }

  companion object {
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminListUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    @JvmStatic
    fun newInstance(param1: String, param2: String) =
      AdminListUserFragment().apply {
      }
  }
}