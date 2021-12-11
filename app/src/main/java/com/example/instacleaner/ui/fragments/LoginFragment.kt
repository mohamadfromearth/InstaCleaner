package com.example.instacleaner.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.instacleaner.R
import com.example.instacleaner.databinding.FragmentLoginBinding
import com.example.instacleaner.ui.viewModels.LoginViewModel
import com.example.instacleaner.utils.Constance.INSTAGRAM_URL
import com.example.instacleaner.utils.Constance.PREF_USER_INDEX
import com.example.instacleaner.utils.showToast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment:Fragment(R.layout.fragment_login) {

    private val viewModel:LoginViewModel by viewModels()

    private var _binding:FragmentLoginBinding? = null
    private val binding:FragmentLoginBinding
    get() = _binding!!


    private lateinit var nav:NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
         binding.viewModel = viewModel

        nav = findNavController()
        setWebView()
        subscribeToObservers()




    }

    private fun subscribeToObservers(){
        viewModel.navToHome.observe(viewLifecycleOwner,{
            nav.previousBackStackEntry?.savedStateHandle?.set(PREF_USER_INDEX, true)
            nav.popBackStack()
        })
        viewModel.invalidCookie.observe(viewLifecycleOwner, {
            showToast(it)
        })
        viewModel.popToHome.observe(viewLifecycleOwner,{
            nav.popBackStack()
        })

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebView() = binding.wbInsta.apply {
           loadUrl(INSTAGRAM_URL)
           settings.javaScriptEnabled = true
           webViewClient = webViewClient()
    }

    private fun webViewClient():WebViewClient{
        return object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                viewModel.validateCookie(url)
            }
        }
    }


}