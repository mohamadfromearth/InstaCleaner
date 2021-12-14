package com.example.instacleaner.utils


import android.content.SharedPreferences

class PreferenceManager(private val preference : SharedPreferences){
    fun set(key : String, value : Any?){
        when(value){
            is String -> preference.edit().putString(key,value).apply()
            is Boolean-> preference.edit().putBoolean(key,value).apply()
            is Int -> preference.edit().putInt(key,value).apply()
            is Long -> preference.edit().putLong(key,value).apply()
        }

    }

    fun clear(){
        preference.edit().clear().apply()
    }

    fun getString(key : String) = preference.getString(key,null)

    fun getBoolean(key: String) = preference.getBoolean(key,false)

    fun getLong(key:String) = preference.getLong(key,0)
}

