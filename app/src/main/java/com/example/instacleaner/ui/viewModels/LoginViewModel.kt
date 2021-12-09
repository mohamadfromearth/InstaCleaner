package com.example.instacleaner.ui.viewModels

import android.webkit.CookieManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.instacleaner.data.local.Account
import com.example.instacleaner.utils.Constance.ACCOUNT
import com.example.instacleaner.utils.Constance.CURRENT_ACCOUNT
import com.example.instacleaner.utils.PreferenceManager
import com.example.instacleaner.utils.SingleLiveEvent
import com.example.instacleaner.utils.extractCookie
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


//get session cookie from webView  and save it to sharePreference

//private val accountPref = preferenceManager.getString(COOKIE, "")
//private val accountListType = object : TypeToken<List<Account>>() {}.type
//private val accountsModel = Gson().fromJson<ArrayList<Account>>(accountPref, accountListType)
//private val isLogin = preferenceManager.getBoolean(IS_LOGIN)


@HiltViewModel
class LoginViewModel @Inject constructor(private val preferenceManager: PreferenceManager):ViewModel() {


    val navToHome = MutableLiveData<Boolean>()

    val invalidCookie = SingleLiveEvent<String>()


   fun validateCookie(url: String?){
       url?.let {
           val cookie = getCookie(it)
           when {
               cookie.contains("sessionid=") -> {
                   val userId = extractCookie(cookie, "sessionid=")
                   saveAccountToSharePreference(Account(userId.toLong(),cookie))
                   navToHome.value = true
               }
               cookie.contains("db_user_id") -> {
                   val userId = extractCookie(cookie, "ds_user_id=")
                   saveAccountToSharePreference(Account(userId.toLong(),cookie))
                   navToHome.value = true
               }
               else -> invalidCookie.value = "Invalid cookie"
           }
       }

   }


    private fun saveAccountToSharePreference(account: Account){
        val accountManager = AccountManager(preferenceManager,account)
        if (accountManager.isAccountExists()) return
        accountManager.saveAccount()

    }




   private fun getCookie(url:String) = CookieManager.getInstance().getCookie(url)





}

class AccountManager(private val preferenceManager:PreferenceManager,private val account: Account){
       private val accountsStringFromSharePref = preferenceManager.getString(ACCOUNT)
       private val accountListTypeForGson =  object : TypeToken<List<Account>>() {}.type
       private val accounts = Gson().fromJson<ArrayList<Account>>(accountsStringFromSharePref, accountListTypeForGson)
    fun isAccountExists():Boolean{
        accounts.forEach{
            if (account.cookie == it.cookie) return true
        }
        return false
    }

    fun saveAccount(){
        accounts.add(account)
        val accountsJson = Gson().toJson(accounts)
        preferenceManager.set(CURRENT_ACCOUNT,account.userId)
        preferenceManager.set(ACCOUNT,accountsJson)
    }
}