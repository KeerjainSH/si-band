package com.machina.siband.admin.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.machina.siband.admin.repository.AdminFirestoreRepo
import com.machina.siband.model.Lantai
import com.machina.siband.model.Lantai.Companion.toLantai
import com.machina.siband.model.Ruangan
import com.machina.siband.model.Ruangan.Companion.toRuangan

class AdminViewModel: ViewModel() {

    private val _listLantai = MutableLiveData<List<Lantai>>()
    val listLantai: LiveData<List<Lantai>> = _listLantai

    private val _listRuangan = MutableLiveData<List<Ruangan>>()
    val listRuangan: LiveData<List<Ruangan>> = _listRuangan

    private val _listItem = MutableLiveData<ArrayList<String>>()
    val listItem: LiveData<ArrayList<String>> = _listItem

    private var _selectedRuangan: Ruangan? = null
    val selectedRuangan get() = _selectedRuangan!!


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

    fun addLantai(lantai: Lantai) {
        AdminFirestoreRepo.getListLantaiRef()
            .add(lantai)
            .addOnFailureListener {
                sendCrashlytic("Failed to add Lantai on Admin", it)
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
                sendCrashlytic("Failed to fetch List Item in Admin", it)
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

    fun setSelectedRuangan(ruangan: Ruangan) {
        _selectedRuangan = ruangan
    }


    fun clearListRuangan() {
        _listRuangan.value = listOf()
    }

    fun clearListItem() {
        _listItem.value = arrayListOf()
    }

    private fun sendCrashlytic(message: String, error: Exception) {
        Log.e(TAG, message, error)
        FirebaseCrashlytics.getInstance().log(message)
        FirebaseCrashlytics.getInstance().recordException(error)
    }

    companion object {
        private const val TAG = "AdminViewModel"
    }
}