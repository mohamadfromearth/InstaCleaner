package com.example.mohamadkh_instacleaner.data.remote.response.userFollowers

import com.example.instacleaner.data.remote.response.User

data class UserFollowers(
    val big_list: Boolean,
    val friend_requests: FriendRequests,
    val groups: List<Any>,
    val more_groups_available: Boolean,
    val page_size: Double,
    val status: String,
    val users: List<User>
)