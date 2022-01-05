package com.example.instacleaner.ui.viewModels

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instacleaner.App
import com.example.instacleaner.R
import com.example.instacleaner.data.local.DescriptionDialogModel
import com.example.instacleaner.data.local.DialogModel
import com.example.instacleaner.data.local.ListDialogModel

import com.example.instacleaner.data.local.dialogModel.Tab
import com.example.instacleaner.data.remote.response.User
import com.example.instacleaner.data.remote.response.User.Companion.cloned
import com.example.instacleaner.repositories.InstaRepository
import com.example.instacleaner.utils.*
import com.example.instacleaner.utils.Constance.FOLLOWER_TAB_INDEX
import com.example.instacleaner.utils.Constance.FOLLOWING_TAB_INDEX
import com.example.instacleaner.utils.Constance.SEARCH_INTERVAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import util.extension.copyToClipboard
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    private val accountManager: AccountManager,
    private val repository: InstaRepository,
    private val app: App,
    private val clipboardManager: ClipboardManager
) : ViewModel() {


    var shouldScroll = false
    private var isRequesting = false
    private var followersLoadingVisibility = false
    private var followingLoadingVisibility = false
    private var isFollowerSelectionMode = false
    private var isFollowingSelectionMode = false
    private var maxId = ""
    private var searchQuery = ""
    private var lastSelectedUserIndex = 0
    private var imageDialogUserIndex = 0
    private var currentListSize = 0
    private var currentAccount = accountManager.getCurrentAccount()
    private var currentList:ArrayList<User> = arrayListOf()


    private var job: Job? = null

    var tabIndex = 0
    private val followerList = arrayListOf<User>()
    private val followingList = arrayListOf<User>()
    private var followerSelectionCount = 0
    private var followingSelectionCount = 0
    private val followerSortOptionList = arrayListOf(
        DialogModel(
            listOf(
                Tab(app.getString(R.string.a_to_z)),
                Tab(app.getString(R.string.z_to_a))
            ), app.getString(R.string.by_username), DialogModel.Options.ByUsername(true)
        ),
        DialogModel(
            listOf(
                Tab(app.getString(R.string.public_first)),
                Tab(app.getString(R.string.private_first))
            ), app.getString(R.string.by_status), DialogModel.Options.ByCondition(true)
        ),
        DialogModel(
            listOf(
                Tab(app.getString(R.string.pic_first)),
                Tab(app.getString(R.string.no_pic_first))
            ), app.getString(R.string.by_profile_picture), DialogModel.Options.ByAvatar(true)
        ),
        DialogModel(
            listOf(
                Tab(app.getString(R.string.selected_first)),
                Tab(app.getString(R.string.not_selected_first))
            ), app.getString(R.string.by_selection), DialogModel.Options.BySelection(true)
        ),
        DialogModel(
            listOf(Tab(app.getString(R.string.with_out_sort))),
            app.getString(R.string.with_out_sort),
            DialogModel.Options.NoSort,
            true
        )
    )
    private val followingSortOptionList = arrayListOf(
        DialogModel(
            listOf(
                Tab(app.getString(R.string.a_to_z)),
                Tab(app.getString(R.string.z_to_a))
            ), app.getString(R.string.by_username), DialogModel.Options.ByUsername(true)
        ),
        DialogModel(
            listOf(
                Tab(app.getString(R.string.public_first)),
                Tab(app.getString(R.string.private_first))
            ), app.getString(R.string.by_status), DialogModel.Options.ByCondition(true)
        ),
        DialogModel(
            listOf(
                Tab(app.getString(R.string.pic_first)),
                Tab(app.getString(R.string.no_pic_first))
            ), app.getString(R.string.by_profile_picture), DialogModel.Options.ByAvatar(true)
        ),
        DialogModel(
            listOf(
                Tab(app.getString(R.string.selected_first)),
                Tab(app.getString(R.string.not_selected_first))
            ), app.getString(R.string.by_selection), DialogModel.Options.BySelection(true)
        ),
        DialogModel(
            listOf(Tab(app.getString(R.string.with_out_sort))),
            app.getString(R.string.with_out_sort),
            DialogModel.Options.NoSort,
            true
        )
    )
    private val followerFilterOptionList = arrayListOf(
        DialogModel(
            listOf(
                Tab(app.getString(R.string.public_)),
                Tab(app.getString(R.string.private_))
            ), app.getString(R.string.by_status), DialogModel.Options.Status(false)
        ),
        DialogModel(
            listOf(
                Tab(app.getString(R.string.has_pic)),
                Tab(app.getString(R.string.no_pic))
            ), app.getString(R.string.by_profile_picture), DialogModel.Options.Avatar(false)
        ),
        DialogModel(
            listOf(
                Tab(app.getString(R.string.verified_accounts)),
                Tab(app.getString(R.string.not_verified_accounts))
            ), app.getString(R.string.by_verify), DialogModel.Options.Verify(false)
        ),
        // DialogModel(listOf(Tab(app.getString(R.string.verified_accounts)),Tab(app.getString(R.string.no_verified_account))),app.getString(R.string.by_verify)),
        DialogModel(
            listOf(
                Tab(app.getString(R.string.selected)),
                Tab(app.getString(R.string.not_selected))
            ), app.getString(R.string.by_selection), DialogModel.Options.Select(true)
        ),
        DialogModel(
            listOf(
                Tab(app.getString(R.string.i_following_back)),
                Tab(app.getString(R.string.i_not_following_back))
            ), app.getString(R.string.by_followback), DialogModel.Options.FollowBack(false)
        ),
        DialogModel(
            listOf(Tab(app.getString(R.string.remove_filter))),
            app.getString(R.string.no_filter),
            DialogModel.Options.NoFilter,true
        )
    )
    private val followingFilterOptionList = arrayListOf(
        DialogModel(
            listOf(
                Tab(app.getString(R.string.public_)),
                Tab(app.getString(R.string.private_))
            ), app.getString(R.string.by_status), DialogModel.Options.Status(false)
        ),
        DialogModel(
            listOf(
                Tab(app.getString(R.string.has_pic)),
                Tab(app.getString(R.string.no_pic))
            ), app.getString(R.string.by_profile_picture), DialogModel.Options.Avatar(false)
        ),
        DialogModel(
            listOf(
                Tab(app.getString(R.string.verified_accounts)),
                Tab(app.getString(R.string.not_verified_accounts))
            ), app.getString(R.string.by_verify), DialogModel.Options.Verify(false)
        ),
        DialogModel(
            listOf(
                Tab(app.getString(R.string.selected)),
                Tab(app.getString(R.string.not_selected))
            ), app.getString(R.string.by_selection), DialogModel.Options.Select(true)
        ),
        DialogModel(
            listOf(
                Tab(app.getString(R.string.they_following_back)),
                Tab(app.getString(R.string.they_not_following_back))
            ), app.getString(R.string.by_followback), DialogModel.Options.FollowBack(false)
        ),
        DialogModel(
            listOf(Tab(app.getString(R.string.remove_filter))),
            app.getString(R.string.no_filter),
            DialogModel.Options.NoFilter,true
        )
    )
    private val paginateStateList = arrayListOf(
            DescriptionDialogModel(app.getString(R.string.auto),app.getString(R.string.auto_desc),R.drawable.ic_auto,DescriptionDialogModel.Options.Auto),
            DescriptionDialogModel(app.getString(R.string.pause),app.getString(R.string.pause_desc),R.drawable.ic_pause,DescriptionDialogModel.Options.Pause),
            DescriptionDialogModel(app.getString(R.string.play),app.getString(R.string.play_desc),R.drawable.ic_play,DescriptionDialogModel.Options.Play),
            )


    private var pagingState: DescriptionDialogModel.Options = DescriptionDialogModel.Options.Auto


    val loadingVisibility = ObservableInt(View.GONE)
    val btnPaginateStateVisibility = ObservableInt(View.VISIBLE)
    val adapterList = MutableLiveData<ArrayList<User>>()
    val showFilterDialog = SingleLiveEvent<Pair<String, ArrayList<DialogModel>>>()
    val showSortDialog = SingleLiveEvent<Pair<String, ArrayList<DialogModel>>>()
    val showOptionDialog = SingleLiveEvent<Pair<String, List<ListDialogModel>>>()
    val showBottomSheet = SingleLiveEvent<Pair<String, List<ListDialogModel>>>()
    val showImageViewDialog = SingleLiveEvent<Triple<User,Int,Int>>()
    val updateImageViewDialog = SingleLiveEvent<Pair<Int,ArrayList<User>>>()
    val showSnackBar = SingleLiveEvent<String>()
    val navToProfileFragment = SingleLiveEvent<Boolean>()
    val selectionCount = MutableLiveData<Int>()
    val btnPaginateSateIcon = MutableLiveData<Int>()
    val showDescriptionDialog = SingleLiveEvent<List<DescriptionDialogModel>>()
    val imageDialogData = MutableLiveData<User>()



    init {
        btnPaginateSateIcon.value = R.drawable.ic_auto
        getFollowers()
        getFollowings()
    }

    fun onStart() {
        if (currentAccount.user.pk != accountManager.getCurrentAccount().user.pk) {
            currentAccount = accountManager.getCurrentAccount()
            followerList.clear()
            followingList.clear()
            followerSelectionCount = 0
            followingSelectionCount = 0
            selectionCount.value = followerSelectionCount
            getFollowings()
            getFollowers()
        }
    }

    fun itemClickAction(pos: Int, user: User) {

      when(tabIndex){
          FOLLOWER_TAB_INDEX -> {
              if (isFollowerSelectionMode){
                  val isSelected = currentList[pos].isSelected.not()
                  currentList[pos].isSelected = isSelected
                  updateMainList(currentList)
                  setList()
              }else{
                  navToProfileFragment.value = true
              }
          }
          FOLLOWING_TAB_INDEX -> {
              if (isFollowingSelectionMode){
                  val isSelected = currentList[pos].isSelected.not()
                  currentList[pos].isSelected = isSelected
                  updateMainList(currentList)
                  setList()
              }else{
                  navToProfileFragment.value = true
              }
          }
      }

    }



    fun itemLongClickAction(pos: Int, user: User) {
        lastSelectedUserIndex = pos
        imageDialogUserIndex = pos
        when (tabIndex) {
            FOLLOWER_TAB_INDEX -> if (isFollowerSelectionMode.not() && user.pk != -1L) {
                currentList.first { it.pk == user.pk }.apply {
                    isSelected = isSelected.not()
                }
                updateMainList(currentList)
                selectionCount.postValue(currentList.count { it.isSelected })
                //isFollowerSelectionMode = selectionCount.value != 0
                setList()
                isFollowerSelectionMode = true
            } else return
            FOLLOWING_TAB_INDEX -> if (isFollowingSelectionMode.not()) {
                currentList.first { it.pk == user.pk }.apply {
                    isSelected = isSelected.not()
                }
                updateMainList(currentList)
                selectionCount.postValue(currentList.count { it.isSelected })
                //isFollowingSelectionMode = selectionCount.value != 0
                setList()
                //isFollowerSelectionMode = true

            } else return
        }
    }
    fun tabSelectAction(position: Int) {
        shouldScroll = true
        tabIndex = position
        if (tabIndex == FOLLOWER_TAB_INDEX) btnPaginateStateVisibility.set(View.VISIBLE) else btnPaginateStateVisibility.set(View.GONE)
        setLoading()
        adapterList.value = arrayListOf()
        setList()

    }
    fun btnSelectionAction() {
        if (tabIndex == FOLLOWER_TAB_INDEX) {

                showBottomSheet.value = Pair("", followerSelectionDialogList)
        } else {

                showBottomSheet.value = Pair("", followingSelectionDialogList)
        }
    }
    fun btnPaginateStateAction() {
        showDescriptionDialog.value =  paginateStateList
    }
    fun itemOptionAction(option: ListDialogModel.Options) {
        val user = adapterList.value!![lastSelectedUserIndex]
        when (option) {
            ListDialogModel.Options.CopyUsername -> {
                val clip = ClipData.newPlainText(user.username, user.username)
                clipboardManager.setPrimaryClip(clip)
            }
            ListDialogModel.Options.CopyLink -> {

            }
            ListDialogModel.Options.GoToInstagram -> TODO()
            ListDialogModel.Options.ShowImage -> TODO()
            ListDialogModel.Options.ShowProfile -> TODO()
            else -> Unit
        }
    }
    fun btnFilterAction() {
        showFilterDialog.value = Pair(app.getString(R.string.filter), if (tabIndex == FOLLOWER_TAB_INDEX) followerFilterOptionList else followingFilterOptionList)
    }
    fun itemListOptionAction(pos: Int, user: User) {
        lastSelectedUserIndex = pos
        imageDialogUserIndex = pos
        //showOptionDialog.value = Pair(user.username, itemOptionDialogList)
        showBottomSheet.value = Pair(user.username,itemOptionDialogList)
    }
    fun bottomSheetAction(option:ListDialogModel.Options) {
        val user = currentList[lastSelectedUserIndex]
        when(option){
            ListDialogModel.Options.CopyLink ->{
              copyToClipboard(Constance.CON_INSTAGRAM_URL + user.username)
                showSnackBar.value = app.getString(R.string.copied_to_clipboard)
            }
            ListDialogModel.Options.CopyUsername ->{
                copyToClipboard(user.username)
                showSnackBar.value = app.getString(R.string.copied_to_clipboard)
            }
            ListDialogModel.Options.DeleteFollower ->{

            }
            ListDialogModel.Options.DeleteFollowing ->{

            }
            ListDialogModel.Options.DownloadProfile ->{

            }
            ListDialogModel.Options.FollowUser ->{

            }
            ListDialogModel.Options.GoToInstagram ->{
                app.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constance.CON_INSTAGRAM_URL + user.username)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
            ListDialogModel.Options.PauseState ->{

            }
            ListDialogModel.Options.PlayState ->{

            }
            ListDialogModel.Options.ShowImage ->{
             showImageViewDialog.value = Triple(currentList[imageDialogUserIndex],currentList.size,imageDialogUserIndex)
            }
            ListDialogModel.Options.ShowProfile ->{
             navToProfileFragment.value = true
            }
        }

    }
    fun popUpMenuAction(itemId: Int?) {
        adapterList.value ?: return

        when (itemId) {
            R.id.select_all -> {
                currentList.forEach {
                    it.isSelected = true
                }
                updateMainList(currentList)
                setList()
            }
            R.id.select_none -> {
                currentList.forEach {
                    it.isSelected = false
                }
                updateMainList(currentList)
                setList()


            }
            R.id.invert_selection -> {
                currentList.forEach {
                    it.isSelected = it.isSelected.not()
                }
                updateMainList(currentList)
                setList()


            }
            R.id.interval_selection -> {
                val firstSelectedIndex = currentList.indexOfFirst { it.isSelected }
                val lastSelectedIndex = currentList.indexOfLast { it.isSelected }
                for (index in firstSelectedIndex .. lastSelectedIndex){
                    currentList[index].isSelected = true
                }
                updateMainList(currentList)
                setList()


            }
        }
    }


    fun paginate() {
        when (pagingState) {
            DescriptionDialogModel.Options.Auto -> {
                if (tabIndex == FOLLOWER_TAB_INDEX && isRequesting.not() && maxId.isNotEmpty() ){
                    getFollowers(true)
                }
            }
            DescriptionDialogModel.Options.Pause -> Unit
            DescriptionDialogModel.Options.Play -> Unit
        }
    }



    fun setImageDialogData(){
        imageDialogData.value = currentList[lastSelectedUserIndex]
    }




    //mod
    private fun getFollowers(isPaging:Boolean = false)  {
        viewModelScope.launch {
            isRequesting = true
            if (isPaging.not()){
                followersLoadingVisibility = true
                setLoading()
            }
            if (pagingState is DescriptionDialogModel.Options.Play){
                delay(Constance.CON_GET_FOLLOWERS_DELAY)
            }
            when (val result = repository.getFollower(accountManager.getCurrentAccount(), maxId)) {
                is Resource.Success -> {
                    followersLoadingVisibility = false
                    followerList.addAll(result.data!!.users)
                    maxId = result.data.next_max_id ?: ""
                    setLoading()
                    setList()
                    isRequesting = false
                    if (maxId.isNotEmpty() && pagingState is  DescriptionDialogModel.Options.Play){
                        getFollowers()
                    }

                }
                is Resource.Error -> {
                    isRequesting = false
                    followersLoadingVisibility = false
                }
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

    fun setFilter(options: DialogModel.Options) {
        shouldScroll = true
        if (tabIndex == FOLLOWER_TAB_INDEX) {
            followerFilterOptionList.forEach {
                it.isSelected = it.option.javaClass.name == options.javaClass.name
                if (it.option.javaClass.name == options.javaClass.name){
                    it.option = options
                }
            }
        } else {
            followingFilterOptionList.forEach {
                it.isSelected = it.option.javaClass.name == options.javaClass.name
                if (it.option.javaClass.name == options.javaClass.name){
                    it.option = options
                }
            }
        }
        setList()

    }

    fun setSort(options: DialogModel.Options) {
        shouldScroll = true
        if (tabIndex == FOLLOWER_TAB_INDEX) {
            followerSortOptionList.forEach {
                it.isSelected = it.option.javaClass.name == options.javaClass.name
                if (it.option.javaClass.name == options.javaClass.name){
                    it.option = options
                }
            }
        } else {
            followingSortOptionList.forEach {
                it.isSelected = it.option.javaClass.name == options.javaClass.name
                if (it.option.javaClass.name == options.javaClass.name){
                    it.option = options
                }
            }
        }
        setList()
    }

   fun setPaginateState(options: DescriptionDialogModel.Options){
       pagingState = options
           when(options){
          is DescriptionDialogModel.Options.Auto -> {
               btnPaginateSateIcon.value = R.drawable.ic_auto
           }
          is DescriptionDialogModel.Options.Pause -> {
              btnPaginateSateIcon.value = R.drawable.ic_pause
           }
          is DescriptionDialogModel.Options.Play ->{
               btnPaginateSateIcon.value = R.drawable.ic_play
              if (maxId.isNotEmpty()){
                  getFollowers()
              }

           }
       }
   }

    //mod
     private fun setList() {
        if (tabIndex == FOLLOWER_TAB_INDEX){
            val sortOption = followerSortOptionList.first { it.isSelected }.option
            val filterOption = followerFilterOptionList.first { it.isSelected }.option
            val finalList = sort(sortOption,filter(followerList,filterOption).cloned())
            currentList = finalList.cloned()
            if (maxId.isNotEmpty() && pagingState is DescriptionDialogModel.Options.Auto ){
                finalList.add(User(-1))
            }
            adapterList.value = finalList
            val count = finalList.count { it.isSelected }
            selectionCount.value = count
            isFollowerSelectionMode = count != 0
        }
        else {
            val sortOption = followingSortOptionList.first { it.isSelected }.option
            val filterOption = followingFilterOptionList.first { it.isSelected }.option
            val finalList = sort(sortOption,filter(followingList,filterOption).cloned())
            currentList = finalList.cloned()
            adapterList.value = finalList
            val count = finalList.count { it.isSelected }
            isFollowingSelectionMode= count != 0
            selectionCount.value = count
        }
    }




    private val itemOptionDialogList = listOf(
        ListDialogModel(
            app.getString(R.string.view_profile),
            R.drawable.ic_account,
            ListDialogModel.Options.ShowProfile
        ),
        ListDialogModel(
            app.getString(R.string.view_image),
            R.drawable.ic_image,
            ListDialogModel.Options.ShowImage
        ),
        ListDialogModel(
            app.getString(R.string.goto_instagram),
            R.drawable.ic_instagram,
            ListDialogModel.Options.GoToInstagram
        ),
        ListDialogModel(
            app.getString(R.string.copy_link),
            R.drawable.ic_link,
            ListDialogModel.Options.CopyLink
        ),
        ListDialogModel(
            app.getString(R.string.copy_username),
            R.drawable.ic_copy,
            ListDialogModel.Options.CopyUsername
        ),
    )

    private val followerSelectionDialogList = listOf(
        ListDialogModel(
            app.getString(R.string.follow),
            R.drawable.ic_add,
            ListDialogModel.Options.FollowUser
        ),
        ListDialogModel(
            app.getString(R.string.remove_follower),
            R.drawable.ic_delete,
            ListDialogModel.Options.DeleteFollower
        ),
        ListDialogModel(
            app.getString(R.string.block),
            R.drawable.ic_block,
            ListDialogModel.Options.Block
        ),
        ListDialogModel(
            app.getString(R.string.download_profile_images),
            R.drawable.ic_download,
            ListDialogModel.Options.DownloadProfile
        )
    )

    private val followingSelectionDialogList = listOf(
        ListDialogModel(
            app.getString(R.string.unfollow),
            R.drawable.ic_remove,
            ListDialogModel.Options.FollowUser
        ),
        ListDialogModel(
            app.getString(R.string.block),
            R.drawable.ic_block,
            ListDialogModel.Options.Block
        ),
        ListDialogModel(
            app.getString(R.string.download_profile_images),
            R.drawable.ic_download,
            ListDialogModel.Options.DownloadProfile
        ),
    )




    //mod
    private  fun updateMainList(users: ArrayList<User>) {
        val currentIndexList = if (tabIndex == FOLLOWER_TAB_INDEX) followerList else followingList
        users.forEach { user ->
                currentIndexList.first { it.pk == user.pk }.isSelected = user.isSelected
            }
    }



    //mod
    fun search(query: String) {
        searchQuery = query
        job?.cancel()
       job = viewModelScope.launch {
            delay(SEARCH_INTERVAL)
           setList()
        }

    }

    //mod
    fun btnSortClick() {
        showSortDialog.value = Pair(app.getString(R.string.sort),if (tabIndex == FOLLOWER_TAB_INDEX) followerSortOptionList else followingSortOptionList)
    }

    private fun filter(list: ArrayList<User>, filter: DialogModel.Options): List<User> {
        //if (tabIndex == FOLLOWER_TAB_INDEX) followerFilter = filter else followingFilter = filter
        val filteredList:List<User> = when (filter) {
            is DialogModel.Options.Status -> {
                 if (filter.isPrivate){
                    list.filter { it.is_private }
                }else{

                    list.filter { it.is_private.not() }
                }
            }

            is DialogModel.Options.Avatar -> {
                 if (filter.hasAvatar) {
                      list.filter { it.has_anonymous_profile_picture.not() }

                } else {
                     list.filter { it.has_anonymous_profile_picture }
                }
            }

            is DialogModel.Options.Verify -> {
                 if (filter.isVerify) {
                     list.filter { it.is_verified }
                } else {
                     list.filter { it.is_verified.not() }
                }
            }

            is DialogModel.Options.Select -> {
                 if (filter.isSelected) {
                     list.filter { it.isSelected }
                } else {
                     list.filter { it.isSelected.not() }
                }
            }

            is DialogModel.Options.FollowBack -> {
                val followBackList = ArrayList<User>()
                if (tabIndex == 0) {
                     if (filter.isFollowBack) {
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
                     if (filter.isFollowBack){
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

            else -> list
        }
        return searchedList(filteredList)
    }

    private fun searchedList(list:List<User>) =
        if (searchQuery.isNotEmpty()) list.filter { it.username.contains(searchQuery) } else list.cloned()


    private fun sort(sort: DialogModel.Options, users: ArrayList<User>): ArrayList<User> {
        when (sort) {
            is DialogModel.Options.ByUsername -> {
                return if (sort.state) {
                    users.sortBy { it.username }

                    users
                } else {
                    users.sortBy { it.username }
                    users.reverse()
                    users
                }

            }
            is DialogModel.Options.ByCondition -> {
                return if (sort.isFirstPublic) {
                    users.sortBy { it.is_private.not() }
                    users
                } else {
                    users.sortBy { it.is_private }
                    users
                }

            }
            is DialogModel.Options.ByAvatar -> {
                return if (sort.hasFirstAvatar) {
                    users.sortBy { it.has_anonymous_profile_picture }
                    return users
                } else {
                    users.sortBy { it.has_anonymous_profile_picture.not() }
                    users
                }

            }
            is DialogModel.Options.BySelection -> {
                return if (sort.isFirstSelected) {
                    users.sortBy { it.isSelected.not() }
                    users
                } else {
                    users.sortBy { it.isSelected }
                    users
                }
            }
            is DialogModel.Options.NoSort -> {
                return users.cloned()
            }
            else -> return users.cloned()
        }
    }


    override fun onCleared() {
        super.onCleared()
        job?.let {
            it.cancel()
        }
        job = null
    }

    sealed class PagingState {
        object PauseState : PagingState()
        object AutoState:PagingState()
        object PlayState : PagingState()
    }

}
