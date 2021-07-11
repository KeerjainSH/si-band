package com.machina.siband.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class AreaRuangan(
    val nama: String,
    val warna: String
): Parcelable {

    companion object {
        private const val TAG = "AreaRuangan"

        fun DocumentSnapshot.toAreaRuangan(): AreaRuangan? {
            return try {
                val nama = getString("nama")!!
                val warna = getString("warna")!!

                AreaRuangan(nama, warna)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting ruangan data", e)
                FirebaseCrashlytics.getInstance().log("Error converting to AreaRuangan")
                FirebaseCrashlytics.getInstance().setCustomKey("userId", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }
    }
}