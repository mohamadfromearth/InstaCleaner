package com.example.instacleaner.data.local

data class ListDialogModel(
    val title:String,
    val icon:Int,
    val option: Options

){
    sealed class Options{
        object ShowProfile:Options()
        object ShowImage:Options()
        object GoToInstagram:Options()
        object CopyLink:Options()
        object CopyUsername:Options()
        object FollowUser:Options()
        object DeleteFollower:Options()
        object DeleteFollowing:Options()
        object Block:Options()
        object DownloadProfile:Options()

    }


}




