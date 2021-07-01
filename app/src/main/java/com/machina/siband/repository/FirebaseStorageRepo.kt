package com.machina.siband.repository

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

object FirebaseStorageRepo {

    fun getLaporanImageRef(email: String, tanggal: String, lokasi: String, item: String): StorageReference {
        val path = "$tanggal/$lokasi/$item"

        return Firebase.storage.reference.child(path)
    }

    fun getLaporanPerbaikanImageRef(
        email: String,
        tanggal: String,
        lokasi: String,
        item: String
    ): StorageReference {
        val path = "$tanggal/$lokasi/perbaikan/$item"

        return Firebase.storage.reference.child(path)
    }


    fun getMapImageRef(namaLantai: String): StorageReference {
        val path = "map/$namaLantai"

        return Firebase.storage.reference.child(path)
    }
}