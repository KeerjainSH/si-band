package com.machina.siband.user.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ruangan(
        val nama: String,
        val isChecked: Boolean = false): Parcelable {

    companion object {
        private const val TAG = "Ruangan"

        fun DocumentSnapshot.toRuangan(): Ruangan? {
            return try {
                val nama = getString("nama")!!
                Ruangan(nama, )
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
