package com.machina.siband.admin.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.machina.siband.model.*
import com.machina.siband.model.Account.Companion.toAccount
import com.machina.siband.model.AreaRuangan.Companion.toAreaRuangan
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
import java.util.*
import kotlin.collections.ArrayList

class AdminViewModel : ViewModel() {

  private val _listLantai = MutableLiveData<List<Lantai>>()
  val listLantai: LiveData<List<Lantai>> = _listLantai

  private val _listRuangan = MutableLiveData<List<Ruangan>>()
  val listRuangan: LiveData<List<Ruangan>> = _listRuangan

  private val _listItem = MutableLiveData<ArrayList<String>>()
  val listItem: LiveData<ArrayList<String>> = _listItem

  private val _listAreaRuangan = MutableLiveData<List<AreaRuangan>>()
  val listAreaRuangan: LiveData<List<AreaRuangan>> = _listAreaRuangan

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

  private lateinit var _laporanListener: ListenerRegistration

  private var _listLaporanResult = listOf<LaporanRuangan>()
  private var _listLaporanSafe = listOf<LaporanRuangan>()

  private val _imagesUri = mutableListOf<Uri>()

  var currentImageUri: Uri? = null

  var errorFlag = MutableLiveData(false)
  var errorMessage: String? = ""


  fun getListLaporanByDate(tanggal: String): List<LaporanRuangan> {
    val listLaporan = _listLaporanResult
    return listLaporan.filter { it.tanggal == tanggal }
  }

  fun getListNamaRuangan(): Set<String> {
    val tempLaporanBase = _listLaporanBase.value
    if (tempLaporanBase == null) {
      return setOf()
    } else {
      val listNamaRuangan = mutableSetOf<String>()
      for (item in tempLaporanBase) listNamaRuangan.add(item.lokasi)
      return listNamaRuangan
    }
  }

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
    val resTemp = mutableListOf<LaporanRuangan>()
    val laporanBase = listLaporanBase.value
    if (laporanBase.isNullOrEmpty()) return

