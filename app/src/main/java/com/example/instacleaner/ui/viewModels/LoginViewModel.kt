package com.example.instacleaner.ui.viewModels

import android.view.View
import android.webkit.CookieManager
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import com.example.instacleaner.App
import com.example.instacleaner.data.local.Account
import com.example.instacleaner.utils.AccountManager
import com.example.instacleaner.utils.SingleLiveEvent
import com.example.instacleaner.utils.extractCookie
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


//get session cookie from webView  and save it to sharePreference

//private val accountPref = preferenceManager.getString(COOKIE, "")
//private val accountListType = object : TypeToken<List<Account>>() {}.type
//private val accountsModel = Gson().fromJson<ArrayList<Account>>(accountPref, accountListType)
//private val isLogin = preferenceManager.getBoolean(IS_LOGIN)


@HiltViewModel
class LoginViewModel @Inject constructor(private val accountManager: AccountManager,private val app:App):ViewModel() {




    private val isLogin = accountManager.isLogin()
    val backBtnVisibility = if (isLogin) ObservableInt(View.VISIBLE ) else ObservableInt(View.GONE)

    val navToHome = SingleLiveEvent<Boolean>()

    val invalidCookie = SingleLiveEvent<String>()

    val popToHome = SingleLiveEvent<Boolean>()


   fun validateCookie(url: String?){
       url?.let {
           val cookie = getCookie(it)
           cookie?.let {
               when {
                   cookie.contains("sessionid=") -> {
                       val userId = extractCookie(cookie, "sessionid=")
                       accountManager.saveAccount(Account(userId.toLong(),cookie))
                       app.currentAccount = Account(userId.toLong(),cookie)
                       clearCookies()
                       navToHome.value = true
                   }
                   cookie.contains("db_user_id") -> {
                       val userId = extractCookie(cookie, "ds_user_id=")
                       accountManager.saveAccount(Account(userId.toLong(),cookie))
                       clearCookies()
                       app.currentAccount = Account(userId.toLong(),cookie)
                       navToHome.value = true
                   }
                   else -> invalidCookie.value = "Invalid cookie"
               }
           }

       }

   }

    private fun clearCookies() {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    }


    fun popToHomeFragment(){
       popToHome.value = true
    }

    private fun getCookie(url:String) = CookieManager.getInstance().getCookie(url)





}

