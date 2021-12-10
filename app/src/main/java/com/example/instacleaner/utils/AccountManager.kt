package com.example.instacleaner.utils


import com.example.instacleaner.data.local.Account
import com.example.instacleaner.utils.Constance.ACCOUNT
import com.example.instacleaner.utils.Constance.CURRENT_ACCOUNT
import com.example.instacleaner.utils.Constance.IS_LOGIN
import com.example.mohamadkh_instacleaner.data.remote.response.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class AccountManager@Inject constructor(private  val prefManager:PreferenceManager){
    private val accountsStringFromSharePref = prefManager.getString(Constance.ACCOUNT)
    private val accountListTypeForGson =  object : TypeToken<List<Account>>() {}.type
    private val accounts = Gson().fromJson<ArrayList<Account>>(accountsStringFromSharePref, accountListTypeForGson)
    private val currentAccount = prefManager.getLong(CURRENT_ACCOUNT)

    private fun isAccountExists(account:Account):Boolean{
        accounts?.let { accounts ->
            accounts.forEach{
                if (account.cookie == it.cookie) return true
            }
        }

        return false
    }

    fun saveAccount(account:Account){
        if (isAccountExists(account)) return
        accounts?.let {
          saveAccountHelper(it,account)
        }?:kotlin.run {
            val accounts = arrayListOf<Account>()
            saveAccountHelper(accounts,account)
        }



    }
     fun updateAccount(user: User,callback:(accounts:ArrayList<Account>)->Unit){
         accounts?.let {  accounts ->
             accounts.find { it.userId == user.pk }?.user = user
             updateAccountHelper(accounts)
             callback(accounts)
         }
     }

    private fun updateAccountHelper(accounts:ArrayList<Account>){
        val accountsJson = Gson().toJson(accounts)
        prefManager.set(ACCOUNT,accountsJson)
    }

    private fun saveAccountHelper(accounts:ArrayList<Account>,account: Account){
        accounts.add(account)
        val accountsJson = Gson().toJson(accounts)
        prefManager.set(CURRENT_ACCOUNT,account.userId)
        prefManager.set(ACCOUNT,accountsJson)
        prefManager.set(IS_LOGIN,true)
    }
    fun getAccounts() = accounts

    fun isLogin() = prefManager.getBoolean(IS_LOGIN)

    fun getCurrentAccount() = accounts.find { it.userId == currentAccount }
}