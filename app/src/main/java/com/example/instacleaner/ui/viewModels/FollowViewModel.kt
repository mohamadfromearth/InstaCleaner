package com.example.instacleaner.ui.viewModels

import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instacleaner.App
import com.example.instacleaner.data.remote.response.User
import com.example.instacleaner.data.remote.response.User.Companion.cloned
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

    var shouldScroll = false
    private var isRequesting = false
    private var followersLoadingVisibility = false
    private var followingLoadingVisibility = false
    private var maxId = ""
    private var currentAccount = accountManager.getCurrentAccount()

    var tabIndex = 0
    private val followerList = arrayListOf<User>()
    private val followingList = arrayListOf<User>()

    val loadingVisibility = ObservableInt(View.GONE)


    val adapterList = MutableLiveData<ArrayList<User>>()




    init {
        getFollowers()
         getFollowings()
    }

    fun onStart(){
        if (currentAccount.user.pk != accountManager.getCurrentAccount().user.pk){
            currentAccount = accountManager.getCurrentAccount()
            followerList.clear()
            followingList.clear()
            getFollowings()
            getFollowers()
        }
    }



    fun onItemClickAction(pos:Int,user:User){
           if (tabIndex == 0){
               followerList[pos].isSelected.not()
               adapterList.value = followerList.cloned()
           }else{
               followingList[pos].isSelected.not()
               adapterList.value = followingList.cloned()
           }
    }


    fun paginate() {
         if (tabIndex == 0 && isRequesting.not() && maxId.isNotEmpty()){
             getFollowers()
         }
    }

    fun getFollowers() = viewModelScope.launch {
        followersLoadingVisibility = true
        isRequesting = true
        setLoading()
        when (val result = repository.getFollower(accountManager.getCurrentAccount(), maxId)) {
            is Resource.Success -> {
                isRequesting = false
                followersLoadingVisibility = false
                followerList.addAll(result.data!!.users)
                // log("max_idh: ${result.data.next_max_id}")
                maxId = result.data.next_max_id ?: ""
                setLoading()
                setList()
            }
            is Resource.Error -> {
                isRequesting = false
                followersLoadingVisibility = false
            }
        }
    }


     fun getFollowings() = viewModelScope.launch {
        followingLoadingVisibility = true
        setLoading()
        when (val result = repository.getFollowing(accountManager.getCurrentAccount())) {
            is Resource.Success -> {
                followingLoadingVisibility = false
                followingList.addAll(result.data!!.users)
                setList()
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

    private fun setList() {
        if (tabIndex == 0) {

            adapterList.value = followerList

        } else {

            adapterList.value = followingList

        }

    }


    fun userClickAction(user: User) {
        if (tabIndex == 0) {
            followerList
        } else {

        }
    }

    fun tabSelectAction(position: Int) {
        shouldScroll = true
        tabIndex = position
        setLoading()
        setList()


    }

}