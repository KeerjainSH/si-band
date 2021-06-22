package com.machina.siband.admin.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.machina.siband.repository.AdminFirestoreRepo
import com.machina.siband.model.Lantai
import com.machina.siband.model.Lantai.Companion.toLantai
import com.machina.siband.model.Ruangan
import com.machina.siband.model.Ruangan.Companion.toRuangan
import com.machina.siband.repository.FirebaseStorageRepo

class AdminViewModel: ViewModel() {

    private val _listLantai = MutableLiveData<List<Lantai>>()
    val listLantai: LiveData<List<Lantai>> = _listLantai

    private val _listRuangan = MutableLiveData<List<Ruangan>>()
    val listRuangan: LiveData<List<Ruangan>> = _listRuangan

    private val _listItem = MutableLiveData<ArrayList<String>>()
    val listItem: LiveData<ArrayList<String>> = _listItem

    private var _selectedRuangan: Ruangan? = null
    val selectedRuangan get() = _selectedRuangan!!

    var currentImageUri: Uri? = null

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