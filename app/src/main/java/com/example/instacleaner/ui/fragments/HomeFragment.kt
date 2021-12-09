package com.example.instacleaner.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.instacleaner.R
import dagger.hilt.android.AndroidEntryPoint


//first get account from sharePref  then if exists getUserInfo from insta api and set values into the ui
//if doesn't exist navigate to login fragment





class HomeFragment:Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }



}