package com.example.instacleaner.data.local

data class DescriptionDialogModel(
    val title:String,
    val description:String,
    val icon:Int,
    val options:Options
){
    sealed class Options{
        object Play:Options()
        object Pause:Options()
        object Auto:Options()
    }
}