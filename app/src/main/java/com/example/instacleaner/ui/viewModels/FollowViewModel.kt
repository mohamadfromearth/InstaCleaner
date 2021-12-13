package com.example.instacleaner.ui.viewModels

import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instacleaner.App
import com.example.instacleaner.data.remote.response.User
import com.example.instacleaner.data.remote.response.userFollowers.UserList
import com.example.instacleaner.repositories.InstaRepository
import com.example.instacleaner.utils.AccountManager
import com.example.instacleaner.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    private val accountManager: AccountManager,
    private val repository: InstaRepository,
    ) : ViewModel() {

    private var followersLoadingVisibility = false
    private var followingLoadingVisibility = false
    private var tabIndex = 0
    private val followerList = arrayListOf<User>()
    private val followingList = arrayListOf<User>()

    val loadingVisibility = ObservableInt(View.GONE)


    val adapterList = MutableLiveData<Pair<ArrayList<User>,Boolean>>()
    private var maxId = ""



    init {
        getFollowers()
        // getFollowings()
    }


    fun paginate() {
         if (tabIndex == 0){
             getFollowers()
         }
    }

    fun getFollowers() = viewModelScope.launch {
        followersLoadingVisibility = true
        setLoading()
        when (val result = repository.getFollower(accountManager.getCurrentAccount(), maxId)) {
            is Resource.Success -> {
                followersLoadingVisibility = false
                followerList.users.addAll(result.data!!.users)
                // log("max_idh: ${result.data.next_max_id}")
                maxId = result.data.next_max_id ?: ""
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
        when (val result = repository.getFollowing(accountManager.getCurrentAccount())) {
            is Resource.Success -> {
                followingLoadingVisibility = false
                followingList.users.addAll(result.data!!.users)
                setList(false)
                setLoading()
            }
            is Resource.Error -> {

            }
        }
    }

    private fun setLoading() {
        if (tabIndex == 0) {
            loadingVisibility.set(if (followersLoadingVisibility) View.VISIBLE else View.GONE)
        } else {
            loadingVisibility.set(if (followingLoadingVisibility) View.VISIBLE else View.GONE)
        }
    }

    private fun setList(returnToTop : Boolean) {
        if (tabIndex == 0) {
            adapterList.value = followerList to returnToTop
        } else {
            adapterList.value = followingList to returnToTop
        }
    }


    fun userClickAction(user: User) {
        if (tabIndex == 0) {
            followerList
        } else {

        }
    }

    fun tabSelectAction(position: Int) {
        tabIndex = position
        setLoading()
        setList(true)

    }

}