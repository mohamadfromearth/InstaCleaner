package com.example.instacleaner.ui.viewModels

import android.content.ClipData
import android.content.ClipboardManager
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
import com.example.instacleaner.utils.Constance.FOLLOWER_TAB_INDEX
import com.example.instacleaner.utils.Constance.FOLLOWING_TAB_INDEX
import com.example.instacleaner.utils.Constance.SEARCH_INTERVAL
import com.example.instacleaner.utils.Resource
import com.example.instacleaner.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    private var shouldSearchAgain = false
    private var isFollowerSelectionMode = false
    private var isFollowingSelectionMode = false
    private var isGetFollowerLoopOn = true
    private var maxId = ""
    private var searchQuery = ""
    private var lastSelectedUserIndex = 0
    private var currentAccount = accountManager.getCurrentAccount()


    private var job: Job? = null

    var tabIndex = 0
    private val followerList = arrayListOf<User>()
    private val followingList = arrayListOf<User>()
    private var followerSelectionCount = 0
    private var followingSelectionCount = 0




    private var followerFilter: DialogModel.Options = DialogModel.Options.NoFilter
    private var followingFilter: DialogModel.Options = DialogModel.Options.NoFilter
    private var followerSort: DialogModel.Options = DialogModel.Options.NoSort
    private var followingSort: DialogModel.Options = DialogModel.Options.NoSort

    private var pagingState: PagingState = PagingState.PauseState


    val loadingVisibility = ObservableInt(View.GONE)
    val adapterList = MutableLiveData<ArrayList<User>>()
    val showFilterDialog = SingleLiveEvent<Pair<String, ArrayList<DialogModel>>>()
    val showSortDialog = SingleLiveEvent<Pair<String, ArrayList<DialogModel>>>()
    val showOptionDialog = SingleLiveEvent<Pair<String, List<ListDialogModel>>>()
    val showSelectBottomSheet = SingleLiveEvent<Pair<String, List<ListDialogModel>>>()
    val navToProfileFragment = SingleLiveEvent<Boolean>()
    val selectionCount = MutableLiveData<Int>()
    val btnPaginateSateIcon = MutableLiveData<Int>()


    init {
        btnPaginateSateIcon.value = R.drawable.ic_play
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
        lastSelectedUserIndex = pos
        when (tabIndex) {
            FOLLOWER_TAB_INDEX -> if (isFollowerSelectionMode) handleFollowerItemSelection(pos) else navToProfileFragment.value =
                true
            FOLLOWING_TAB_INDEX -> if (isFollowingSelectionMode) handleFollowingItemSelection(pos) else navToProfileFragment.value =
                true
        }
    }

    private fun handleFollowerItemSelection(pos: Int) {
        val currentList = adapterList.value!!.cloned()
        val selectValue = currentList[pos].isSelected.not()
        setFollowerSelectionCount(selectValue)
        currentList[pos].isSelected = selectValue
        followerList.first { it.username == currentList[pos].username }.isSelected = selectValue
        adapterList.value = currentList
    }

    private fun handleFollowingItemSelection(pos: Int) {
        val currentList = adapterList.value!!.cloned()
        val selectValue = currentList[pos].isSelected.not()
        setFollowingSelectionCount(selectValue)
        currentList[pos].isSelected = selectValue
        followingList.first { it.pk == currentList[pos].pk }.isSelected = selectValue
        adapterList.value = currentList
    }

    fun itemLongClickAction(pos: Int, user: User) {
        lastSelectedUserIndex = pos
        when (tabIndex) {
            FOLLOWER_TAB_INDEX -> if (isFollowerSelectionMode.not()) {
                handleFollowerItemSelection(pos)
                isFollowerSelectionMode = true
            } else return
            FOLLOWING_TAB_INDEX -> if (isFollowingSelectionMode.not()) {
                handleFollowingItemSelection(pos)
                isFollowingSelectionMode = true
            } else return
        }
    }

    fun tabSelectAction(position: Int) {
        shouldScroll = true
        tabIndex = position
        setLoading()
        adapterList.value = arrayListOf()
        setList()

    }

    fun btnSelectionAction() {
        if (tabIndex == 0) {
            if (followerSelectionCount > 0)
                showSelectBottomSheet.value = Pair("", followerSelectionDialogList)
        } else {
            if (followingSelectionCount > 0)
                showSelectBottomSheet.value = Pair("", followingSelectionDialogList)
        }
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


    private fun setFollowingSelectionCount(isActionSelect: Boolean) {
        if (isActionSelect) {
            followingSelectionCount++
        } else {
            followingSelectionCount--
            if (followingSelectionCount == 0) isFollowingSelectionMode = false
        }
        selectionCount.value = followingSelectionCount
    }

    private fun setFollowerSelectionCount(isActionSelect: Boolean) {
        if (isActionSelect) {
            followerSelectionCount++

        } else {
            followerSelectionCount--
            if (followerSelectionCount == 0) isFollowerSelectionMode = false
        }
        selectionCount.value = followerSelectionCount
    }

    fun paginate() {
        when (pagingState) {
            PagingState.AutoState -> {
                if (tabIndex == FOLLOWER_TAB_INDEX && isRequesting.not() && maxId.isNotEmpty() ){
                    followerSort = DialogModel.Options.NoSort
                    getFollowers()
                }
            }
            PagingState.PauseState -> {
                if (tabIndex == FOLLOWER_TAB_INDEX && isRequesting.not() && maxId.isNotEmpty()) {
                    followerSort = DialogModel.Options.NoSort

                }
            }
            PagingState.PlayState -> Unit
        }

    }

    private fun getFollowersLoop() {
        if (maxId.isNotEmpty() && isGetFollowerLoopOn) {
            getFollowers() {
                if (maxId.isNotEmpty()) {
                    getFollowersLoop()
                }
            }
        }
    }

    fun setPaginateState() {
        pagingState = when (pagingState) {
           PagingState.AutoState -> {
               btnPaginateSateIcon.value = R.drawable.ic_pause
               isGetFollowerLoopOn = true
               getFollowersLoop()
               PagingState.PlayState
           }
            PagingState.PlayState -> {
                btnPaginateSateIcon.value = R.drawable.ic_play
                isGetFollowerLoopOn = false
                PagingState.PauseState
            }
            PagingState.PauseState -> {
                btnPaginateSateIcon.value = R.drawable.ic_auto
                isGetFollowerLoopOn = true
                //getFollowersLoop()
                PagingState.AutoState
            }

        }
    }

    private fun getFollowers(onSuccess: (() -> Unit)? = null) = viewModelScope.launch {
        followersLoadingVisibility = true
        isRequesting = true
        setLoading()
        when (val result = repository.getFollower(accountManager.getCurrentAccount(), maxId)) {
            is Resource.Success -> {
                followersLoadingVisibility = false
                followerList.addAll(result.data!!.users)
                maxId = result.data.next_max_id ?: ""
                setLoading()
                setList()
                isRequesting = false
                onSuccess?.let { it() }
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

    fun setFilter(options: DialogModel.Options) {
        shouldScroll = true
        if (tabIndex == 0) {
            followerFilter = options
        } else {
            followingFilter = options
        }
        setList()

    }

    fun setSort(options: DialogModel.Options) {
        shouldScroll = true
        if (tabIndex == 0) {
            followerSort = options
        } else {
            followingSort = options
        }
        setList()

    }

     private fun setList() {
        if (tabIndex == 0){
            selectionCount.value = followerSelectionCount
            adapterList.value = sort(followerSort,filter(followerList,followerFilter))
        }
        else {
            selectionCount.value = followingSelectionCount
            adapterList.value = sort(followingSort,filter(followingList,followingFilter))
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

    private fun followingFilterDialogList() =
        arrayListOf(
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
                    Tab(app.getString(R.string.they_following_back)),
                    Tab(app.getString(R.string.they_not_following_back))
                ), app.getString(R.string.by_followback), DialogModel.Options.FollowBack(false)
            ),
            DialogModel(
                listOf(Tab(app.getString(R.string.remove_filter))),
                app.getString(R.string.no_filter),
                DialogModel.Options.NoFilter
            )
        )

    private fun followerFilterDialogList() =
        arrayListOf(
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
                    Tab(app.getString(R.string.i_following_back)),
                    Tab(app.getString(R.string.i_not_following_back))
                ), app.getString(R.string.by_followback), DialogModel.Options.FollowBack(false)
            ),
            DialogModel(
                listOf(Tab(app.getString(R.string.remove_filter))),
                app.getString(R.string.no_filter),
                DialogModel.Options.NoFilter
            )
        )


    private fun sortList() = arrayListOf(
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
            DialogModel.Options.NoSort
        )
    )



    fun btnFilterAction() {
        val dialogModels = arrayListOf<DialogModel>()
        if (tabIndex == 0) {
            dialogModels.addAll(followerFilterDialogList())
            dialogModels.first { it.option.javaClass.name == followerFilter.javaClass.name }.isSelected =
                true
        } else {
            dialogModels.addAll(followingFilterDialogList())
            dialogModels.first { it.option.javaClass.name == followingFilter.javaClass.name }.isSelected =
                true
        }
        showFilterDialog.value = Pair(app.getString(R.string.filter), dialogModels)
    }

    fun itemListOptionAction(pos: Int, user: User) {
        lastSelectedUserIndex = pos
        showOptionDialog.value = Pair(user.username, itemOptionDialogList)
    }


    fun popUpMenuAction(itemId: Int?) {
        adapterList.value ?: return
        val currentList = adapterList.value!!.cloned()
        when (itemId) {
            R.id.select_all -> {
                if (tabIndex == 0) {
                    currentList.forEach {
                        it.isSelected = true
                    }

                    followerSelectionCount = currentList.size
                    updateMainList(currentList, followerList)
                    selectionCount.value = followerSelectionCount
                    adapterList.value = sort(followerSort, filter(currentList, followerFilter))
                } else {
                    currentList.forEach {
                        it.isSelected = true
                    }
                    followingSelectionCount = currentList.size
                    updateMainList(currentList, followingList)
                    selectionCount.value = followingSelectionCount
                    adapterList.value = sort(followingSort, filter(currentList, followingFilter))
                }
            }
            R.id.select_none -> {
                if (tabIndex == 0) {
                    followerList.forEach {
                        it.isSelected = false
                    }
                    followerSelectionCount = 0
                    selectionCount.value = followerSelectionCount
                    adapterList.value = sort(followerSort, filter(followerList, followerFilter))
                    isFollowerSelectionMode = false
                } else {
                    followingList.forEach {
                        it.isSelected = false
                    }
                    followingSelectionCount = 0
                    selectionCount.value = followingSelectionCount
                    adapterList.value = sort(followingSort, filter(followingList, followingFilter))
                    isFollowingSelectionMode = false
                }

            }
            R.id.invert_selection -> {
                if (tabIndex == 0) {
                    followerSelectionCount = 0
                    followerList.forEach {
                        it.isSelected = it.isSelected.not()
                        if (it.isSelected) {
                            followerSelectionCount++
                        }
                    }
                    selectionCount.value = followerSelectionCount

                    adapterList.value = sort(followerSort, filter(followerList, followerFilter))
                } else {
                    followingSelectionCount = 0
                    followingList.forEach {
                        it.isSelected = it.isSelected.not()
                        if (it.isSelected) {
                            followingSelectionCount++
                        }
                    }

                    selectionCount.value = followerSelectionCount
                    adapterList.value = sort(followingSort, filter(followingList, followingFilter))
                }

            }
            R.id.interval_selection -> {
                adapterList.value?.let { users ->
                    if (tabIndex == 0) {
                        intervalSelect(users.cloned())?.let {
                            updateMainList(it, followerList)
                            adapterList.value = it
                        }
                    } else {
                        intervalSelect(users.cloned())?.let {
                            updateMainList(it, followingList)
                            adapterList.value = it
                        }
                    }


                }
            }
        }
    }

    private fun updateMainList(users: ArrayList<User>, listForUpdate: ArrayList<User>) {
        users.forEach { user ->
            listForUpdate.forEach {
                if (it.pk == user.pk) {
                    it.isSelected = user.isSelected
                }
            }
        }
    }

    private fun setSelectionCountForInterval(firstIndex: Int, secondIndex: Int) {
        if (tabIndex == 0) {
            followerSelectionCount = secondIndex - firstIndex + 1
            selectionCount.value = followerSelectionCount
        } else {
            followingSelectionCount = secondIndex - firstIndex + 1
            selectionCount.value = followingSelectionCount
        }
    }

    private fun intervalSelect(list: ArrayList<User>): ArrayList<User>? {
        var shouldUpdateFirstIndex = true
        var firstIndex = 0
        var secondIndex = 0
        list.forEach {
            if (it.isSelected) {
                if (shouldUpdateFirstIndex) {
                    firstIndex = list.indexOf(it)
                    shouldUpdateFirstIndex = false
                } else {
                    secondIndex = list.indexOf(it)
                }
            }
        }

        setSelectionCountForInterval(firstIndex, secondIndex)

        for (index in firstIndex..secondIndex) {
            list[index].isSelected = true
        }
        return if (firstIndex != secondIndex) {
            list
        } else {
            null
        }
    }

    fun search(query: String) {
        //shouldSearchAgain = true
        searchQuery = query
        job?.cancel()
        job = viewModelScope.launch {
            delay(SEARCH_INTERVAL)
            if (tabIndex == 0) {
                if (query.isEmpty()) {
                    adapterList.value = sort(followerSort, filter(followerList,followerFilter))
                    shouldScroll = true
                    return@launch
                }
                adapterList.value =
                    sort(followerSort, filter(followerList,followerFilter))
            } else {
                if (query.isEmpty()) {
                    adapterList.value = sort(followingSort, filter(followingList, followingFilter))
                    shouldScroll = true
                    return@launch
                }
                adapterList.value =
                    sort(followingSort, filter(followingList,followingFilter))
            }

        }
        shouldSearchAgain = false

    }

    fun btnSortClick() {
        val sortList = sortList()
        if (tabIndex == 0) {
            sortList.first { it.option.javaClass.name == followerSort.javaClass.name }.isSelected =
                true
            showSortDialog.value = Pair(app.getString(R.string.sort), sortList)
        } else {
            sortList.first { it.option.javaClass.name == followingSort.javaClass.name }.isSelected =
                true
            showSortDialog.value = Pair(app.getString(R.string.sort), sortList)

        }
    }

    private fun filter(list: ArrayList<User>, filter: DialogModel.Options): ArrayList<User> {

        if (tabIndex == FOLLOWER_TAB_INDEX) followerFilter = filter else followingFilter = filter


        val filteredList:ArrayList<User> = when (filter) {
            is DialogModel.Options.Status -> {
                 if (filter.isPrivate){
                    list.filter { it.is_private }.cloned()
                }else{

                    list.filter { it.is_private.not() }.cloned()
                }
            }

            is DialogModel.Options.Avatar -> {
                 if (filter.hasAvatar) {
                      list.filter { it.has_anonymous_profile_picture.not() }.cloned()

                } else {
                     list.filter { it.has_anonymous_profile_picture }.cloned()
                }
            }

            is DialogModel.Options.Verify -> {
                 if (filter.isVerify) {
                     list.filter { it.is_verified }.cloned()
                } else {
                     list.filter { it.is_verified.not() }.cloned()
                }
            }

            is DialogModel.Options.Select -> {
                 if (filter.isSelected) {
                     list.filter { it.isSelected }.cloned()
                } else {
                     list.filter { it.isSelected.not() }.cloned()
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

            else -> list.cloned()
        }
        return searchedList(filteredList)
    }

    private fun searchedList(list:ArrayList<User>) =
        if (searchQuery.isNotEmpty()) list.filter { it.username.contains(searchQuery) }.cloned() else list




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
