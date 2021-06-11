package com.machina.siband.user.repository

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

object UserFirebaseStorageRepo {

    fun getLaporanImageRef(email: String, tanggal: String, lokasi: String, item: String): StorageReference {
        val path = "$email/$tanggal/$lokasi/$item"

        return Firebase.storage.reference.child(path)
    }
}