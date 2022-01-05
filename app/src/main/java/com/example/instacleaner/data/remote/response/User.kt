package com.example.instacleaner.data.remote.response

import android.os.Environment
import util.extension.getInternalDirectory
import java.io.File

data class User(
    val pk: Long = 0,
    val biography: String? = null,
    val follower_count: Int? = null,
    val following_count: Int? = null,
    val full_name: String = "",
    val has_anonymous_profile_picture: Boolean = false,
    val has_highlight_reels: Boolean? = false,
    val is_private: Boolean = false,
    val media_count: Int? = null,
    val profile_pic_url: String = "",
    val hd_profile_pic_url_info: HdProfile = HdProfile(0,"",0),
    val username: String = "",
    val is_verified: Boolean = false,
    var isSelected:Boolean = false,
    var path : String = "",
    var isDownloaded : Boolean = false,
) {
    fun init() {
        path = Environment.DIRECTORY_PICTURES + File.separator+"profiles"+ File.separator + File.separator + username
        isDownloaded = File(getInternalDirectory().absolutePath ,File.separator + "/profiles" + File.separator +  username).exists()
    }

    companion object {
        fun ArrayList<User>.cloned() = ArrayList(map { it.copy() })
        fun List<User>.cloned() = ArrayList(map { it.copy() })
    }
}