    // Call data for each item in
    for (item in laporanBase) {
      UserFirestoreRepo.getListLaporanRuanganRef(item.email, item.tanggal, item.lokasi)
        .get()
        .addOnSuccessListener { coll ->
          var temp = coll.mapNotNull { it.toLaporanRuangan() }
          resTemp.addAll(temp)
          _listLaporanResult = resTemp

          temp = temp.filter { it.tipe.isNotEmpty() }
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
    val listLaporan = _listLaporanRuangan.value
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
    AdminFirestoreRepo.getListRuanganRef(lantai.nama)
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
    AdminFirestoreRepo.getRuanganRef(lantai.nama, ruangan.nama)
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
            val ref = AdminFirestoreRepo.getRuanganRef(lantai.nama, item.nama)
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
    AdminFirestoreRepo.getRuanganRef(lantai.nama, itemName)
      .delete()
      .addOnSuccessListener {
        getListRuangan(lantai)
      }
      .addOnFailureListener {
        sendCrashlytic("Failed to delete Ruangan on admin", it)
      }
  }

  fun isOnlyLetterOrDigit(string: String): Boolean {
    val length = string.length - 1
    for (i in 0..length) {
      if (string[i] == ' ' || string[i] == '&' || string[i] == '.') {
        continue
      }
      if (!string[i].isLetterOrDigit()) {
        return false
      }
    }
    return true
  }

  fun addRuangan(
    lantai: Lantai,
    itemName: String,
    area: String,
    listItem: ArrayList<String>,
    listKelompok: ArrayList<String>
  ) {
    if (itemName == ".") {
      errorMessage = "Field Name tidak boleh hanya '.'"
      errorFlag.value = true
      return
    }
    if (itemName == "..") {
      errorMessage = "Field Nama tidak boleh hanya '..'"
      errorFlag.value = true
      return
    }
    if (itemName.find { it == '/' } != null) {
      errorMessage = "Field Nama tidak boleh menggunakan '/'"
      errorFlag.value = true
      return
    }
    if (!isOnlyLetterOrDigit(itemName)) {
      errorMessage = "Field Nama hanya boleh menggunakan symbol spesifik, alphabet, dan angka"
      errorFlag.value = true
      return
    }


    AdminFirestoreRepo.getAreaRuanganRef(area)
      .get()
      .addOnSuccessListener {
        val warna = it.toAreaRuangan()?.warna
        if (warna.isNullOrBlank()) return@addOnSuccessListener
        AdminFirestoreRepo.getLantaiRef(lantai.nama)
          .get()
          .addOnSuccessListener { snapshot ->
            val tempLantai = snapshot.toLantai()
            if (tempLantai != null) {
              val index = tempLantai.lastRuanganIndex
              val newRuangan =
                Ruangan(listItem, listKelompok, itemName, area, warna, lantai.nama, index + 1)
              AdminFirestoreRepo.getRuanganRef(lantai.nama, itemName)
                .set(newRuangan)
                .addOnSuccessListener {
                  getListRuangan(lantai)
                  AdminFirestoreRepo.getLantaiRef(lantai.nama)
                    .update("lastRuanganIndex", index + 1)
                    .addOnFailureListener { e ->
                      sendCrashlytic("Failed to update last index", e)
                    }
                }
                .addOnFailureListener { e ->
                  sendCrashlytic("Failed to add Ruangan on admin", e)
                }
            }
          }
      }
      .addOnFailureListener {
        sendCrashlytic("Failed to retrieve warna from Area Ruangan on admin", it)
      }
  }

  fun deleteItem(lantai: Lantai, ruangan: Ruangan, itemName: String) {
    val deleteIndex = selectedRuangan.listItem.indexOf(itemName)
    selectedRuangan.listItem.remove(itemName)
    selectedRuangan.listKelompok.removeAt(deleteIndex)

    AdminFirestoreRepo.getRuanganRef(lantai.nama, ruangan.nama)
      .set(selectedRuangan)
      .addOnSuccessListener {
        getListItem(lantai, ruangan)
      }
      .addOnFailureListener {
        sendCrashlytic("Error when deleting item on admin", it)
      }
  }

  fun addItem(lantai: Lantai, ruangan: Ruangan, itemName: String, kelompok: String) {
    val listItem = selectedRuangan.listItem
    val listKelompok = selectedRuangan.listKelompok
    listItem.add(itemName)
    listKelompok.add(kelompok)
    val newRuangan = selectedRuangan.copy(listItem = listItem, listKelompok = listKelompok)

    AdminFirestoreRepo.getRuanganRef(lantai.nama, ruangan.nama)
      .set(newRuangan)
      .addOnSuccessListener {
        setSelectedRuangan(newRuangan)
        getListItem(lantai, ruangan)
      }
      .addOnFailureListener {
        sendCrashlytic("Error when deleting item on admin", it)
      }
  }

  fun putNewLaporanRuangan(laporanRuangan: LaporanRuangan, images: List<Uri>) {
    val email = laporanRuangan.email
    val tanggal = laporanRuangan.tanggal
    val lokasi = laporanRuangan.lokasi
    val nama = laporanRuangan.nama

    UserFirestoreRepo.getLaporanRuanganRef(email, tanggal, lokasi, nama)
      .set(laporanRuangan)
      .addOnSuccessListener {
        val newTime = Calendar.getInstance().timeInMillis.toString()
        UserFirestoreRepo.getLaporanBaseRef(email, tanggal, lokasi)
          .update("lastUpdated", newTime)
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
        .addOnFailureListener {
          sendCrashlytic("Failed to Put new Dokumentasi Laporan Images", it)
        }
    }
  }

  fun getListAreaRuangan() {
    AdminFirestoreRepo.getListAreaRuanganRef()
      .get()
      .addOnSuccessListener { snapshot ->
        _listAreaRuangan.value = snapshot.mapNotNull { it.toAreaRuangan() }
      }
      .addOnFailureListener {
        sendCrashlytic("Failed to get List Area Ruangan on Admin", it)
      }
  }

  fun addAreaRuangan(areaRuangan: AreaRuangan) {
    AdminFirestoreRepo.getAreaRuanganRef(areaRuangan.nama)
      .set(areaRuangan)
      .addOnSuccessListener {
        getListAreaRuangan()
      }
      .addOnFailureListener {
        sendCrashlytic("Failed to add Area Ruangan", it)
      }
  }

  fun deleteAreaRuangan(areaRuangan: AreaRuangan) {
    AdminFirestoreRepo.getAreaRuanganRef(areaRuangan.nama)
      .delete()
      .addOnSuccessListener {
        getListAreaRuangan()
      }
      .addOnFailureListener {
        sendCrashlytic("Failed to Delete Area Ruangan", it)
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

  fun addImageToImagesUri(imageUri: Uri) {
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

  fun setLaporanChangeListener() {
    _laporanListener = Firebase.firestore.collection("list-laporan")
      .addSnapshotListener { value, error ->
        if (error != null) {
          Log.w(TAG, "Failed to listen to LaporanRuangan change", error)
          sendCrashlytic("Failed to listen to LaporanRuangan change", error)
          return@addSnapshotListener
        }

        val laporans = mutableListOf<LaporanBase>()
        if (value != null) {
          for (laporan in value) {
            laporan.toLaporanBase()?.let {
              laporans.add(it)
              Log.d(TAG, "Laporan Ruangan $it")
            }
          }
        }
      }
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