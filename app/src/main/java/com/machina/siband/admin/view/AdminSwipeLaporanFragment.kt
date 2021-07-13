package com.machina.siband.admin.view

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
import com.machina.siband.model.LaporanRuangan
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

        viewModel.getListLantai()
        viewModel.getListAreaRuangan()
        viewModel.getListLaporanBase()
        viewModel.getListAreaRuangan()
    }

    private fun onDialogDateSet(calendar: Calendar) {
        val tanggal = "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"
        createCsvFile(tanggal)
    }

    private fun createCsvFile(tanggal: String) {
        val urutArea = listOf("A.", "B.", "C.", "D.", "E.", "F.", "G.", "H.", "I.", "J.", "K.", "L.", "M.", "N.", "O.", "P.", "Q.", "R.", "S.", "T.", "U.", "V.", "W.", "X.", "Y.", "Z.",)
        val filteredLaporan = viewModel.getListLaporanByDate(tanggal)
        val listLantai = viewModel.listLantai.value
        val listArea = viewModel.listAreaRuangan.value
        val listItem = resources.getStringArray(R.array.item)
        val csv = File(context?.getExternalFilesDir(null), "${tanggal}.csv") // Here csv file name is MyCsvFile.csv
        Log.d(TAG, "csv [${csv.absolutePath}] nama [${csv.name}]")

        if (filteredLaporan.isEmpty()) {
            Toast.makeText(requireContext(), "Tidak ada laporan pada $tanggal", Toast.LENGTH_SHORT).show()
            return
        }

        if (listLantai.isNullOrEmpty() && listArea.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "An error occured please try again in a moment", Toast.LENGTH_SHORT).show()
            return
        }

        // Group laporan by its lantai then area ruangan then the ruangan itself
        val allLaporanByLantai = mutableListOf<MutableList<MutableList<LaporanRuangan>>>()

        var laporanByArea = mutableListOf<MutableList<LaporanRuangan>>()
        var mutableTempLaporan: MutableList<LaporanRuangan>
        var temp: MutableList<LaporanRuangan>
        for (lantai in listLantai!!) {
            mutableTempLaporan = filteredLaporan.filter {
                it.lantai == lantai.nama
            }.toMutableList()
            for (area in listArea!!) {
                temp = mutableListOf()
                for (laporan in mutableTempLaporan) {
                    if (laporan.area == area.nama) {
                        temp.add(laporan)
                    }
                }
                laporanByArea.add(temp)
            }
            allLaporanByLantai.add(laporanByArea)
            laporanByArea = mutableListOf(mutableListOf())
        }

        for (lantai in allLaporanByLantai) {
            for (laporanArea in lantai) {
                for (laporan in laporanArea) {
                    Log.d(TAG, "lantai [${laporan.lantai}] area [${laporan.area}] ruangan [${laporan.lokasi}] item [${laporan.nama}]")
                }
            }
        }
        val brokenLaporan = filteredLaporan.filter { it.tipe.isNotBlank() }

        try {
            val writer = CSVWriter(FileWriter(csv))
            val data: MutableList<Array<String>> = ArrayList()
            var counterArea = 0
            var counterRuangan = 1
            var flagLantai = 1
            var flagArea = 1
            var flagRuangan = 1
            var tempString: String
            var currentRuangan = ""
            for (lantai in allLaporanByLantai) {
                var laporanMutable = MutableList(19) { "" }
                for (laporanArea in lantai) {
                    laporanArea.forEachIndexed { index, laporan ->
                        if (flagRuangan == 1) {
                            currentRuangan = laporan.lokasi
                        }
                        tempString = laporan.lokasi

                        if (tempString != currentRuangan) {
                            flagRuangan = 1
                            currentRuangan = laporan.lokasi
                            counterRuangan++
                            Log.d(TAG, "write ruangan ke csv ${laporanMutable}")
                            data.add(laporanMutable.toTypedArray())
                            laporanMutable = MutableList(19) { "" }
                        }

                        var tempMutable = MutableList(19) { "" }

                        if (flagLantai == 1) {
                            flagLantai = 0
                            data.add(arrayOf(""))
                            data.add(arrayOf(""))
                            data.add(arrayOf("${laporan.lantai}"))
                            tempMutable[2] = "FASILITAS"
                            data.add(tempMutable.toTypedArray())
                            tempMutable = MutableList(19) { "" }
                            tempMutable[0] = "NO."
                            tempMutable[1] = "RUANGAN"
                            tempMutable[2] = "Struktur"
                            tempMutable[6] = "Arsitektur"
                            tempMutable[12] = "Mekanikal (Sanitasi)"
                            data.add(tempMutable.toTypedArray())
                            tempMutable = MutableList(19) { "" }
                            listItem.forEachIndexed { index, s ->
                                tempMutable[index + 2] = s
                            }
                            data.add(tempMutable.toTypedArray())
                        }

                        if (flagArea == 1) {
                            flagArea = 0
                            tempMutable = MutableList(19) { "" }
                            tempMutable[0] = urutArea[counterArea]
                            tempMutable[1] = laporan.area
                            data.add(tempMutable.toTypedArray())
                            counterArea++
                        }

                        if (flagRuangan == 1) {
                            Log.d(TAG, "${laporan.area} ${laporan.lokasi}")
                            flagRuangan = 0
                            laporanMutable[0] = "$counterRuangan."
                            laporanMutable[1] = laporan.lokasi
                        }

                        if (laporan.tipe.isEmpty()) {
                            val index = listItem.indexOf(laporan.nama) + 2
                            laporanMutable[index] = "x"
                        } else {
                            Log.d(TAG, "ruangan ${laporan.lokasi} item ${laporan.nama} rusak")
                        }
                        Log.d(TAG, "$laporanMutable")
//                        Log.d(TAG, "lantai [${laporan.lantai}] area [${laporan.area}] ruangan [${laporan.lokasi}] item [${laporan.nama}]")
                    }
                    if (flagRuangan == 0) {
                        flagRuangan = 1
                        counterRuangan++
                        Log.d(TAG, "write ruangan ke csv ${laporanMutable}")
                        data.add(laporanMutable.toTypedArray())
                        laporanMutable = MutableList(19) { "" }
                    }
                    flagArea = 1
                    counterRuangan = 1
                }
                counterArea = 0
                flagLantai = 1
            }

            data.add(arrayOf(""))
            data.add(arrayOf(""))
            data.add(arrayOf("Kerusakan: "))
            data.add(arrayOf("No.", "Lokasi", "Tipe Kerusakan", "Kelompok", "Item/Fasilitas", "Dokumentasi Sebelum Perbaikan", "Dokumentasi Seletah Perbaikan", "Keterangan"))

            brokenLaporan.forEachIndexed { index, item ->
                val tempInternal = MutableList(8) { "" }
                tempInternal[0] = "${index + 1}"
                tempInternal[1] = item.lokasi
                tempInternal[2] = item.tipe
                tempInternal[3] = item.kelompok
                tempInternal[4] = item.nama
                tempInternal[7] = item.keterangan
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
                            tempInternal[5] = dokText
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
                            tempInternal[6] = dokText
                        }
                    }
                }
                Log.d(TAG, "dok ${tempInternal[3]}")
                Log.d(TAG, "perbaikan ${tempInternal[4]}")
                data.add(tempInternal.toTypedArray())
            }

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