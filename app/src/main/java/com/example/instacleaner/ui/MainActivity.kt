package com.example.instacleaner.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.instacleaner.R
import com.example.instacleaner.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var navController:NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        navController =findNavController(R.id.navHost)
        binding.bottomNav.setupWithNavController(navController)
        setBottomNavVisibilityState()
    }

    private fun setBottomNavVisibilityState(){
        navController.addOnDestinationChangedListener{_,destination,_ ->
            when(destination.id) {
                R.id.loginFragment -> binding.bottomNav.isVisible =false
                else -> binding.bottomNav.isVisible = true
            }

        }
    }

}