package com.example.instacleaner.data.local

import com.example.mohamadkh_instacleaner.data.remote.response.User

data class Account(
    val userId:Long,
    val cookie:String,
    var user : User? = null,
    )
