package com.machina.siband.admin.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.machina.siband.model.*
import com.machina.siband.model.Account.Companion.toAccount
import com.machina.siband.repository.AdminFirestoreRepo
import com.machina.siband.model.Lantai.Companion.toLantai
import com.machina.siband.model.LaporanBase.Companion.toLaporanBase
import com.machina.siband.model.LaporanRuangan.Companion.toLaporanRuangan
import com.machina.siband.model.Ruangan.Companion.toRuangan
import com.machina.siband.repository.FirebaseStorageRepo
import com.machina.siband.repository.UserFirestoreRepo
import com.machina.siband.user.viewModel.UserHomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminViewModel: ViewModel() {

    private val _listLantai = MutableLiveData<List<Lantai>>()
    val listLantai: LiveData<List<Lantai>> = _listLantai

    private val _listRuangan = MutableLiveData<List<Ruangan>>()
    val listRuangan: LiveData<List<Ruangan>> = _listRuangan

    private val _listItem = MutableLiveData<ArrayList<String>>()
    val listItem: LiveData<ArrayList<String>> = _listItem

    private val _listAccount = MutableLiveData<List<Account>>()
    val listAccount: LiveData<List<Account>> = _listAccount

    private var _selectedRuangan: Ruangan? = null
    val selectedRuangan get() = _selectedRuangan!!

    private var _listLaporanBase = MutableLiveData<List<LaporanBase>>()
    val listLaporanBase: LiveData<List<LaporanBase>> = _listLaporanBase

    private var _listLaporanRuangan = MutableLiveData<List<LaporanRuangan>>()
    val listLaporanRuangan: LiveData<List<LaporanRuangan>> = _listLaporanRuangan

    private var _listLaporanNoProgressYet = MutableLiveData<List<LaporanRuangan>>()
    val listLaporanNoProgressYet: LiveData<List<LaporanRuangan>> = _listLaporanNoProgressYet

    private var _listLaporanOnProgress = MutableLiveData<List<LaporanRuangan>>()
    val listLaporanOnProgress: LiveData<List<LaporanRuangan>> = _listLaporanOnProgress

    private var _listLaporanDone = MutableLiveData<List<LaporanRuangan>>()
    val listLaporanDone: LiveData<List<LaporanRuangan>> = _listLaporanDone

    private val _imagesUri = mutableListOf<Uri>()

    var currentImageUri: Uri? = null


    fun getListLaporanBase() {
        AdminFirestoreRepo.getListLaporanBaseRef()
            .get()
            .addOnSuccessListener { snapShot ->
                val tempList = snapShot.mapNotNull { it.toLaporanBase() }
                _listLaporanBase.value = tempList
                fetchListLaporanRuangan()
            }
            .addOnFailureListener {
                sendCrashlytic("Error when fetching ListLaporanBase", it)
            }
    }

    private fun fetchListLaporanRuangan() {
        val tempMutable = mutableListOf<LaporanRuangan>()
        val laporanBase = listLaporanBase.value
        if (laporanBase.isNullOrEmpty()) return

        // Call data for each item in
        for (item in laporanBase) {
            UserFirestoreRepo.getListLaporanRuanganRef(item.email, item.tanggal, item.lokasi)
                .get()
                .addOnSuccessListener { coll ->
                    var temp = coll.mapNotNull { it.toLaporanRuangan() }
                    temp = temp.filter {
                        it.tipe.isNotEmpty() && it.isChecked
                    }
                    tempMutable.addAll(temp)
                    _listLaporanRuangan.value = tempMutable
                    viewModelScope.launch(Dispatchers.Default) {
                        filterListLaporan()
                    }
                }
                .addOnFailureListener { error ->
                    sendCrashlytic("Failed to fetch LaporanRuangan", error)
                }
        }
    }

    private suspend fun filterListLaporan() {
        val listLaporan =  _listLaporanRuangan.value
        println(Thread.currentThread().name)
        if (listLaporan != null) {
            var temp = listLaporan.filter { it.status == UserHomeViewModel.NO_PROGRESS }
            setListLaporanNoProgressYet(temp)
//            for (item in temp) Log.d(TAG, "item : ${item.nama}, status : ${item.status}")

            temp = listLaporan.filter { it.status == UserHomeViewModel.ON_PROGRESS }
            setListLaporanOnProgress(temp)
//            for (item in temp) Log.d(TAG, "item : ${item.nama}, status : ${item.status}")

            temp = listLaporan.filter { it.status == UserHomeViewModel.DONE }
            setListLaporanDone(temp)
//            for (item in temp) Log.d(TAG, "item : ${item.nama}, status : ${item.status}")
        }
    }

    private suspend fun setListLaporanNoProgressYet(data: List<LaporanRuangan>) {
        withContext(Dispatchers.Main) {
            _listLaporanNoProgressYet.value = data
        }
    }

    private suspend fun setListLaporanOnProgress(data: List<LaporanRuangan>) {
        withContext(Dispatchers.Main) {
            _listLaporanOnProgress.value = data
        }
    }

    private suspend fun setListLaporanDone(data: List<LaporanRuangan>) {
        withContext(Dispatchers.Main) {
            _listLaporanDone.value = data
        }
    }

    fun getListLantai() {
        AdminFirestoreRepo.getListLantaiRef()
            .get()
            .addOnSuccessListener { snapshot ->
                _listLantai.value = snapshot.mapNotNull { it.toLantai() }
            }
            .addOnFailureListener {
                sendCrashlytic("Failed to fetch ListLantai on admin", it)
            }
    }

    fun getListRuangan(lantai: Lantai) {
        AdminFirestoreRepo.getListRuanganRef(lantai.id)
            .get()
            .addOnSuccessListener { snapshot ->
                val temp = snapshot.mapNotNull { it.toRuangan() }
                _listRuangan.value = temp
            }
            .addOnFailureListener {
                sendCrashlytic("Failed to fetch List Ruangan", it)
            }
    }

    fun getListItem(lantai: Lantai, ruangan: Ruangan) {
        AdminFirestoreRepo.getRuanganRef(lantai.id, ruangan.nama)
            .get()
            .addOnSuccessListener { snapshot ->
                val tempArrayList = snapshot.get("listKeluhan")
                _listItem.value = arrayListOf()
                _listItem.value = tempArrayList as ArrayList<String>
            }
            .addOnFailureListener {
                sendCrashlytic("Failed to fetch List Item on Admin", it)
            }
    }

    fun deleteLantai(lantai: Lantai) {
        AdminFirestoreRepo.getListRuanganRef(lantai.nama)
            .get()
            .addOnSuccessListener { snapshot ->
                val listRuanganRef = snapshot.mapNotNull { it.toRuangan() }
                Firebase.firestore.runBatch { batch ->
                    for (item in listRuanganRef) {
                        val ref = AdminFirestoreRepo.getRuanganRef(lantai.id, item.nama)
                        batch.delete(ref)
                    }
                }
                .addOnSuccessListener {
                    deleteLantaiRef(lantai)
                }
                .addOnFailureListener {
                    sendCrashlytic("Failed to batch delete on Delete Collection", it)
                }
            }
    }

    fun addLantai(lantai: Lantai, imageUri: Uri) {
        FirebaseStorageRepo.getMapImageRef(lantai.nama)
            .putFile(imageUri)
            .addOnFailureListener {
                sendCrashlytic("Failed to put new Map Image", it)
            }

        AdminFirestoreRepo.getLantaiRef(lantai.nama)
            .set(lantai)
            .addOnSuccessListener {
                getListLantai()
            }
            .addOnFailureListener {
                sendCrashlytic("Failed to add new lantai", it)
            }
    }

    private fun deleteLantaiRef(lantai: Lantai) {
        AdminFirestoreRepo.getLantaiRef(lantai.nama)
            .delete()
            .addOnSuccessListener {
                getListLantai()
            }
            .addOnFailureListener {
                sendCrashlytic("Failed to delete Lantai on Admin", it)
            }
    }

    fun deleteRuangan(lantai: Lantai, itemName: String) {
        AdminFirestoreRepo.getRuanganRef(lantai.id, itemName)
            .delete()
            .addOnSuccessListener {
                getListRuangan(lantai)
            }
            .addOnFailureListener {
                sendCrashlytic("Failed to delete Ruangan on admin", it)
            }
    }

    fun addRuangan(lantai: Lantai, itemName: String) {
        val newRuangan = Ruangan(arrayListOf(), itemName)

        AdminFirestoreRepo.getRuanganRef(lantai.id, itemName)
            .set(newRuangan)
            .addOnSuccessListener {
                getListRuangan(lantai)
            }
            .addOnFailureListener {
                sendCrashlytic("Failed to add Ruangan on admin", it)
            }
    }

    fun deleteItem(lantai: Lantai, ruangan: Ruangan, itemName: String) {
        val newListItem = selectedRuangan.listKeluhan.filter {
            it != itemName
        }
        val newRuangan = selectedRuangan.copy(listKeluhan = newListItem as ArrayList<String>)

        AdminFirestoreRepo.getRuanganRef(lantai.id, ruangan.nama)
            .set(newRuangan)
            .addOnSuccessListener {
                setSelectedRuangan(newRuangan)
                getListItem(lantai, ruangan)
            }
            .addOnFailureListener {
                sendCrashlytic("Error when deleting item on admin", it)
            }
    }

    fun addItem(lantai: Lantai, ruangan: Ruangan, itemName: String) {
        val newListItem = selectedRuangan.listKeluhan
        newListItem.add(itemName)
        val newRuangan = selectedRuangan.copy(listKeluhan = newListItem)

        AdminFirestoreRepo.getRuanganRef(lantai.id, ruangan.nama)
            .set(newRuangan)
            .addOnSuccessListener {
                setSelectedRuangan(newRuangan)
                getListItem(lantai, ruangan)
            }
            .addOnFailureListener {
                sendCrashlytic("Error when deleting item on admin", it)
            }
    }

    fun putNewLaporanRuangan(laporanRuangan: LaporanRuangan,  images: List<Uri>) {
        val email = laporanRuangan.email
        val tanggal = laporanRuangan.tanggal
        val lokasi = laporanRuangan.lokasi
        val nama = laporanRuangan.nama

        UserFirestoreRepo.getLaporanRuanganRef(email, tanggal, lokasi, nama)
            .set(laporanRuangan)
            .addOnSuccessListener {
                putNewImage(laporanRuangan, images)
            }
            .addOnFailureListener { exception ->
                sendCrashlytic("An error occured when trying to upload new laporan", exception)
            }
    }

    private fun putNewImage(laporanRuangan: LaporanRuangan, images: List<Uri>) {
        val email = laporanRuangan.email
        val tanggal = laporanRuangan.tanggal
        val lokasi = laporanRuangan.lokasi
        val nama = laporanRuangan.nama

        images.forEachIndexed { index, uri ->
            FirebaseStorageRepo.getLaporanPerbaikanImageRef(email, tanggal, lokasi, "${nama}${index}")
                .putFile(uri)
                .addOnFailureListener{
                    sendCrashlytic("Failed to Put new Dokumentasi Laporan Images", it)
                }
        }
    }

    fun getListAccount() {
        AdminFirestoreRepo.getListAccountRef()
            .get()
            .addOnSuccessListener { snapshot ->
                _listAccount.value = snapshot.mapNotNull { it.toAccount() }
            }
            .addOnFailureListener {
                sendCrashlytic("Failed to Get List Account on Admin", it)
            }
    }

    fun clearImagesUri() {
        while (_imagesUri.size > 0)
            _imagesUri.removeLast()
    }

    fun getImagesUri(): MutableList<Uri> {
        return _imagesUri
    }

    fun addImageToImagesUri(imageUri: Uri){
        _imagesUri.add(imageUri)
    }

    fun setSelectedRuangan(ruangan: Ruangan) {
        _selectedRuangan = ruangan
    }

    fun clearListRuangan() {
        _listRuangan.value = listOf()
    }

    fun clearListItem() {
        _listItem.value = arrayListOf()
    }

    fun getUserEmail(): String {
        val currUser = Firebase.auth.currentUser
        return currUser?.email ?: "-"
    }

    private fun sendCrashlytic(message: String, error: Exception) {
        Log.e(TAG, message, error)
        FirebaseCrashlytics.getInstance().log(message)
        FirebaseCrashlytics.getInstance().recordException(error)
    }

    companion object {
        private const val TAG = "AdminViewModel"
        private const val NO_PROGRESS = "No Progress Yet"
        private const val ON_PROGRESS = "On Progress"
        private const val DONE = "Done"
    }
}