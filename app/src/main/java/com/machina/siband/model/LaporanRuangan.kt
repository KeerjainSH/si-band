package com.machina.siband.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class LaporanRuangan(
        val id: String,
        val nama: String,
        val email: String = "",
        val lokasi: String,
        val tanggal: String,
        val tipe: String = "",
        val dokumentasi: Int = 0,
        val keterangan: String = "",
        val status: String = "No Progress Yet",
        val dokumentasiPerbaikan: Int = 0,
        @field:JvmField
        val isChecked: Boolean = false): Parcelable {

    companion object {
        private const val TAG = "LaporanRuangan"

        fun DocumentSnapshot.toLaporanRuangan(): LaporanRuangan? {
            return try {
                val nama = getString("nama")!!
                val email = getString("email")!!
                val lokasi = getString("lokasi")!!
                val tanggal = getString("tanggal")!!
                val tipe = getString("tipe")!!
                val dokumentasi = get("dokumentasi")!! as Long
                val keterangan = getString("keterangan")!!
                val status = getString("status")!!
                val dokumentasiPerbaikan = get("dokumentasiPerbaikan")!! as Long
                val isChecked = getBoolean("isChecked")!!
                Log.d(TAG, "Converted to LaporanRuangan nama [$nama] lokasi [$lokasi]")
                LaporanRuangan(id, nama, email, lokasi, tanggal, tipe, dokumentasi.toInt(), keterangan, status, dokumentasiPerbaikan.toInt(), isChecked)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting to LaporanRuangan", e)
                FirebaseCrashlytics.getInstance().log("Error converting to LaporanRuangan")
                FirebaseCrashlytics.getInstance().setCustomKey("userId", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }
    }
}
