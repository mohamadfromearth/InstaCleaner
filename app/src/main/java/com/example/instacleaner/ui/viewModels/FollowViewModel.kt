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


    private var followerFilter : DialogModel.FilterType = DialogModel.FilterType.NoFilter
    private var followingFilter : DialogModel.FilterType = DialogModel.FilterType.NoFilter

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
               val selectValue = currentList[pos].isSelected.not()
                currentList[pos].isSelected = selectValue
              followerList.first {it.username ==currentList[pos].username }.isSelected = selectValue
               adapterList.value = currentList
           }else{
               val currentList = adapterList.value!!.cloned()
               val selectValue = currentList[pos].isSelected.not()
               currentList[pos].isSelected = selectValue
               followingList.first { it.pk == currentList[pos].pk }.isSelected = selectValue
               adapterList.value = currentList
           }
    }


    fun paginate() {
         if (tabIndex == 0 && isRequesting.not() && maxId.isNotEmpty() && hasFollowerFilter.not()){
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
                val list = arrayListOf<User>()
                followerList.forEach { user->
                    followingList.firstOrNull { it.pk == user.pk }?.let {
                        list.add(it)
                    }
                }
                list.isEmpty()

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

    fun setFilter(filterType:DialogModel.FilterType){
        shouldScroll = true
        if (tabIndex == 0){
            followerFilter = filterType
        }else{
            followingFilter = filterType
        }
        setList()
        shouldScroll = false
    }

     private fun setList() {
        if (tabIndex == 0) {
            adapterList.value = filter(followerList,followerFilter)
        }
        else {
            adapterList.value = filter(followingList,followingFilter)
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
            DialogModel(listOf(Tab(app.getString(R.string.public_)),Tab(app.getString(R.string.private_))),app.getString(R.string.by_status),DialogModel.FilterType.Status(false)),
            DialogModel(listOf(Tab(app.getString(R.string.no_pic)),Tab(app.getString(R.string.has_pic))),app.getString(R.string.by_profile_picture),DialogModel.FilterType.Avatar(false)),
            DialogModel(listOf(Tab(app.getString(R.string.verified_accounts)),Tab(app.getString(R.string.not_verified_accounts))),app.getString(R.string.by_verify),DialogModel.FilterType.Verify(false)),
           // DialogModel(listOf(Tab(app.getString(R.string.verified_accounts)),Tab(app.getString(R.string.no_verified_account))),app.getString(R.string.by_verify)),
            DialogModel(listOf(Tab(app.getString(R.string.selected)),Tab(app.getString(R.string.not_selected))),app.getString(R.string.by_selection),DialogModel.FilterType.Select(true)),
            DialogModel(listOf(Tab(app.getString(R.string.they_following_back)),Tab(app.getString(R.string.they_not_following_back))),app.getString(R.string.by_followback),DialogModel.FilterType.FollowBack(false)),
            DialogModel(listOf(Tab(app.getString(R.string.remove_filter))),app.getString(R.string.no_filter),DialogModel.FilterType.NoFilter)

        )

    private fun followerDialogList() =
        arrayListOf(
    DialogModel(listOf(Tab(app.getString(R.string.public_)),Tab(app.getString(R.string.private_))),app.getString(R.string.by_status),DialogModel.FilterType.Status(false)),
    DialogModel(listOf(Tab(app.getString(R.string.has_pic)),Tab(app.getString(R.string.no_pic))),app.getString(R.string.by_profile_picture),DialogModel.FilterType.Avatar(false)),
    DialogModel(listOf(Tab(app.getString(R.string.verified_accounts)),Tab(app.getString(R.string.not_verified_accounts))),app.getString(R.string.by_verify),DialogModel.FilterType.Verify(false)),
            DialogModel(listOf(Tab(app.getString(R.string.selected)),Tab(app.getString(R.string.not_selected))),app.getString(R.string.by_selection),DialogModel.FilterType.Select(true)),
    DialogModel(listOf(Tab(app.getString(R.string.i_following_back)),Tab(app.getString(R.string.i_not_following_back))),app.getString(R.string.by_followback),DialogModel.FilterType.FollowBack(false)),
    DialogModel(listOf(Tab(app.getString(R.string.remove_filter))),app.getString(R.string.no_filter),DialogModel.FilterType.NoFilter)

    )



    fun tabSelectAction(position: Int) {
        shouldScroll = true
        tabIndex = position
        setLoading()
        setList()


    }

    fun btnFilterAction() {
        val dialogModels = arrayListOf<DialogModel>()
        if (tabIndex == 0){
            dialogModels.addAll(followerDialogList())
            dialogModels.first { it.filter.javaClass.name == followerFilter.javaClass.name }.isSelected = true
        }else{
            dialogModels.addAll(followingDialogList())
            dialogModels.first { it.filter.javaClass.name == followingFilter.javaClass.name }.isSelected = true
        }
            showFilterDialog.value = Pair(app.getString(R.string.filter),dialogModels)

    }

   private  fun filter(list : ArrayList<User>, filter : DialogModel.FilterType) : ArrayList<User>{
        if (tabIndex == 0) followerFilter = filter else followingFilter = filter
        when(filter){
            is DialogModel.FilterType.Status -> {
                return if (filter.isPrivate){
                    list.filter { it.is_private }.cloned()
                }else{

                    list.filter { !it.is_private.not() }.cloned()
                }
            }

            is DialogModel.FilterType.Avatar -> {
                return if(filter.hasAvatar){
                    list.filter { it.has_anonymous_profile_picture.not() }.cloned()
                }else{
                    list.filter { it.has_anonymous_profile_picture }.cloned()
                }
            }

            is DialogModel.FilterType.Verify -> {
                return if (filter.isVerify){
                    list.filter { it.is_verified }.cloned()
                }else{
                    list.filter { it.is_verified.not() }.cloned()
                }
            }

            is DialogModel.FilterType.Select -> {
                return if (filter.isSelected){
                    list.filter { it.isSelected }.cloned()
                 }else{
                     list.filter { it.isSelected.not() }.cloned()
                 }
            }

            is DialogModel.FilterType.FollowBack -> {
                var followBackList = ArrayList<User>()
                if (tabIndex == 0){
                    return if (filter.isFollowBack){
                        followerList.forEach { user ->
                            followBackList.addAll(followingList.filter { it.pk == user.pk })
                        }
                        followBackList
                    } else{
                        followerList.forEach { user ->
                            followBackList.addAll(followingList.filter { it.pk != user.pk })
                        }
                        followBackList
                    }

                }else{
                   return if (filter.isFollowBack){
                      followingList.forEach { user ->
                          followBackList.addAll(followerList.filter { it.pk == user.pk })
                      }
                       followBackList
                    }else{
                        val mylist = ArrayList<User>()
                        followBackList.addAll(list)
                       followBackList.forEach { user ->
                           mylist.addAll(followerList.filter { it.username == user.username })
                       }
                     val isRemove =  followBackList.removeAll()
                       isRemove
                       followBackList.cloned()
                   }

                }

            }


            else -> return list.cloned()

        }
    }




}