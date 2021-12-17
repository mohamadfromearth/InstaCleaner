package com.example.instacleaner.ui.viewModels

import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instacleaner.App
import com.example.instacleaner.R
import com.example.instacleaner.data.local.DialogModel

import com.example.instacleaner.data.local.dialogModel.Tab
import com.example.instacleaner.data.remote.response.User
import com.example.instacleaner.data.remote.response.User.Companion.cloned
import com.example.instacleaner.repositories.InstaRepository
import com.example.instacleaner.utils.AccountManager
import com.example.instacleaner.utils.Resource
import com.example.instacleaner.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    private val accountManager: AccountManager,
    private val repository: InstaRepository,
    private val app:App,
    ) : ViewModel() {

    var shouldScroll = false
    private var isRequesting = false
    private var hasFilter = false
    private var followersLoadingVisibility = false
    private var followingLoadingVisibility = false
    private var maxId = ""
    private var currentAccount = accountManager.getCurrentAccount()

    var tabIndex = 0
    private val followerList = arrayListOf<User>()
    private val followingList = arrayListOf<User>()
    private var followerFilteredList = arrayListOf<User>()
    private var followingFilteredList = arrayListOf<User>()

    val loadingVisibility = ObservableInt(View.GONE)


    val adapterList = MutableLiveData<ArrayList<User>>()
    val showFilterDialog = SingleLiveEvent<Pair<String,ArrayList<DialogModel>>>()




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
       if (hasFilter.not()){
           if (tabIndex == 0) {

               adapterList.value = followerList

           } else {

               adapterList.value = followingList

           }
       }


    }


    fun userClickAction(user: User) {
        if (tabIndex == 0) {
            followerList
        } else {

        }
    }




    private fun followingDialogList() =
        arrayListOf(
            DialogModel(listOf(Tab(app.getString(R.string.private_)),Tab(app.getString(R.string.public_))),app.getString(R.string.by_status)),
            DialogModel(listOf(Tab(app.getString(R.string.no_pic)),Tab(app.getString(R.string.has_pic))),app.getString(R.string.by_profile_picture)),
            DialogModel(listOf(Tab(app.getString(R.string.verified_accounts)),Tab(app.getString(R.string.not_verified_accounts))),app.getString(R.string.by_verify)),
           // DialogModel(listOf(Tab(app.getString(R.string.verified_accounts)),Tab(app.getString(R.string.no_verified_account))),app.getString(R.string.by_verify)),
            DialogModel(listOf(Tab(app.getString(R.string.selected)),Tab(app.getString(R.string.not_selected))),app.getString(R.string.by_selection)),
            DialogModel(listOf(Tab(app.getString(R.string.they_following_back)),Tab(app.getString(R.string.they_not_following_back))),app.getString(R.string.by_followback)),
            DialogModel(listOf(Tab(app.getString(R.string.remove_filter))),app.getString(R.string.no_filter))

        )

    private fun followerDialogList() =
        arrayListOf(
            DialogModel(listOf(Tab(app.getString(R.string.public_)),Tab(app.getString(R.string.private_))),app.getString(R.string.by_status)),
            DialogModel(listOf(Tab(app.getString(R.string.no_pic)),Tab(app.getString(R.string.has_pic))),app.getString(R.string.by_profile_picture)),
            DialogModel(listOf(Tab(app.getString(R.string.verified_accounts)),Tab(app.getString(R.string.no_verified_account))),app.getString(R.string.by_verify)),
            DialogModel(listOf(Tab(app.getString(R.string.selected)),Tab(app.getString(R.string.not_selected))),app.getString(R.string.by_selection)),
            DialogModel(listOf(Tab(app.getString(R.string.i_following_back)),Tab(app.getString(R.string.i_not_following_back))),app.getString(R.string.by_followback)),
            DialogModel(listOf(Tab(app.getString(R.string.remove_filter))),app.getString(R.string.no_filter))
        )



    fun tabSelectAction(position: Int) {
        shouldScroll = true
        tabIndex = position
        setLoading()
        setList()


    }

    fun btnFilterAction() {
     if (tabIndex == 0) showFilterDialog.value =  Pair(app.getString(R.string.filter),followerDialogList()) else showFilterDialog.value = Pair(app.getString(R.string.filter),followingDialogList())
    }


    fun filter(dialogModel:DialogModel){
        val filterTabIndex = dialogModel.tabs.indexOf(dialogModel.tabs.find { it.isSelected })
        val currentList = if (filterTabIndex == 0) followerList else followingList

        when(dialogModel.title){
            app.getString(R.string.by_status) -> {
                when(filterTabIndex){
                    0 ->{


                    }
                    1 ->{

                    }
                }

            }
            app.getString(R.string.by_profile_picture) -> {
                when(filterTabIndex){
                    0 ->{

                    }
                    1 ->{

                    }
                }

            }
            app.getString(R.string.by_verify) -> {
                when(filterTabIndex){
                    0 ->{

                    }
                    1 ->{

                    }
                }

            }
            app.getString(R.string.by_selection) -> {
                when(filterTabIndex){
                    0 ->{

                    }
                    1 ->{

                    }
                }

            }
            app.getString(R.string.by_followback) -> {
                when(filterTabIndex){
                    0 ->{

                    }
                    1 ->{

                    }
                }

            }

        }

    }


}