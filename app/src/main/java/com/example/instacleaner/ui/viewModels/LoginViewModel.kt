package com.example.instacleaner.ui.viewModels

import android.view.View
import android.webkit.CookieManager
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instacleaner.App
import com.example.instacleaner.data.local.Account
import com.example.instacleaner.data.remote.response.User
import com.example.instacleaner.utils.AccountManager
import com.example.instacleaner.utils.Constance.CON_VALIDATE_COOKIE_INTERVAL
import com.example.instacleaner.utils.SingleLiveEvent
import com.example.instacleaner.utils.extractCookie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


//get session cookie from webView  and save it to sharePreference

//private val accountPref = preferenceManager.getString(COOKIE, "")
//private val accountListType = object : TypeToken<List<Account>>() {}.type
//private val accountsModel = Gson().fromJson<ArrayList<Account>>(accountPref, accountListType)
//private val isLogin = preferenceManager.getBoolean(IS_LOGIN)


@HiltViewModel
class LoginViewModel @Inject constructor(private val accountManager: AccountManager,private val app:App):ViewModel() {


    private var job: Job? = null

//    private val isLogin = accountManager.isLogin()
    val backBtnVisibility = if (accountManager.isLogin()) ObservableInt(View.VISIBLE ) else ObservableInt(View.GONE)

    val navToHome = SingleLiveEvent<Boolean>()

    val invalidCookie = SingleLiveEvent<String>()

    val exist = SingleLiveEvent<Boolean>()

//    val popToHome = SingleLiveEvent<Boolean>()


   fun validateCookie(url: String?){

       var userId = ""
       job?.cancel()
      job = viewModelScope.launch {
           url?.let {
               val cookie = getCookie(it)
               cookie?.let {
                   when {
                       cookie.contains("sessionid=") -> {
                            userId = extractCookie(cookie, "sessionid=")



                       }
                       cookie.contains("db_user_id") -> {
                            userId = extractCookie(cookie, "ds_user_id=")



                       }
                       else -> return@launch
                   }

                   delay(CON_VALIDATE_COOKIE_INTERVAL)

                   if (!url.contains("/terms/") && !url.contains("/challenge/")){
                       accountManager.saveAccount(Account(cookie,User(pk = userId.toLong())))
                       clearCookies()
                       navToHome.value = true
                   }else{
                       clearCookies()
                   }



               }
           }
       }



   }

    private fun clearCookies() {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    }


    fun navToHome(){
        navToHome.value = false
    }

    private fun getCookie(url:String) = CookieManager.getInstance().getCookie(url)


    fun backPress() {
       if (accountManager.isLogin()) navToHome.value = false else exist.value = true
    }


}

