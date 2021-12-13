package com.example.instacleaner.data.remote.response.userFollowers

import com.example.instacleaner.data.remote.response.User

data class UserFollowers(
    val next_max_id:String?,
    val users: List<User>
)