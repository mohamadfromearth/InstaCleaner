package com.example.instacleaner.data.local

import com.example.instacleaner.data.local.dialogModel.Tab

data class DialogModel(
    val tabs:List<Tab>,
    val title:String,
    var option : Options,
    var isSelected:Boolean = false
 //   val type:String
){
    companion object {
        fun ArrayList<DialogModel>.cloned() = ArrayList(map { it.copy() })
    }

    sealed class Options
    {
        class Status(val isPrivate : Boolean):Options()
        class Avatar(val hasAvatar : Boolean) : Options()
        class Verify(val isVerify:Boolean):Options()
        class Select(val isSelected:Boolean):Options()
        class FollowBack(val isFollowBack:Boolean):Options()
        object NoFilter : Options()
        class ByUsername(val state:Boolean):Options()
        class ByCondition (val isFirstPublic:Boolean):Options()
        class ByAvatar (val hasFirstAvatar:Boolean):Options()
        class BySelection (val isFirstSelected:Boolean):Options()
        object NoSort:Options()
    }
}
