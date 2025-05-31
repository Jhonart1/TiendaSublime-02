package com.sargames.tiendasublime

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sargames.tiendasublime.data.DatabaseHelper
import com.sargames.tiendasublime.data.SharedPreferencesManager

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPrefs: SharedPreferencesManager
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPrefs = SharedPreferencesManager(this)
        dbHelper = DatabaseHelper(this)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController
        
        val navView: BottomNavigationView = findViewById(R.id.bottomNavigation)
        navView.setupWithNavController(navController)

        // Mostrar opción de gestión de productos solo para el administrador
        val userEmail = sharedPrefs.getUserEmail()
        if (userEmail != null && dbHelper.isAdmin(userEmail)) {
            navView.menu.findItem(R.id.navigation_product_management).isVisible = true
        }
    }
}