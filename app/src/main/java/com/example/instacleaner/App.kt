package com.example.instacleaner

import android.app.Application
import com.example.instacleaner.data.local.Account
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App:Application() {


    companion object {
        @Volatile
        private lateinit var instance: App
        fun getInstance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }


//    var currentAccount:Account? = null

}