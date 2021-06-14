package com.machina.siband.admin.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.machina.siband.admin.repository.AdminFirestoreRepo
import com.machina.siband.model.Lantai
import com.machina.siband.model.Lantai.Companion.toLantai
import com.machina.siband.user.viewModel.UserHomeViewModel

class AdminViewModel: ViewModel() {

    private val _listLantai = MutableLiveData<List<Lantai>>()
    val listLantai: LiveData<List<Lantai>> = _listLantai





    fun getListLantai() {
        AdminFirestoreRepo.getListLantaiRef()
            .get()
            .addOnSuccessListener { snapshot ->
                _listLantai.value = snapshot.mapNotNull { it.toLantai() }
            }
            .addOnFailureListener { exception ->
                sendCrashlytic("Error getting ListLantai on admin", exception)
            }
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