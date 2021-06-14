package com.machina.siband.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize
import java.lang.Exception

@Parcelize
data class Lantai(
        val id: String,
        val nama: String,
        val urlMap: String,
        val listRuangan: List<String>) : Parcelable {

    companion object {
        private const val TAG = "Lantai"

        fun DocumentSnapshot.toLantai(): Lantai? {
            return try {
                val nama = getString("nama")!!
                val urlMap = getString("url-map")!!
                var temp = get("list-ruangan")!!
                if (temp is ArrayList<*>) {
                    temp = temp.toList()
                }
                Lantai(id, nama, urlMap, temp as List<String>)
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
