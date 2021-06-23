package com.machina.siband.admin

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.machina.siband.R
import com.machina.siband.databinding.ActivityAdminBinding

class AdminActivity: AppCompatActivity() {

    private var _binding: ActivityAdminBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initiateView()
        initiateDrawerLayout()
    }

    private fun initiateView() {
        _binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.activity_admin_nav_host) as NavHostFragment
        navController = navHostFragment.navController
        drawerLayout = binding.activityAdminDrawerLayout
        navigationView = binding.activityAdminDrawer
    }

    private fun initiateDrawerLayout() {
        val topLevelDestination: Set<Int> = setOf(R.id.adminSwipeLaporanFragment, R.id.adminListLantaiFragment, R.id.adminListUserFragment)
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


    companion object {
        private const val TAG = "AdminActivity"
    }

}