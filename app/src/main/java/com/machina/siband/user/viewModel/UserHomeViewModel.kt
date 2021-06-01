package com.machina.siband.user.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.machina.siband.user.model.Lantai
import com.machina.siband.user.model.Lantai.Companion.toLantai
import com.machina.siband.user.model.LaporanBase
import com.machina.siband.user.model.LaporanBase.Companion.toLaporanBase
import com.machina.siband.user.model.LaporanRuangan
import com.machina.siband.user.model.LaporanRuangan.Companion.toLaporanRuangan
import com.machina.siband.user.model.Ruangan
import com.machina.siband.user.repository.UserFirestoreRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UserHomeViewModel: ViewModel() {

    // List of Lantai Object
    private val _listLantai = MutableLiveData<List<Lantai>>()
    val listLantai: LiveData<List<Lantai>> = _listLantai

    // Array of nama of each Lantai object, used to populate spinner
    private val _arrayListLantai = MutableLiveData<Array<String>>()
    var arrayListLantai: LiveData<Array<String>> = _arrayListLantai

    // Value of selectedLantai Object, used to populate recyclerAdapter
    private val _selectedLantai = MutableLiveData<Lantai>()
    val selectedLantai: LiveData<Lantai> = _selectedLantai

    // Value of selected Lantai position to keep spinner consistent
    private var _selectedPosition: Int = 0
    val selectedPosition get() = _selectedPosition

    // List of possible report of a ruangan
    private var _listLaporanRuangan = MutableLiveData<List<LaporanRuangan>>()
    val listLaporanRuangan: LiveData<List<LaporanRuangan>> = _listLaporanRuangan

    // List of submitted LaporanBase
    private var _listLaporanBase = MutableLiveData<List<LaporanBase>>()
    val listLaporanBase: LiveData<List<LaporanBase>> = _listLaporanBase

    // All listener to a firestore
    private lateinit var lantaiListener: ListenerRegistration

    init {
        attachLantaiListener()
    }

    private fun fetchListLaporanRuangan() {
        val tempMutable = mutableListOf<LaporanRuangan>()
        val laporanBase = listLaporanBase.value
        if (laporanBase.isNullOrEmpty()) return

        // Call data for each item in
        for (item in laporanBase) {
            UserFirestoreRepo.getListLaporanRuanganRef(item.email, item.tanggal, item.lokasi).get()
                .addOnSuccessListener { coll ->
                    var temp = coll.mapNotNull { it.toLaporanRuangan() }
                    temp = temp.filter {
                        return@filter it.tipe.isNotEmpty()
                    }
                    tempMutable.addAll(temp)
                    _listLaporanRuangan.value = tempMutable
                }
                .addOnFailureListener { error ->
                    sendCrashlytics("Failed to fetch LaporanRuangan", error)
                }
        }

        viewModelScope.launch(Dispatchers.Default) {
            for (item in tempMutable) {
                Log.d(TAG, "item : ${item.nama}, tanggal : ${item.tanggal}")
            }
        }

    }


    fun getListLaporanBase(email: String) {
        UserFirestoreRepo.getUserListLaporanRuanganRef(email)
            .get()
            .addOnSuccessListener { snapShot ->
                val tempList = snapShot.documents.mapNotNull { it.toLaporanBase() }
                _listLaporanBase.value = tempList
                fetchListLaporanRuangan()
            }
            .addOnFailureListener {
                sendCrashlytics("Error when fetching ListLaporanBase", it)
            }
    }


    /**
     * Put the local change that has been made
     * to listKeluhanRuangan to the database
     *
     * @param email
     * @param tanggal date that this function is called
     * @param namaRuangan
     */
    fun putUpdatedListLaporanRuangan(email: String, tanggal: String, namaRuangan: String) {
        val listRef = mutableListOf<DocumentReference>()
        val listTemp = listLaporanRuangan.value
        val baseLaporanRef = UserFirestoreRepo.getLaporanBaseRef(email, tanggal, namaRuangan)

        baseLaporanRef.update("isSubmitted", true)

        if (listTemp != null) {
            for (item in listTemp) {
                val tempRef = UserFirestoreRepo.getLaporanRuanganRef(email, tanggal, namaRuangan, item.nama)
                listRef.add(tempRef)
            }

            val db = Firebase.firestore
            db.runBatch { batch ->
                for ((counter, item) in listRef.withIndex()) {
                    batch.set(item, listTemp[counter])
                }
            }
            .addOnFailureListener { e ->
                sendCrashlytics("An error occurred while batch write", e)
            }
        }
    }


    /**
     * Aplly change on Invididual Laporan Form
     * to a Local Storage before submitted
     *
     * @param nama name of the item in DetailRuangan
     * @param newTipe value of new Tipe (Sedang, Darurat)
     * @param newKeterangan value of new Keterangan
     */
    fun applyLocalChangeLaporan(nama: String, newTipe: String, newKeterangan: String) {
        val newList = _listLaporanRuangan.value?.map {
            if (it.nama == nama) {
                Log.d(TAG, "$newTipe $newKeterangan")
                return@map it.copy(tipe = newTipe, keterangan = newKeterangan, isChecked = true)
            } else
                return@map it.copy()
        }
        if (newList != null)
            _listLaporanRuangan.value = newList as List<LaporanRuangan>
    }


    // Convert param into mutableList
    private fun convertToMutableList(arrayTemp: ArrayList<*>): MutableList<Ruangan> {
        val listTemp = mutableListOf<Ruangan>()
        if (arrayTemp[0] != null && arrayTemp[0] is String) {
            for (item in arrayTemp) {
                val ruangan = Ruangan(item as String, false)
                listTemp.add(ruangan)
            }
        }
        return listTemp
    }

    // this function only called one time in one day, when user open ListLaporan for the first time
    // Generate dummy laporan in appropriate path
    private fun setListLaporanRuangan(idLantai: String, email: String, tanggal: String, namaRuangan: String) {
        val laporanBaseRef = UserFirestoreRepo.getLaporanBaseRef(email, tanggal, namaRuangan)
        val laporanBase = LaporanBase(namaRuangan, email, tanggal, "On Process",false)
        val listDetailRuanganRef = UserFirestoreRepo.getListDetailRuanganRef(idLantai, namaRuangan)

        laporanBaseRef.set(laporanBase)
                .addOnSuccessListener {
                    listDetailRuanganRef.get()
                            .addOnSuccessListener { docs ->
                                val arrayTemp = docs.get("list-keluhan")
                                if (arrayTemp != null) {
                                    val listTemp = convertToMutableList(arrayTemp as ArrayList<*>)
                                    for (item in listTemp)
                                        putLaporanRuangan(email, tanggal, namaRuangan, item.nama)
                                    getListLaporanRuangan(idLantai, email, tanggal, namaRuangan)
                                }
                            }
                            .addOnFailureListener {
                                sendCrashlytics("Failed to put LaporanRuangan", it)
                            }
                }
                .addOnFailureListener {
                    sendCrashlytics("Failed to put LaporanBase", it)
                }

    }

    /**
     * This method used to instantiate dummy laporan data on database
     * Only called once by setListLaporan()
     *
     * @param email an email of logged in user.
     * @param tanggal date of laporan.
     * @param lokasi location of the created laporan.
     * @param nama item name.
     * @return nothing.
     */
    private fun putLaporanRuangan(email: String, tanggal: String, lokasi: String, nama: String) {
        val emptyLaporan = LaporanRuangan(nama, nama, lokasi, tanggal)
        UserFirestoreRepo.getLaporanRuanganRef(email, tanggal, lokasi, nama)
                .set(emptyLaporan)
    }

    fun clearLaporanRuangan() {
        _listLaporanRuangan.value = mutableListOf()
    }

    // Get the list of report of some Ruangan at current date
    fun getListLaporanRuangan(idLantai: String, email: String, tanggal: String, namaRuangan: String) {
        UserFirestoreRepo.getListLaporanRuanganRef(email, tanggal, namaRuangan).get()
                .addOnSuccessListener { coll ->
                    if (coll.isEmpty) {
                        setListLaporanRuangan(idLantai, email, tanggal, namaRuangan)
                        return@addOnSuccessListener
                    } else {
                        _listLaporanRuangan.value = coll.mapNotNull { it.toLaporanRuangan() }
                    }
                }
                .addOnFailureListener { error ->
                    sendCrashlytics("Failed to fetch LaporanRuangan", error)
                }

    }

    // Change the selected value in spinner
    fun setSelectedLantai(newValue: Lantai, position: Int) {
        _selectedLantai.value = newValue
        _selectedPosition = position
    }

    // Attach snapshotListener to _listLantai
    private fun attachLantaiListener() {
        lantaiListener = UserFirestoreRepo
            .getListLantaiRef()
            .addSnapshotListener { value, error ->
                if (error != null) sendCrashlytics("error fetching list lantai", error)

                val lantaiTemp: MutableList<Lantai> = mutableListOf()
                for (item in value!!) {
                    val lantai = item.toLantai()
                    if (lantai != null) lantaiTemp.add(lantai)
                }

                _listLantai.value = lantaiTemp

                populateArrayListLantai(lantaiTemp)
        }
    }

    private fun populateArrayListLantai(mutableList: MutableList<Lantai>) {
        val tempList = mutableListOf<String>()
        for (item in mutableList)
            tempList.add(item.nama)

        _arrayListLantai.value = tempList.toTypedArray()
    }

    private fun sendCrashlytics(message: String, error: Exception) {
        Log.e(TAG, message, error)
        FirebaseCrashlytics.getInstance().log(message)
        FirebaseCrashlytics.getInstance().recordException(error)
    }

    override fun onCleared() {
        super.onCleared()
        lantaiListener.remove()
    }
    companion object {
        private const val TAG = "UserHome"
    }
}