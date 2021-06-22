package com.machina.siband.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ruangan(
        val listKeluhan: ArrayList<String>,
        val nama: String = ""
): Parcelable {

    companion object {
        private const val TAG = "Ruangan"

        fun DocumentSnapshot.toRuangan(): Ruangan? {
            return try {
                val nama = getString("nama")!!
                var listKeluhan = get("listKeluhan")!!
                Log.d(TAG, "Nama Ruangan: $nama")
                Ruangan(listKeluhan as ArrayList<String>, nama)
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
