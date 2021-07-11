package com.machina.siband.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ruangan(
    val listItem: ArrayList<String>,
    val listKelompok: ArrayList<String>,
    val nama: String = "",
    val area: String = "",
    val warna: String = "",
    val lantai: String = ""
): Parcelable {

    companion object {
        private const val TAG = "Ruangan"

        fun DocumentSnapshot.toRuangan(): Ruangan? {
            return try {
                val nama = getString("nama")!!
                val listItem = get("listItem")!!
                val listKelompok = get("listKelompok")!!
                val area = getString("area")!!
                val warna = getString("warna")!!
                val lantai = getString("lantai")!!
                Log.d(TAG, "Nama Ruangan: $nama")
                Ruangan(listItem as ArrayList<String>, listKelompok as ArrayList<String>, nama, area, warna, lantai)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting ruangan data", e)
                FirebaseCrashlytics.getInstance().log("Error converting user profile")
                FirebaseCrashlytics.getInstance().setCustomKey("userId", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }
    }
}
