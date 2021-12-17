package com.example.instacleaner.data.local

import com.example.instacleaner.data.remote.response.User

data class Account(
    val cookie: String,
    var user: User,
    var isSelected:Boolean = false

) {
    companion object {
        fun ArrayList<Account>.cloned() = ArrayList(map { it.copy(user = it.user.copy()) })
        fun List<Account>.cloned() = ArrayList(map { it.copy() })
    }
}
