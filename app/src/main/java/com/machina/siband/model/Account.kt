package com.machina.siband.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class Account(
    val email: String,
    val nama: String,
    val password: String,
    val tipeAkun: String
): Parcelable {

    companion object {
        private const val TAG = "Account"

        fun DocumentSnapshot.toAccount(): Account? {
            return try {
                val nama = getString("nama")!!
                val password = getString("password")!!
                val tipeAkun = getString("tipeAkun")!!

                Log.d(TAG, "Converted to Account email [$id]")
                Account(id, nama, password, tipeAkun)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting to Account", e)
                FirebaseCrashlytics.getInstance().log("Error converting to Account")
                FirebaseCrashlytics.getInstance().setCustomKey("userId", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }
    }
}
