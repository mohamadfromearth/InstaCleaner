package com.example.instacleaner

import android.app.Application
import com.example.instacleaner.data.local.Account
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App:Application() {

    var currentAccount:Account? = null

}