package com.machina.siband.user.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class LaporanRuangan(
        val id: String,
        val nama: String,
        val lokasi: String,
        val tanggal: String,
        val tipe: String = "",
        val dokumentasi: String = "",
        val keterangan: String = "",
        val status: String = "",
        val dokumentasiPerbaikan: String = "",
        @field:JvmField
        val isChecked: Boolean = false): Parcelable {

    companion object {
        private const val TAG = "LaporanRuangan"

        fun DocumentSnapshot.toLaporanRuangan(): LaporanRuangan? {
            return try {
                val nama = getString("nama")!!
                val lokasi = getString("lokasi")!!
                val tanggal = getString("tanggal")!!
                val tipe = getString("tipe")!!
                val dokumentasi = getString("dokumentasi")!!
                val keterangan = getString("keterangan")!!
                val status = getString("status")!!
                val dokumentasiPerbaikan = getString("dokumentasiPerbaikan")!!
                val isChecked = getBoolean("isChecked")!!
                Log.d(TAG, "Converted to LaporanRuangan")
                LaporanRuangan(id, nama, lokasi, tanggal, tipe, dokumentasi, keterangan, status, dokumentasiPerbaikan, isChecked)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting to LaporanRuangan", e)
                FirebaseCrashlytics.getInstance().log("Error converting user profile")
                FirebaseCrashlytics.getInstance().setCustomKey("userId", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }
    }
}
