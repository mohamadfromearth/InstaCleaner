package com.example.instacleaner.ui.viewModels

import android.content.SharedPreferences
import android.webkit.CookieManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.instacleaner.data.local.Account
import com.example.instacleaner.utils.AccountManager
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
class LoginViewModel @Inject constructor(private val accountManager: AccountManager):ViewModel() {


    val navToHome = MutableLiveData<Boolean>()

    val invalidCookie = SingleLiveEvent<String>()


   fun validateCookie(url: String?){
       url?.let {
           val cookie = getCookie(it)
           cookie?.let {
               when {
                   cookie.contains("sessionid=") -> {
                       val userId = extractCookie(cookie, "sessionid=")
                       accountManager.saveAccount(Account(userId.toLong(),cookie))
                       navToHome.value = true
                   }
                   cookie.contains("db_user_id") -> {
                       val userId = extractCookie(cookie, "ds_user_id=")
                       accountManager.saveAccount(Account(userId.toLong(),cookie))
                       navToHome.value = true
                   }
                   else -> invalidCookie.value = "Invalid cookie"
               }
           }

       }

   }







   private fun getCookie(url:String) = CookieManager.getInstance().getCookie(url)





}

