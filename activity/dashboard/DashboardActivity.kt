package com.cloudin.monsterchicken.activity.dashboard

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.cloudin.monsterchicken.R
import com.cloudin.monsterchicken.commonclass.CloudInBaseActivity
import com.cloudin.monsterchicken.databinding.ActivityDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : CloudInBaseActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard)
        navView.setupWithNavController(navController)
    }
}