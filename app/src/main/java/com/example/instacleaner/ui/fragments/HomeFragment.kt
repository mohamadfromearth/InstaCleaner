package com.example.instacleaner.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.instacleaner.R
import com.example.instacleaner.adapters.AccountAdapter
import com.example.instacleaner.data.local.Account.Companion.cloned
import com.example.instacleaner.databinding.FragmentHomeBinding
import com.example.instacleaner.ui.dialog.LogoutDialog
import com.example.instacleaner.ui.viewModels.HomeViewModel
import com.example.instacleaner.utils.Constance
import dagger.hilt.android.AndroidEntryPoint



//first get account from sharePref  then if exists getUserInfo from insta api and set values into the ui
//if doesn't exist navigate to login fragment


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var accountAdapter: AccountAdapter
    private lateinit var nav: NavController
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DataBindingUtil.bind(view)
        binding.viewModel = viewModel
        nav = findNavController()
        setUpRecyclerView()
        subscribeToObservers()


    }


    private fun subscribeToObservers() {
        viewModel.navigateToLogin.observe(viewLifecycleOwner, {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragment2ToLoginFragment())
        })
        viewModel.accounts.observe(viewLifecycleOwner, {
                binding.rvAccount.recycledViewPool.clear()
            setUpRecyclerView()
            accountAdapter.submitList(it.cloned())
        })

        nav.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(Constance.PREF_USER_INDEX)
            ?.observe(viewLifecycleOwner) {
                if(it){
                viewModel.login()
                }
            }


        viewModel.exit.observe(viewLifecycleOwner,{
           requireActivity().finishAffinity()
        })
        viewModel.dialogShow.observe(viewLifecycleOwner,{
           LogoutDialog(){
               viewModel.approveLogout()
           }.show(childFragmentManager,"")
        })



    }


    private fun setUpRecyclerView() {
        accountAdapter = AccountAdapter({ position, account ->
            viewModel.accountClick(account, position)
        }) {
            viewModel.addAccountClick()
        }
        binding.rvAccount.adapter = accountAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}