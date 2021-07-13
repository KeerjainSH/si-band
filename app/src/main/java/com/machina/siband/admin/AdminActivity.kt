package com.machina.siband.admin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.machina.siband.R
import com.machina.siband.admin.viewmodel.AdminViewModel
import com.machina.siband.databinding.ActivityAdminBinding
import com.machina.siband.model.LaporanBase
import com.machina.siband.model.LaporanBase.Companion.toLaporanBase

class AdminActivity: AppCompatActivity() {

    private var _binding: ActivityAdminBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var _laporanListener: ListenerRegistration

    private val viewModel: AdminViewModel by viewModels()

    private var notifFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initiateView()
        initiateDrawerLayout()
    }

    private fun initiateView() {
        _binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel()
        setLaporanChangeListener()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.activity_admin_nav_host) as NavHostFragment
        navController = navHostFragment.navController
        drawerLayout = binding.activityAdminDrawerLayout
        navigationView = binding.activityAdminDrawer
    }

    private fun setLaporanChangeListener() {
        val intent = Intent(this, this::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.group_12)
            .setContentTitle("Laporan Baru")
            .setContentText("Ada laporan yang baru masuk atau ada update pada laporan")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        _laporanListener = Firebase.firestore.collection("list-laporan")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w(TAG, "Failed to listen to LaporanRuangan change", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    if (notifFlag) {
                        with(NotificationManagerCompat.from(this)) {
                            // notificationId is a unique int for each notification that you must define
                            notify(10, builder.build())
                        }
                    }
                    notifFlag = true
                }
            }
    }

    private fun initiateDrawerLayout() {
        val topLevelDestination: Set<Int> =
            setOf(
                R.id.adminSwipeLaporanFragment,
                R.id.adminListLantaiFragment,
                R.id.adminListAreaRuanganFragment,
                R.id.adminListUserFragment
            )

        appBarConfiguration = AppBarConfiguration
            .Builder(topLevelDestination)
            .setOpenableLayout(drawerLayout)
            .build()

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        navigationView.setCheckedItem(R.id.adminSwipeLaporanFragment)
        navigationView.setNavigationItemSelectedListener { dest ->
            when (dest.itemId) {
                R.id.adminLogoutOption -> { logOut() }
                else -> {
                    NavigationUI.onNavDestinationSelected(dest, navController)
                    myCloseDrawer()
                }
            }
            true
        }
    }

    private fun logOut() {
        Firebase.auth.signOut()
        setResult(1000)
        finish()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Nama Channel"
            val descriptionText = "Notifikasi perubahan pada laporan"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun myCloseDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) ||
                super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
            val dest = navController.currentDestination
            if (dest != null) {
                Log.d(TAG, "id: ${dest.id} | label: ${dest.label} | navName: ${dest.navigatorName}")
                navigationView.setCheckedItem(dest.id)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _laporanListener.remove()
        notifFlag = false
    }


    companion object {
        private const val TAG = "AdminActivity"
        private const val CHANNEL_ID = "9987"
    }

}