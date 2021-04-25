package com.machina.siband.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.machina.siband.R
import com.machina.siband.databinding.ActivityUserBinding

class UserActivity: AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initiateView()
        initiateDrawerLayout()

    }



    private fun initiateDrawerLayout() {
        // Create set for Top-Level destination in DrawerLayout
        val topLevelDestinations: Set<Int> = setOf(R.id.userHomeFragment, R.id.userComplaintFragment)
        appBarConfiguration = AppBarConfiguration
                .Builder(topLevelDestinations)
                .setOpenableLayout(binding.activityUserDrawerLayout)
                .build()

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        findViewById<NavigationView>(R.id.activity_user_drawer).setupWithNavController(navController)
    }

    private fun initiateView() {
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.activity_user_nav_host) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)||
                super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (binding.activityUserDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.activityUserDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}