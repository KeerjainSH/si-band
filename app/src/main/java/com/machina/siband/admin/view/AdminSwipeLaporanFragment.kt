package com.machina.siband.admin.view

import android.icu.text.UnicodeSet
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
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
import com.machina.siband.repository.FirebaseStorageRepo
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
        viewModel.getListAreaRuangan()
    }

    private fun onDialogDateSet(calendar: Calendar) {
        val tanggal = "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"
        createCsvFile(tanggal)
    }

    private fun createCsvFile(tanggal: String) {
        val filteredLaporan = viewModel.getListLaporanByDate(tanggal)
        val listNamaRuangan = viewModel.getListNamaRuangan()
        val csv = File(context?.getExternalFilesDir(null), "${tanggal}.csv") // Here csv file name is MyCsvFile.csv
        Log.d(TAG, "csv [${csv.absolutePath}] nama [${csv.name}]")

        if (filteredLaporan.isEmpty() || listNamaRuangan.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Tidak ada laporan pada $tanggal", Toast.LENGTH_SHORT).show()
            return
        }

        val safeLaporan = filteredLaporan.filter { it.tipe.isEmpty() }
        val brokenLaporan = filteredLaporan.filter { it.tipe.isNotBlank() }

        val laporanStruktur = safeLaporan.filter { it.kelompok == "Struktur" }
        val laporanArsitektur = safeLaporan.filter { it.kelompok == "Arsitektur" }
        val laporanMekanikal = safeLaporan.filter { it.kelompok == "Mekanikal" }

        val namaLaporanStruktur = mutableSetOf<String>()
        for (item in laporanStruktur) namaLaporanStruktur.add(item.nama)
        val namaLaporanArsitektur = mutableSetOf<String>()
        for (item in laporanArsitektur) namaLaporanArsitektur.add(item.nama)
        val namaLaporanMekanikal = mutableSetOf<String>()
        for (item in laporanMekanikal) namaLaporanMekanikal.add(item.nama)

        try {
            val writer = CSVWriter(FileWriter(csv))
            val data: MutableList<Array<String>> = ArrayList()
            var pos: Int
            var temp = mutableListOf("No.", "Fasilitas")
            temp.addAll(listNamaRuangan)
            data.add(temp.toTypedArray())
            data.add(arrayOf("1.", "STRUKTUR"))
            for (nama in namaLaporanStruktur) {
                temp = MutableList(listNamaRuangan.size + 2) { "" }
                temp.add(1, nama)
                for (item in laporanStruktur) {
                    if (nama == item.nama) {
                        pos = listNamaRuangan.indexOf(item.lokasi)
                        if (pos != -1) {
                            temp[pos + 2] = "x"
                        }
                    }
                }
                data.add(temp.toTypedArray())
            }

            data.add(arrayOf("2.", "ARSITEKTUR"))
            for (nama in namaLaporanArsitektur) {
                temp = MutableList(listNamaRuangan.size + 2) { "" }
                temp.add(1, nama)
                for (item in laporanArsitektur) {
                    if (nama == item.nama) {
                        pos = listNamaRuangan.indexOf(item.lokasi)
                        if (pos != -1) {
                            temp[pos + 2] = "x"
                        }
                    }
                }
                data.add(temp.toTypedArray())
            }

            data.add(arrayOf("3.", "MEKANIKAL"))
            for (nama in namaLaporanMekanikal) {
                temp = MutableList(listNamaRuangan.size + 2) { "" }
                temp.add(1, nama)
                for (item in laporanMekanikal) {
                    if (nama == item.nama) {
                        pos = listNamaRuangan.indexOf(item.lokasi)
                        if (pos != -1) {
                            temp[pos + 2] = "x"
                        }
                    }
                }
                data.add(temp.toTypedArray())
            }

            data.add(arrayOf(""))
            data.add(arrayOf(""))
            data.add(arrayOf("Kerusakan: "))
            data.add(arrayOf("No.", "Lokasi", "Tipe Kerusakan", "Dokumentasi Kerusakan Sebelum Perbaikan", "Dokumentasi Seletah Perbaikan", "Keterangan"))

            brokenLaporan.forEachIndexed { index, item ->
                val tempInternal = MutableList(8) { "" }
                tempInternal[0] = "${index + 1}"
                tempInternal[1] = item.lokasi
                tempInternal[2] = item.tipe
                tempInternal[5] = item.keterangan
                val dok = item.dokumentasi
                val dokPerbaikan = item.dokumentasiPerbaikan

                if (dok > 0) {
                    var dokText = ""
                    repeat(dok) {
                        val ref = FirebaseStorageRepo.getLaporanImageRef(item.email, tanggal, item.lokasi, "${item.nama}$it")
                            .downloadUrl
                        while (!ref.isComplete) {
                            Thread.sleep(100)
                        }
                        if (ref.isSuccessful) {
                            dokText = "$dokText \n${ref.result}"
                            tempInternal[3] = dokText
                        }
                    }
                }
                if (dokPerbaikan > 0) {
                    var dokText = ""
                    repeat(dok) {
                        val ref = FirebaseStorageRepo.getLaporanPerbaikanImageRef(item.email, tanggal, item.lokasi, "${item.nama}$it")
                            .downloadUrl
                        while (!ref.isComplete) {
                            Thread.sleep(100)
                        }
                        if (ref.isSuccessful) {
                            dokText = "$dokText \n${ref.result}"
                            tempInternal[4] = dokText
                        }
                    }
                }
                Log.d(TAG, "dok ${tempInternal[3]}")
                Log.d(TAG, "perbaikan ${tempInternal[4]}")
                data.add(tempInternal.toTypedArray())
            }

//            data.add(temp.toTypedArray())
            writer.writeAll(data) // data is adding to csv
            writer.close()
            Toast.makeText(requireContext(), "Rekap Laporan Berhasil Dibuat\n${csv.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
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