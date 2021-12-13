package com.example.instacleaner.data.remote.response.userFollowers

import com.example.instacleaner.data.remote.response.User

data class UserList(
    val hasPaginate:Boolean,
    val users:ArrayList<User> = ArrayList<User>()

)

