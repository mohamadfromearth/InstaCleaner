package com.example.instacleaner.data.local

import com.example.instacleaner.data.local.dialogModel.Tab
import java.util.logging.Filter

data class DialogModel(
    val tabs:List<Tab>,
    val title:String,
    var filter : FilterType,
    var isSelected:Boolean = false
 //   val type:String
){
    companion object {
        fun ArrayList<DialogModel>.cloned() = ArrayList(map { it.copy() })

    }

    sealed class FilterType {
        class Status(val isPrivate : Boolean):FilterType()
        class Avatar(val hasAvatar : Boolean) : FilterType()
        class Verify(val isVerify:Boolean):FilterType()
        class Select(val isSelected:Boolean):FilterType()
        class FollowBack(val isFollowBack:Boolean):FilterType()
        object NoFilter : FilterType()
    }
}
