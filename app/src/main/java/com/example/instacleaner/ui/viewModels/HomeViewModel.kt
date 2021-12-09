package com.example.instacleaner.ui.viewModels

import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instacleaner.data.local.Account
import com.example.instacleaner.repositories.InstaRepository
import com.example.instacleaner.utils.Constance.IS_LOGIN
import com.example.instacleaner.utils.PreferenceManager
import com.example.instacleaner.utils.Resource
import com.example.instacleaner.utils.SingleLiveEvent
import com.example.mohamadkh_instacleaner.data.remote.response.User
import com.example.mohamadkh_instacleaner.data.remote.response.userInfo.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Error
import javax.inject.Inject






@HiltViewModel
class HomeViewModel@Inject constructor(
 private val preferenceManager:PreferenceManager,
 private val repository:InstaRepository
):ViewModel() {
    private val isLogin =  preferenceManager.getBoolean(IS_LOGIN)

    val loadingVisibility = ObservableInt(View.GONE)



    val navigateToLogin = MutableLiveData<Boolean>()

    val userInfo = MutableLiveData<User>()

    val errorMessage = SingleLiveEvent<String>()

    init {
        if (!isLogin) navigateToLoginFragment()
        else
            getUserInfo()
    }


    private fun getUserInfo(account: Account) = viewModelScope.launch {
        val result = repository.getUserInfo(account)
        when(result){
            is Resource.Success -> userInfo.value = result.data?.user
            is Resource.Error -> errorMessage.value = result.message!!
        }
    }

    private fun getAccount(){
        
    }


    private fun navigateToLoginFragment(){
        navigateToLogin.value = true
    }



}