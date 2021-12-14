package com.example.instacleaner.ui.viewModels


import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instacleaner.data.local.Account
import com.example.instacleaner.data.remote.response.User
import com.example.instacleaner.repositories.InstaRepository
import com.example.instacleaner.utils.AccountManager
import com.example.instacleaner.utils.Resource
import com.example.instacleaner.utils.SingleLiveEvent
import com.example.instacleaner.utils.translateNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountManager: AccountManager,
    private val repository: InstaRepository
) : ViewModel() {

    val loadingVisibility = ObservableInt(View.GONE)
    val userInfo = ObservableField<User>()
    val followerCount = ObservableField("-")
    val followingCount = ObservableField("-")
    val postCount = ObservableField("-")

    val navigateToLogin = SingleLiveEvent<Boolean>()
    val dialogShow = SingleLiveEvent<Boolean>()
    val exit = SingleLiveEvent<Boolean>()
    val accounts = MutableLiveData<ArrayList<Account>>()


    val errorMessage = SingleLiveEvent<String>()

    init {


        if (accountManager.isLogin()) getUserInfo(accountManager.getCurrentAccount()) else navigateToLogin.value =
            true


    }

    fun login() {
        if (accountManager.getAccounts().isEmpty()) {
            exit.value = true
        } else {
            getUserInfo(accountManager.getCurrentAccount())
        }
    }

    private fun getUserInfo(account: Account) = viewModelScope.launch {
//        if (account == null) accountManager.refreshSharePreferenceValue()
//        val acc = account ?: getAccount()
        loadingVisibility.set(View.VISIBLE)
        when (val result = repository.getUserInfo(account)) {
            is Resource.Success -> {
                result.data?.let {
                    loadingVisibility.set(View.GONE)
                    userInfo.set(it.user)
                    followerCount.set(it.user.follower_count.translateNumber())
                    followingCount.set(it.user.following_count.translateNumber())
                    postCount.set(it.user.media_count.translateNumber())
                    accountManager.updateAccount(it.user) { accountList ->
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


    fun accountClick(account: Account, position: Int) {
        if (accountManager.getCurrentAccount() == account) return
        getUserInfo(account)
    }

    fun addAccountClick() {
        navigateToLogin.value = true
    }

    fun logoutClick() {
        dialogShow.value = true
    }

    fun approveLogout() {
        accountManager.removeAccount().apply {
            if (size != 0) getUserInfo(accountManager.getCurrentAccount())
            else navigateToLogin.value = true
        }


    }


}