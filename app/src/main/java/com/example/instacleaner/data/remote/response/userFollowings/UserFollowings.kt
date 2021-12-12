package com.example.mohamadkh_instacleaner.data.remote.response.userFollowings

import com.example.instacleaner.data.remote.response.User

data class UserFollowings(
    val big_list: Boolean,
    val page_size: Int,
    val status: String,
    val users: List<User>
)