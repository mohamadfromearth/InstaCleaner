package com.example.instacleaner.data.remote.body

data class FollowBody(
    val _csrftoken:String,
    val user_id:String,
    val radio_type:String = "wifi-none",
    val _uid:Long,
    val device_id:String,
    val _uuid:Long
)
