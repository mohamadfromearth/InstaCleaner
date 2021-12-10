package com.example.instacleaner.data.local

import com.example.mohamadkh_instacleaner.data.remote.response.User

data class Account(
    val userId:Long = 0,
    val cookie:String = "",
    var user : User? = null,
    var isLast:Boolean = false

    ){
    companion object {
        fun ArrayList<Account>.cloned() = ArrayList(map { it.copy() })
        fun List<Account>.cloned() = ArrayList(map { it.copy() })
    }
}
