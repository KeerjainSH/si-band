package com.machina.siband.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize
import java.lang.Exception

@Parcelize
data class Lantai(
    val nama: String,
    val lastRuanganIndex: Long
) : Parcelable {

    companion object {
        private const val TAG = "Lantai"

        fun DocumentSnapshot.toLantai(): Lantai? {
            return try {
                val nama = getString("nama")!!
                val lastRuanganIndex = getLong("lastRuanganIndex")!!
                Lantai(nama, lastRuanganIndex)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting lantai data", e)
                FirebaseCrashlytics.getInstance().log("Error converting user profile")
                FirebaseCrashlytics.getInstance().setCustomKey("userId", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }
    }
}
