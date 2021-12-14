package com.example.instacleaner.data.remote.response

data class User(
    val biography: String? = null,
    val follower_count: Int? = null,
    val following_count: Int? = null,
    val full_name: String = "",
    val has_anonymous_profile_picture: Boolean? = null,
    val has_highlight_reels: Boolean? = false,
    val is_private: Boolean = false,
    val media_count: Int? = null,
    val pk: Long,
    val profile_pic_url: String = "",
    val username: String = "",
    val is_verified: Boolean = false,
    val isSelected:Boolean = false
) {
    companion object {
        fun ArrayList<User>.cloned() = ArrayList(map { it.copy() })
        fun List<User>.cloned() = ArrayList(map { it.copy() })
    }
}