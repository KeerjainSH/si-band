package com.machina.siband.user.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object UserFirestoreRepo {
    private const val TAG = "UserFirestoreRepo"

    private const val COL_LANTAI = "list-lantai"
    private const val COL_LAPORAN = "list-laporan"

    fun getLaporanBaseRef(email: String, tanggal: String, namaRuangan: String): DocumentReference {
        val path = "${email}_${tanggal}_${namaRuangan}"
        return Firebase.firestore
                .collection(COL_LAPORAN)
                .document(path)
    }

    /**
     * Get a CollectionReference to a List of Laporan
     * based on the parameter.
     *
     * @param email of the logged in user.
     * @param tanggal todays date.
     * @param namaRuangan
     * @return CollectionReference to a List of Laporan.
     */
    fun getListLaporanRuanganRef(email: String, tanggal: String, namaRuangan: String): CollectionReference {
        // Change this path to actual parameter later, this was only for development purpose
        val path = "${email}_${tanggal}_${namaRuangan}"
        return Firebase.firestore
                .collection(COL_LAPORAN)
                .document(path)
                .collection("laporan")
    }

    /**
     * Get a Query to a list laporan based
     * on email param.
     *
     * @param email of the logged in user.
     * @return Query reference to LaporanRuangan User.
     */
    fun getUserListLaporanRuanganRef(email: String): Query {
        return Firebase.firestore
            .collection(COL_LAPORAN)
            .whereEqualTo("email", email)
            .whereEqualTo("isSubmitted", true)
    }

    /**
     * Get a DocumentReference of a Laporan
     * based on the parameter.
     *
     * @param email of the logged in user.
     * @param tanggal todays date.
     * @param namaRuangan the name of Ruangan.
     * @param name the name of item in Ruangan.
     * @return DoccumentReference to a Laporan.
     */
    fun getLaporanRuanganRef(email: String, tanggal: String, namaRuangan: String, nama: String): DocumentReference{
        val path = "${email}_${tanggal}_${namaRuangan}"
        return Firebase.firestore
                .collection(COL_LAPORAN)
                .document(path)
                .collection("laporan")
                .document(nama)
    }

    fun getListLantaiRef(): CollectionReference {
        return Firebase.firestore
                .collection(COL_LANTAI)
    }

    fun getListDetailRuanganRef(idLantai: String, namaRuangan: String): DocumentReference {
        return Firebase.firestore
                .collection(COL_LANTAI)
                .document(idLantai)
                .collection("col-ruangan")
                .document(namaRuangan)
    }
}