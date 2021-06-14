package com.machina.siband.admin.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object AdminFirestoreRepo {

    fun getListLantaiRef(): CollectionReference {
        return Firebase.firestore.collection("list-lantai")
    }
}