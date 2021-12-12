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
    private var accountsStringFromSharePref = prefManager.getString(Constance.ACCOUNT)
    private var accountListTypeForGson =  object : TypeToken<List<Account>>() {}.type
    private var accounts = Gson().fromJson<ArrayList<Account>>(accountsStringFromSharePref, accountListTypeForGson)
    private var currentAccount = prefManager.getLong(CURRENT_ACCOUNT)


    fun refreshSharePreferenceValue(){
         accountsStringFromSharePref = prefManager.getString(Constance.ACCOUNT)
         accountListTypeForGson =  object : TypeToken<List<Account>>() {}.type
         accounts = Gson().fromJson<ArrayList<Account>>(accountsStringFromSharePref, accountListTypeForGson)
         currentAccount = prefManager.getLong(CURRENT_ACCOUNT)
    }

    private fun isAccountExists(account:Account):Boolean{
        accounts?.let { accounts ->
            accounts.forEach{
                if (account.userId == it.userId) return true
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

    fun setCurrentAccount(userId:Long){
        prefManager.set(CURRENT_ACCOUNT,userId)
    }

     fun getAccounts():ArrayList<Account>{
        return accounts
    }


     fun updateAccount(user: User,account:Account,callback:(accounts:ArrayList<Account>)->Unit){
         accounts?.let {  accounts ->
              accounts.first { it.userId == user.pk }.user = user
              prefManager.set(CURRENT_ACCOUNT,account.userId)
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
    //private fun getAccounts() = accounts

    fun isLogin() = prefManager.getBoolean(IS_LOGIN)

    fun getCurrentAccount() = accounts.find { it.userId == currentAccount }
}