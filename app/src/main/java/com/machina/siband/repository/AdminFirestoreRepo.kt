package com.machina.siband.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object AdminFirestoreRepo {
    fun getListLantaiRef(): CollectionReference {
        return Firebase.firestore
            .collection("list-lantai")
    }

    fun getLantaiRef(nama: String): DocumentReference {
        return Firebase.firestore
            .collection("list-lantai")
            .document(nama)
    }

    fun getListRuanganRef(id: String): CollectionReference {
        return Firebase.firestore
            .collection("list-lantai")
            .document(id)
            .collection("col-ruangan")
    }

    fun getRuanganRef(id: String, lokasi: String): DocumentReference {
        return Firebase.firestore
            .collection("list-lantai")
            .document(id)
            .collection("col-ruangan")
            .document(lokasi)
    }

    fun getListLaporanBaseRef(): Query {
        return Firebase.firestore
            .collection("list-laporan")
            .whereEqualTo("isSubmitted", true)
    }

    fun getAccountRef(email: String): DocumentReference {
        return Firebase.firestore
            .collection("user-info")
            .document(email)
    }

    fun getListAccountRef(): CollectionReference {
        return Firebase.firestore
            .collection("user-info")
    }

    fun getListAreaRuanganRef(): CollectionReference {
        return Firebase.firestore
            .collection("list-letak-ruangan")
    }

    fun getAreaRuanganRef(nama: String): DocumentReference {
        return Firebase.firestore
            .collection("list-letak-ruangan")
            .document(nama)
    }
}