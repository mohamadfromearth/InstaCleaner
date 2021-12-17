package com.example.instacleaner.data.local

import com.example.instacleaner.data.local.dialogModel.Tab

data class DialogModel(
    val tabs:List<Tab>,
    val title:String,
    var isSelected:Boolean = false
 //   val type:String
){
    companion object {
        fun ArrayList<DialogModel>.cloned() = ArrayList(map { it.copy() })

    }
}
