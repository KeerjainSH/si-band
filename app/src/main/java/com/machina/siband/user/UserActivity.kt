package com.machina.siband.user

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.machina.siband.R
import com.machina.siband.databinding.ActivityUserBinding
import com.machina.siband.user.view.UserHomeFragmentDirections

class UserActivity: AppCompatActivity() {

    private var _binding: ActivityUserBinding? = null
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
        _binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.activity_user_nav_host) as NavHostFragment
        navController = navHostFragment.navController
        drawerLayout = binding.activityUserDrawerLayout
        navigationView = binding.activityUserDrawer
    }

    private fun initiateDrawerLayout() {
        // Create set for Top-Level destination in DrawerLayout
        val topLevelDestinations: Set<Int> = setOf(R.id.userHomeFragment, R.id.swipeLaporanFragment, R.id.formPelaporanFragment)
        appBarConfiguration = AppBarConfiguration
                .Builder(topLevelDestinations)
                .setOpenableLayout(drawerLayout)
                .build()

        // Bind action bar to change based on destination that opened
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        // Set listener to NavigationView item
        navigationView.setCheckedItem(R.id.userHomeFragment)
        navigationView.setNavigationItemSelectedListener { dest ->
            when (dest.itemId) {
                R.id.userLogoutOption -> { logOut() }
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
        // This navController enable drawer layout for multiple top level destinations
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
        private const val TAG = "UserActivity"
    }
}