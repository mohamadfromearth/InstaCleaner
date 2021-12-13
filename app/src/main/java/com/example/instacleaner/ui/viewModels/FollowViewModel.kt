package com.example.instacleaner.ui.viewModels

import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instacleaner.data.remote.response.User
import com.example.instacleaner.repositories.InstaRepository
import com.example.instacleaner.utils.AccountManager
import com.example.instacleaner.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    private val accountManager: AccountManager,
    private val repository: InstaRepository
) : ViewModel() {

    private var followersLoadingVisibility = false
    private var followingLoadingVisibility = false
    private var tabIndex = 0

    val loadingVisibility = ObservableInt(View.GONE)
    private val followerList = ArrayList<User>()
    private val followingList = ArrayList<User>()


    val adapterList = MutableLiveData<List<User>>()


    init {
        getFollowers()
        getFollowings()
    }


    private fun getFollowers() = viewModelScope.launch {
        followersLoadingVisibility = true
        setLoading()
        when (val result = repository.getFollower(accountManager.getCurrentAccount())) {
            is Resource.Success -> {
                followersLoadingVisibility = false
                followerList.addAll(result.data!!.users)
                setLoading()
                setList()
            }
            is Resource.Error -> {
                followersLoadingVisibility = false
            }
        }
    }





    private fun getFollowings() = viewModelScope.launch {
        followingLoadingVisibility = true
        setLoading()
        when(val result = repository.getFollowing(accountManager.getCurrentAccount())){
            is Resource.Success -> {
                followingLoadingVisibility = false
                followingList.addAll(result.data!!.users)
                setList()
                setLoading()
               // tabSelectAction(tabIndex)
            }
            is Resource.Error -> {

            }
        }
    }

    private fun setLoading(){
        if (tabIndex == 0) {
            loadingVisibility.set(if (followersLoadingVisibility) View.VISIBLE else View.GONE )
        }else{
            loadingVisibility.set(if (followingLoadingVisibility) View.VISIBLE else View.GONE )
        }
    }

    private fun setList(){
        if (tabIndex == 0){
            adapterList.value = followerList
        }else{
            adapterList.value = followingList
        }
    }


    fun tabSelectAction(position:Int){
        tabIndex = position
        setLoading()
        setList()


    }

}