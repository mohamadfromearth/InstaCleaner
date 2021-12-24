package com.example.instacleaner.ui.viewModels

import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instacleaner.App
import com.example.instacleaner.R
import com.example.instacleaner.data.local.DialogModel
import com.example.instacleaner.data.local.ListDialogModel

import com.example.instacleaner.data.local.dialogModel.Tab
import com.example.instacleaner.data.remote.response.User
import com.example.instacleaner.data.remote.response.User.Companion.cloned
import com.example.instacleaner.repositories.InstaRepository
import com.example.instacleaner.utils.AccountManager
import com.example.instacleaner.utils.Constance.SEARCH_INTERVAL
import com.example.instacleaner.utils.Resource
import com.example.instacleaner.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    private var isSearching = false
    private var followersLoadingVisibility = false
    private var followingLoadingVisibility = false
    private var maxId = ""
    private var currentAccount = accountManager.getCurrentAccount()

    var tabIndex = 0
    private val followerList = arrayListOf<User>()
    private val followingList = arrayListOf<User>()


    val loadingVisibility = ObservableInt(View.GONE)


    private var followerFilter : DialogModel.Options = DialogModel.Options.NoFilter
    private var followingFilter : DialogModel.Options = DialogModel.Options.NoFilter
    private var followerSort:DialogModel.Options =  DialogModel.Options.NoSort
    private var followingSort:DialogModel.Options = DialogModel.Options.NoSort

    val adapterList = MutableLiveData<ArrayList<User>>()
    val showFilterDialog = SingleLiveEvent<Pair<String,ArrayList<DialogModel>>>()
    val showSortDialog = SingleLiveEvent<Pair<String,ArrayList<DialogModel>>>()
    val showOptionDialog = SingleLiveEvent<Pair<List<ListDialogModel>,User>>()




    private val listDialogModels = listOf(
        ListDialogModel(app.getString(R.string.view_profile),R.drawable.ic_account),
        ListDialogModel(app.getString(R.string.view_image),R.drawable.ic_image),
        ListDialogModel(app.getString(R.string.goto_instagram),R.drawable.ic_instagram),
        ListDialogModel(app.getString(R.string.copy_link),R.drawable.ic_link),
        ListDialogModel(app.getString(R.string.copy_username),R.drawable.ic_copy),
    )




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
         if (tabIndex == 0 && isRequesting.not() && maxId.isNotEmpty() && isSearching.not()){
             getFollowers()
         }
    }

   private fun getFollowers() = viewModelScope.launch {
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


    private fun getFollowings() = viewModelScope.launch {
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

    fun setFilter(options:DialogModel.Options){
        shouldScroll = true
        if (tabIndex == 0){
            followerFilter = options
        }else{
            followingFilter = options
        }
        setList()
       // shouldScroll = false
    }

    fun setSort(options:DialogModel.Options){
        shouldScroll = true
        if (tabIndex==0){
            followerSort = options
        }else{
            followingSort = options
        }
        setList()
        //shouldScroll = false
    }

     private fun setList() {
        if (tabIndex == 0) adapterList.value = sort(followerSort,filter(followerList,followerFilter))
        else adapterList.value = sort(followingSort,filter(followingList,followingFilter))
     }


    fun userClickAction(user: User) {
        if (tabIndex == 0) {
            followerList
        } else {

        }
    }





    private fun followingFilterDialogList() =
        arrayListOf(
            DialogModel(listOf(Tab(app.getString(R.string.public_)),Tab(app.getString(R.string.private_))),app.getString(R.string.by_status),DialogModel.Options.Status(false)),
            DialogModel(listOf(Tab(app.getString(R.string.has_pic)),Tab(app.getString(R.string.no_pic))),app.getString(R.string.by_profile_picture),DialogModel.Options.Avatar(false)),
            DialogModel(listOf(Tab(app.getString(R.string.verified_accounts)),Tab(app.getString(R.string.not_verified_accounts))),app.getString(R.string.by_verify),DialogModel.Options.Verify(false)),
           // DialogModel(listOf(Tab(app.getString(R.string.verified_accounts)),Tab(app.getString(R.string.no_verified_account))),app.getString(R.string.by_verify)),
            DialogModel(listOf(Tab(app.getString(R.string.selected)),Tab(app.getString(R.string.not_selected))),app.getString(R.string.by_selection),DialogModel.Options.Select(true)),
            DialogModel(listOf(Tab(app.getString(R.string.they_following_back)),Tab(app.getString(R.string.they_not_following_back))),app.getString(R.string.by_followback),DialogModel.Options.FollowBack(false)),
            DialogModel(listOf(Tab(app.getString(R.string.remove_filter))),app.getString(R.string.no_filter),DialogModel.Options.NoFilter)
        )

    private fun followerFilterDialogList() =
        arrayListOf(
    DialogModel(listOf(Tab(app.getString(R.string.public_)),Tab(app.getString(R.string.private_))),app.getString(R.string.by_status),DialogModel.Options.Status(false)),
    DialogModel(listOf(Tab(app.getString(R.string.has_pic)),Tab(app.getString(R.string.no_pic))),app.getString(R.string.by_profile_picture),DialogModel.Options.Avatar(false)),
    DialogModel(listOf(Tab(app.getString(R.string.verified_accounts)),Tab(app.getString(R.string.not_verified_accounts))),app.getString(R.string.by_verify),DialogModel.Options.Verify(false)),
            DialogModel(listOf(Tab(app.getString(R.string.selected)),Tab(app.getString(R.string.not_selected))),app.getString(R.string.by_selection),DialogModel.Options.Select(true)),
    DialogModel(listOf(Tab(app.getString(R.string.i_following_back)),Tab(app.getString(R.string.i_not_following_back))),app.getString(R.string.by_followback),DialogModel.Options.FollowBack(false)),
    DialogModel(listOf(Tab(app.getString(R.string.remove_filter))),app.getString(R.string.no_filter),DialogModel.Options.NoFilter)
        )


    private fun sortList() = arrayListOf(
        DialogModel(listOf(Tab(app.getString(R.string.a_to_z)),Tab(app.getString(R.string.z_to_a))),app.getString(R.string.by_username),DialogModel.Options.ByUsername(true)),
        DialogModel(listOf(Tab(app.getString(R.string.public_first)),Tab(app.getString(R.string.private_first))),app.getString(R.string.by_status),DialogModel.Options.ByCondition(true)),
        DialogModel(listOf(Tab(app.getString(R.string.pic_first)),Tab(app.getString(R.string.no_pic_first))),app.getString(R.string.by_profile_picture),DialogModel.Options.ByAvatar(true)),
        DialogModel(listOf(Tab(app.getString(R.string.selected_first)),Tab(app.getString(R.string.not_selected_first))),app.getString(R.string.by_selection),DialogModel.Options.BySelection(true)),
        DialogModel(listOf(Tab(app.getString(R.string.sort))),app.getString(R.string.sort),DialogModel.Options.NoSort)
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
            dialogModels.addAll(followerFilterDialogList())
            dialogModels.first { it.option.javaClass.name == followerFilter.javaClass.name }.isSelected = true
        }else{
            dialogModels.addAll(followingFilterDialogList())
            dialogModels.first { it.option.javaClass.name == followingFilter.javaClass.name }.isSelected = true
        }
            showFilterDialog.value = Pair(app.getString(R.string.filter),dialogModels)
    }

   private  fun filter(list : ArrayList<User>, filter : DialogModel.Options) : ArrayList<User>{
        if (tabIndex == 0) followerFilter = filter else followingFilter = filter
        when(filter){
            is DialogModel.Options.Status -> {
                return if (filter.isPrivate){
                    list.filter { it.is_private }.cloned()
                }else{

                    list.filter { it.is_private.not() }.cloned()
                }
            }

            is DialogModel.Options.Avatar -> {
                return if(filter.hasAvatar){
                    list.filter { it.has_anonymous_profile_picture.not() }.cloned()
                }else{
                    list.filter { it.has_anonymous_profile_picture }.cloned()
                }
            }

            is DialogModel.Options.Verify -> {
                return if (filter.isVerify){
                    list.filter { it.is_verified }.cloned()
                }else{
                    list.filter { it.is_verified.not() }.cloned()
                }
            }

            is DialogModel.Options.Select -> {
                return if (filter.isSelected){
                    list.filter { it.isSelected }.cloned()
                 }else{
                     list.filter { it.isSelected.not() }.cloned()
                 }
            }

            is DialogModel.Options.FollowBack -> {
                val followBackList = ArrayList<User>()
                if (tabIndex == 0){
                    return if (filter.isFollowBack){
                        followerList.forEach { user ->
                            followBackList.addAll(followingList.filter { it.pk == user.pk })
                        }
                        followBackList
                    } else{
                        followBackList.addAll(followerList.cloned())
                        followingList.forEach { user->
                            followBackList.removeAll { it.pk ==  user.pk}
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
                        followBackList.addAll(followingList.cloned())
                       followerList.forEach {user->
                           followBackList.removeAll { it.pk == user.pk }
                       }

                       followBackList
                   }

                }

            }


            else -> return list.cloned()

        }
    }



   private fun sort(sort:DialogModel.Options,users:ArrayList<User>):ArrayList<User>{

       when(sort){
           is DialogModel.Options.ByUsername -> {
               return if (sort.state){
                   users.sortBy { it.username }
                   users
               }else{
                   users.sortBy { it.username }
                   users.reverse()
                   users
               }

           }
           is DialogModel.Options.ByCondition -> {
               return if (sort.isFirstPublic){
                   users.sortBy {it.is_private.not()}
                   users
               }else{
                   users.sortBy { it.is_private }
                   users
               }

           }
           is DialogModel.Options.ByAvatar -> {
              return if (sort.hasFirstAvatar){
                  users.sortBy { it.has_anonymous_profile_picture }
                  return users
              }else{
                  users.sortBy { it.has_anonymous_profile_picture.not() }
                  users
              }

           }
           is DialogModel.Options.BySelection -> {
            return if (sort.isFirstSelected){
                users.sortBy {  it.isSelected.not()}
                users
            }else{
                users.sortBy { it.isSelected }
                users
            }
           }
           is DialogModel.Options.NoSort -> {
             return users
           }
           else -> return users
       }
   }


    fun popUpMenuAction(itemId: Int?) {
        when(itemId){
            R.id.select_all -> {
             if (tabIndex == 0){
                 followerList.forEach {
                     it.isSelected = true
                 }
                 adapterList.value = sort(followerSort,filter(followerList,followerFilter))
             }else{
                 followingList.forEach {
                     it.isSelected = true
                 }
                adapterList.value = sort(followingSort,filter(followingList,followingFilter))
             }
            }
           R.id.select_none -> {
               if (tabIndex == 0){
                   followerList.forEach {
                       it.isSelected = false
                   }
                   adapterList.value = sort(followerSort,filter(followerList,followerFilter))
               }else{
                   followingList.forEach {
                       it.isSelected = false
                   }
                   adapterList.value = sort(followingSort,filter(followingList,followingFilter))
               }

           }
           R.id.invert_selection -> {
               if (tabIndex == 0){
                   followerList.forEach {
                       it.isSelected = it.isSelected.not()
                   }
                   adapterList.value = sort(followerSort,filter(followerList,followerFilter))
               }else{
                   followingList.forEach {
                       it.isSelected = it.isSelected.not()
                   }
                   adapterList.value = sort(followingSort,filter(followingList,followingFilter))
               }

           }
           R.id.interval_selection -> {
               adapterList.value?.let { users ->
                   if (tabIndex == 0){
                       intervalSelect(users.cloned())?.let {
                           updateMainList(it,followerList)
                           adapterList.value = it
                       }
                   }else{
                       intervalSelect(users.cloned())?.let {
                           updateMainList(it,followingList)
                           adapterList.value = it
                       }
               }



               }
           }
        }
    }


    private fun updateMainList(users:ArrayList<User>, listForUpdate:ArrayList<User>){
        users.forEach { user ->
            listForUpdate.forEach{
                if (it.pk == user.pk) it.isSelected = user.isSelected
            }
        }
    }


   private fun intervalSelect(list:ArrayList<User>):ArrayList<User>?{
       var shouldUpdateFirstIndex = true
       var firstIndex = 0
       var secondIndex = 0
       list.forEach {
           if (it.isSelected){
               if (shouldUpdateFirstIndex){
                   firstIndex = list.indexOf(it)
                   shouldUpdateFirstIndex = false
               }else{
                   secondIndex = list.indexOf(it)
               }
           }
       }

       for ( index in firstIndex .. secondIndex){
           list[index].isSelected = true
       }
       return if (firstIndex != secondIndex){
           list
       }else{
           null
       }
   }

    fun search(query:String) {
        isSearching = true
        viewModelScope.launch {
         delay(SEARCH_INTERVAL)
            if (tabIndex == 0){
                if (query.isEmpty()){
                    shouldScroll =true
                    adapterList.value = sort(followerSort,filter(followerList,followerFilter))
                    isSearching = false
                    return@launch
                }
            }else{
                if (query.isEmpty()) {
                    shouldScroll = true
                    adapterList.value = sort(followingSort,filter(followingList,followingFilter))
                    isSearching = false
                    return@launch
                }
            }
            adapterList.value?.let { users ->
                adapterList.value = users.filter { it.username.contains(query) }.cloned()

            }
        }
    }

    fun btnSortClick() {
        val sortList = sortList()
        if (tabIndex == 0){
            sortList.first { it.option.javaClass.name == followerSort.javaClass.name }.isSelected = true
            showSortDialog.value = Pair("",sortList)
        }else{
            sortList.first { it.option.javaClass.name == followingSort.javaClass.name }.isSelected = true
            showSortDialog.value = Pair("",sortList)

        }
    }

    fun listOptionClick(user:User){
        showOptionDialog.value = Pair(listDialogModels,user)

    }

    }
