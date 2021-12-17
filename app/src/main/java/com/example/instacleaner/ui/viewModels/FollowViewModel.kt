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
    private var hasFollowerFilter = false
    private var hasFollowingFilter = false
    private var followerCurrentFilterTitle = app.getString(R.string.no_filter)
    private var followingCurrentFilterTitle = app.getString(R.string.no_filter)
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
             val currentList = adapterList.value!!.cloned()
               currentList[pos].isSelected = currentList[pos].isSelected.not()
               adapterList.value = currentList
           }else{
               val currentList = adapterList.value!!.cloned()
               currentList[pos].isSelected = currentList[pos].isSelected.not()
               adapterList.value = currentList
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
             if (hasFollowerFilter.not()) adapterList.value = followerList else {
                 adapterList.value = followerFilteredList
             }


           } else {

              if (hasFollowingFilter.not()) adapterList.value = followingList else{
                  adapterList.value = followingFilteredList
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
            DialogModel(listOf(Tab(app.getString(R.string.public_)),Tab(app.getString(R.string.private_))),app.getString(R.string.by_status)),
            DialogModel(listOf(Tab(app.getString(R.string.no_pic)),Tab(app.getString(R.string.has_pic))),app.getString(R.string.by_profile_picture)),
            DialogModel(listOf(Tab(app.getString(R.string.verified_accounts)),Tab(app.getString(R.string.not_verified_accounts))),app.getString(R.string.by_verify)),
           // DialogModel(listOf(Tab(app.getString(R.string.verified_accounts)),Tab(app.getString(R.string.no_verified_account))),app.getString(R.string.by_verify)),
            DialogModel(listOf(Tab(app.getString(R.string.selected)),Tab(app.getString(R.string.not_selected))),app.getString(R.string.by_selection)),
            DialogModel(listOf(Tab(app.getString(R.string.they_following_back)),Tab(app.getString(R.string.they_not_following_back))),app.getString(R.string.by_followback)),
            DialogModel(listOf(Tab(app.getString(R.string.remove_filter))),app.getString(R.string.no_filter),true)

        )

    private fun followerDialogList() =
        arrayListOf(
            DialogModel(listOf(Tab(app.getString(R.string.public_)),Tab(app.getString(R.string.private_))),app.getString(R.string.by_status)),
            DialogModel(listOf(Tab(app.getString(R.string.no_pic)),Tab(app.getString(R.string.has_pic))),app.getString(R.string.by_profile_picture)),
            DialogModel(listOf(Tab(app.getString(R.string.verified_accounts)),Tab(app.getString(R.string.not_verified_accounts))),app.getString(R.string.by_verify)),
            DialogModel(listOf(Tab(app.getString(R.string.selected)),Tab(app.getString(R.string.not_selected))),app.getString(R.string.by_selection)),
            DialogModel(listOf(Tab(app.getString(R.string.i_following_back)),Tab(app.getString(R.string.i_not_following_back))),app.getString(R.string.by_followback)),
            DialogModel(listOf(Tab(app.getString(R.string.remove_filter))),app.getString(R.string.no_filter),true)
        )



    fun tabSelectAction(position: Int) {
        shouldScroll = true
        tabIndex = position
        setLoading()
        setList()


    }

    fun btnFilterAction() {
        if (tabIndex == 0) {
            if (hasFollowerFilter){
                val dialogModels = followerDialogList()
                dialogModels[dialogModels.size -1].isSelected = false
                dialogModels.first { it.title == followerCurrentFilterTitle }.isSelected = true
                showFilterDialog.value = Pair(app.getString(R.string.filter),dialogModels)
            }else{
                showFilterDialog.value =  Pair(app.getString(R.string.filter),followerDialogList())
            }
        } else{
            if (hasFollowingFilter){
                val dialogModels = followingDialogList()
                dialogModels[dialogModels.size - 1].isSelected = false
                dialogModels.first { it.title == followingCurrentFilterTitle }.isSelected = true
                showFilterDialog.value = Pair(app.getString(R.string.filter),dialogModels)
            }else{
                showFilterDialog.value = Pair(app.getString(R.string.filter),followingDialogList())
            }

        }
    }

    fun filter(dialogModel:DialogModel){
        val filterTabIndex = dialogModel.tabs.indexOf(dialogModel.tabs.find { it.isSelected })

        when(dialogModel.title){
            app.getString(R.string.by_status) -> {

                when(filterTabIndex){
                    0 ->{
                   if (tabIndex == 0) {
                       followerCurrentFilterTitle = dialogModel.title
                       followerFilteredList  = followerList.filter { it.is_private.not() }.cloned()
                       adapterList.value = followerFilteredList
                       hasFollowerFilter = true
                   }
                   else {
                       followingCurrentFilterTitle = dialogModel.title
                       followingFilteredList = followerList.filter { it.is_private.not() }.cloned()
                       adapterList.value =  followingFilteredList
                       hasFollowingFilter = true
                   }
                    }
                    1 ->{
                        if (tabIndex == 0) {
                            followerCurrentFilterTitle = dialogModel.title
                            followerFilteredList  = followerList.filter { it.is_private }.cloned()
                            adapterList.value = followerFilteredList
                            hasFollowerFilter = true
                        }
                        else {
                            followingCurrentFilterTitle = dialogModel.title
                            followingFilteredList = followingList.filter { it.is_private }.cloned()
                            adapterList.value =  followingFilteredList
                            hasFollowingFilter = true
                        }
                    }
                }

            }
            app.getString(R.string.by_profile_picture) -> {

                when(filterTabIndex){
                    0 ->{
                     if (tabIndex == 0){
                         followerCurrentFilterTitle = dialogModel.title
                       followerFilteredList = followerList.filter { it.has_anonymous_profile_picture }.cloned()
                       adapterList.value = followerFilteredList
                       hasFollowerFilter = true
                     }else{
                         followingCurrentFilterTitle = dialogModel.title
                         followingFilteredList = followingList.filter { it.has_anonymous_profile_picture }.cloned()
                         adapterList.value = followingFilteredList
                         hasFollowingFilter = true
                     }
                    }
                    1 ->{
                        if (tabIndex == 0){
                            followerCurrentFilterTitle = dialogModel.title
                            followerFilteredList = followerList.filter { it.has_anonymous_profile_picture.not() }.cloned()
                            adapterList.value = followerFilteredList
                            hasFollowerFilter = true
                        }else{
                            followingCurrentFilterTitle = dialogModel.title
                            followingFilteredList = followingList.filter { it.has_anonymous_profile_picture.not() }.cloned()
                            adapterList.value = followingFilteredList
                            hasFollowingFilter = true
                        }

                    }
                }

            }
            app.getString(R.string.by_verify) -> {
                when(filterTabIndex){
                    0 ->{
                        if (tabIndex == 0){
                            followerCurrentFilterTitle = dialogModel.title
                            followerFilteredList = followerList.filter { it.is_verified }.cloned()
                            adapterList.value = followerFilteredList
                            hasFollowerFilter = true
                        }else{
                            followingCurrentFilterTitle = dialogModel.title
                            followingFilteredList = followingList.filter { it.is_verified }.cloned()
                            adapterList.value = followingFilteredList
                            hasFollowingFilter = true
                        }

                    }
                    1 ->{
                        if (tabIndex == 0){
                            followerCurrentFilterTitle = dialogModel.title
                            followerFilteredList = followerList.filter { it.is_verified.not() }.cloned()
                            adapterList.value = followerFilteredList
                            hasFollowerFilter = true
                        }else{
                            followingCurrentFilterTitle = dialogModel.title
                            followingFilteredList = followingList.filter { it.is_verified.not() }.cloned()
                            adapterList.value = followingFilteredList
                            hasFollowingFilter = true
                        }

                    }
                }

            }
            app.getString(R.string.by_selection) -> {
                when(filterTabIndex){
                    0 ->{
                        if (tabIndex == 0){
                            followerCurrentFilterTitle = dialogModel.title
                            val currentList = adapterList.value!!.filter { it.isSelected }.cloned()
                            followerFilteredList = currentList
                            adapterList.value = currentList
                            hasFollowerFilter = true
                        }else{
                            followingCurrentFilterTitle = dialogModel.title
                            val currentList = adapterList.value!!.filter { it.isSelected }.cloned()
                            followingFilteredList = currentList
                            adapterList.value = currentList
                            hasFollowingFilter = true
                        }

                    }
                    1 ->{
                        if (tabIndex == 0){
                            followerCurrentFilterTitle = dialogModel.title
                            val currentList = adapterList.value!!.filter { it.isSelected.not() }.cloned()
                            followerFilteredList = currentList
                            adapterList.value = currentList
                            hasFollowerFilter = true
                        }else{
                            followingCurrentFilterTitle = dialogModel.title
                            val currentList = adapterList.value!!.filter { it.isSelected.not() }.cloned()
                            followingFilteredList = currentList
                            adapterList.value = currentList
                            hasFollowingFilter = true
                        }

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

            else -> {


                when(tabIndex){
                    0 ->{
                        followerCurrentFilterTitle = app.getString(R.string.no_filter)
                        adapterList.value = followerList
                        hasFollowerFilter = false
                    }
                    1 ->{
                        followingCurrentFilterTitle = app.getString(R.string.no_filter)
                        adapterList.value = followingList
                        hasFollowingFilter = false
                    }
                }
            }

        }

    }


}