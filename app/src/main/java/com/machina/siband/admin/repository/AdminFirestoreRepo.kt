package com.machina.siband.admin.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object AdminFirestoreRepo {

    fun getListLantaiRef(): CollectionReference {
        return Firebase.firestore.collection("list-lantai")
    }

    fun getLantaiRef(nama: String): Query {
        return Firebase.firestore
            .collection("list-lantai")
            .whereEqualTo("nama", nama)
    }

    fun getListRuanganRef(id: String): CollectionReference {
        return Firebase.firestore
            .collection("list-lantai")
            .document(id)
            .collection("col-ruangan")
    }
}