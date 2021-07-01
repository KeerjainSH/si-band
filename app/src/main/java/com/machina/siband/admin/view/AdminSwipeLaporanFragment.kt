package com.machina.siband.admin.view

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.machina.siband.R
import com.machina.siband.admin.dialog.DialogDatePicker
import com.machina.siband.admin.recycler.AdminSwipeViewAdapter
import com.machina.siband.admin.viewmodel.AdminViewModel
import com.machina.siband.databinding.FragmentAdminSwipeLaporanBinding
import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class AdminSwipeLaporanFragment: Fragment() {
    private lateinit var mSwipeViewAdapter: AdminSwipeViewAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private var _binding: FragmentAdminSwipeLaporanBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminViewModel by activityViewModels()

    private val tabTitleList = listOf("No Progress Yet", "On Progress", "Done")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminSwipeLaporanBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        mSwipeViewAdapter = AdminSwipeViewAdapter(this)
        viewPager = binding.fragmentAdminSwipeLaporanPager
        viewPager.adapter = mSwipeViewAdapter
        tabLayout = binding.fragmentAdminSwipeLaporanTab

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitleList[position]
        }.attach()

        viewModel.getListLaporanBase()
    }

    private fun createCsvFile() {
        val csv = File(context?.getExternalFilesDir(null), "report.csv") // Here csv file name is MyCsvFile.csv
        Log.d(TAG, "csv [${csv.absolutePath}] nama [${csv.name}]")

        try {
            val writer = CSVWriter(FileWriter(csv))
            val data: MutableList<Array<String>> = ArrayList()
            data.add(arrayOf("Country", "Capital", ""))
            data.add(arrayOf("India", "New Delhi", ""))
            data.add(arrayOf("United States", "Washington D.C", "ini kosong"))
            data.add(arrayOf("Germany", "Berlin", "ini kosong juga"))
            Log.d(TAG, "writer [$writer]")
            writer.writeAll(data) // data is adding to csv
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun onDialogDateSet(calendar: Calendar) {
        Log.d(TAG, "Current date ${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.admin_list_laporan_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.adminListLaporanMenuExcel -> {
                val datePicker = DialogDatePicker(this::onDialogDateSet)
                datePicker.show(parentFragmentManager, "datePicker")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "AdminSwipe"
    }
}