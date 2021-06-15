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

    private val _listItem = MutableLiveData<List<String>>()
    val listItem: LiveData<List<String>> = _listItem


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

    fun getListRuangan(nama: String) {
        AdminFirestoreRepo.getLantaiRef(nama)
            .get()
            .addOnSuccessListener { lantaiSnapshot ->
                val document = lantaiSnapshot.documents.first()
                if (document != null) {
                    AdminFirestoreRepo.getListRuanganRef(document.id)
                        .get()
                        .addOnSuccessListener { ruanganSnapshot ->
                            val temp = ruanganSnapshot.documents.mapNotNull { it.toRuangan() }
                            _listRuangan.value = temp
                        }
                        .addOnFailureListener {
                            sendCrashlytic("Failed to fetch List Ruangan", it)
                        }
                }
            }
            .addOnFailureListener {
                sendCrashlytic("Failed to fetch Lantai", it)
            }
    }

    fun getListItem(lantai: Lantai, ruangan: Ruangan) {
        AdminFirestoreRepo.getRuanganRef(lantai.id, ruangan.nama)
            .get()
            .addOnSuccessListener { snapshot ->
                var tempArrayList = snapshot.get("list-keluhan")

                if (tempArrayList is ArrayList<*>) {
                    tempArrayList = tempArrayList.toList()
                    _listItem.value = tempArrayList as List<String>
                }
            }
            .addOnFailureListener {
                sendCrashlytic("Failed to fetch List Item in Admin", it)
            }
    }


    fun clearListRuangan() {
        _listRuangan.value = listOf()
    }

    fun clearListItem() {
        _listItem.value = listOf()
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