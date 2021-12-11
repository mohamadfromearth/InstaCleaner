package com.example.instacleaner.ui.viewModels


import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instacleaner.data.local.Account
import com.example.instacleaner.repositories.InstaRepository
import com.example.instacleaner.utils.AccountManager
import com.example.instacleaner.utils.Resource
import com.example.instacleaner.utils.SingleLiveEvent
import com.example.instacleaner.utils.translateNumber
import com.example.mohamadkh_instacleaner.data.remote.response.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject






@HiltViewModel
class HomeViewModel@Inject constructor(
    private val accountManager:AccountManager,
    private val repository:InstaRepository
):ViewModel() {


    val loadingVisibility = ObservableInt(View.GONE)
    val userInfo = ObservableField<User>()
    val followerCount = ObservableField("-")

    val navigateToLogin = SingleLiveEvent<Boolean>()
    val accounts = MutableLiveData<ArrayList<Account>>()

     val errorMessage = SingleLiveEvent<String>()

    init {
        if (!accountManager.isLogin()) navigateToLogin.value  = true  else getUserInfo()


    }


     fun getUserInfo(account: Account? = null) = viewModelScope.launch {
         val acc = account ?: getAccount()
        loadingVisibility.set(View.VISIBLE)
        when(val result = repository.getUserInfo(acc!!)){
            is Resource.Success -> {
                result.data?.let {
                    loadingVisibility.set(View.GONE)
                    userInfo.set(it.user)
                    followerCount.set(it.user.follower_count.translateNumber())
                    accountManager.updateAccount(it.user,acc){ accountList ->
                        accounts.value = accountList

                    }
                }

            }
            is Resource.Error -> {
                loadingVisibility.set(View.GONE)
                errorMessage.value = result.message!!
            }
        }
    }


    fun onAccountClick(account: Account,position:Int){
        getUserInfo(account)
    }

    fun onAddAccountClick(){
        navigateToLogin.value =  true
    }


    private fun getAccount() = accountManager.getCurrentAccount()






}