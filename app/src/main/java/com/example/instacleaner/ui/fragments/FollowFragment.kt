package com.example.instacleaner.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import com.example.instacleaner.R
import com.example.instacleaner.databinding.FragmentFollowBinding
import com.google.android.material.tabs.TabLayout

class FollowFragment:Fragment(R.layout.fragment_follow) {

    private  var _binding:FragmentFollowBinding? = null
    private val binding:FragmentFollowBinding
    get() = _binding!!



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DataBindingUtil.bind(view)!!
    }




}