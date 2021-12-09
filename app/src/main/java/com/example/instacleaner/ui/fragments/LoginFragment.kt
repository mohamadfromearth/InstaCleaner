package com.example.instacleaner.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.instacleaner.R
import com.example.instacleaner.databinding.FragmentLoginBinding
import com.example.instacleaner.ui.viewModels.LoginViewModel
import com.example.instacleaner.utils.Constance.INSTAGRAM_URL
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment:Fragment(R.layout.fragment_login) {

    private val viewModel:LoginViewModel by viewModels()

    private var _binding:FragmentLoginBinding? = null
    private val binding:FragmentLoginBinding
    get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
         binding.viewModel = viewModel
        setWebView()
    }

    private fun subscribeToObservers(){
        viewModel.navToHome.observe(viewLifecycleOwner,{

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