package com.example.accountastock_android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.accountastock_android.databinding.ActivityMainBinding
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Define destinations where the BottomNavigationView will be displayed
        val bottomNavDestinations = setOf(
            R.id.navigation_account, R.id.navigation_inventory, R.id.navigation_user
        )

        // Setup ActionBar with NavController
        val appBarConfiguration = AppBarConfiguration(
            bottomNavDestinations
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Setup BottomNavigationView with NavController
        navView.setupWithNavController(navController)

        // Hide BottomNavigationView initially
        navView.visibility = View.GONE

        // Listen for destination changes
        val destinationChangedListener =
            NavController.OnDestinationChangedListener { _, destination, _ ->
                if (destination.id in bottomNavDestinations) {
                    // Show BottomNavigationView only on specified destinations
                    navView.visibility = View.VISIBLE
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                } else {
                    // Hide BottomNavigationView on other destinations
                    navView.visibility = View.GONE
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
            }
        navController.addOnDestinationChangedListener(destinationChangedListener)

        // Check if the user is authenticated
        if (!isUserAuthenticated()) {
            // Navigate to login
            navController.navigate(R.id.navigation_login)
        }
    }

    private fun isUserAuthenticated(): Boolean {
        // TODO: Implement real authentication check
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}