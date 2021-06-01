package com.machina.siband.user.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.machina.siband.R
import com.machina.siband.databinding.FragmentUserHomeBinding
import com.machina.siband.recycler.ListRuanganAdapter
import com.machina.siband.user.viewModel.UserHomeViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [UserHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserHomeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentUserHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserHomeViewModel by activityViewModels()
    private lateinit var mAdapter: ListRuanganAdapter
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserHomeBinding.inflate(layoutInflater, container, false)

        spinner = binding.fragmentUserHomeSpinner
        spinner.onItemSelectedListener = this

        setupRecycler()
        setupObserver()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun setupRecycler() {
        val mLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mAdapter = ListRuanganAdapter(this::onItemRuanganClicked)

        binding.fragmentUserHomeRecycler.apply {
            adapter = mAdapter
            layoutManager = mLayoutManager
        }
    }

    private fun setupObserver() {
        // Listening to listLantai change
//        viewModel.listLantai.observe(viewLifecycleOwner, { listLantai ->
//
//
//            for (lantai in listLantai) {
//                Log.d(TAG, "nama: ${lantai.nama} | url: ${lantai.urlMap}")
//                for (ruangan in lantai.listRuangan)
//                    Log.d(TAG, "ruangan: $ruangan")
//            }
//        })

        // Listening to selectedLantai, update adapter dataset when selectedLantai changed
        viewModel.selectedLantai.observe(viewLifecycleOwner, { selectedLantai ->
            mAdapter.setData(selectedLantai.listRuangan)
        })

        // Listening to arrayLantai, update Data for spinner on change
        viewModel.arrayListLantai.observe(viewLifecycleOwner, { arrayListLantai ->
            val mSpinnerAdapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, arrayListLantai)
            spinner.apply {
                adapter = mSpinnerAdapter
                setSelection(viewModel.selectedPosition)
            }
        })
    }

    private fun onItemRuanganClicked(name: String) {
        val id = viewModel.selectedLantai.value?.id
        if (!id.isNullOrEmpty()) {
            val action = UserHomeFragmentDirections.actionUserHomeFragmentToDetailRuanganFragment(id, name)
            findNavController().navigate(action)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val newLantai = viewModel.listLantai.value?.get(position)
        if (newLantai != null) {
            viewModel.setSelectedLantai(newLantai, position)
        } else {
            Toast.makeText(context, "An error occured please try again", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }



    companion object {
        private val TAG = "UserHomeFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserHomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            UserHomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

}