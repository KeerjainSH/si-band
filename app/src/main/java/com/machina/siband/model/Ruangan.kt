package com.machina.siband.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ruangan(
        val listKeluhan: List<String>,
        val nama: String = ""
): Parcelable {

    companion object {
        private const val TAG = "Ruangan"

        fun DocumentSnapshot.toRuangan(): Ruangan? {
            return try {
                val nama = getString("nama")!!
                var listKeluhan = get("list-keluhan")!!
                if (listKeluhan is ArrayList<*>) {
                    listKeluhan = listKeluhan.toList()
                }

                Ruangan(listKeluhan as List<String>, nama)
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
