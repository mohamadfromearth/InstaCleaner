package com.example.instacleaner.utils


import com.example.instacleaner.data.local.Account
import com.example.instacleaner.utils.Constance.ACCOUNT
import com.example.instacleaner.utils.Constance.CURRENT_ACCOUNT
import com.example.instacleaner.data.remote.response.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class AccountManager@Inject constructor(private  val prefManager:PreferenceManager){




    private var accountsStringFromSharePref = prefManager.getString(ACCOUNT)
    private var accountListTypeForGson =  object : TypeToken<List<Account>>() {}.type
    private var accounts : ArrayList<Account> = Gson().fromJson(accountsStringFromSharePref, accountListTypeForGson) ?: arrayListOf()
    private var currentAccount = prefManager.getLong(CURRENT_ACCOUNT)
    private fun isAccountExists(account:Account)= accounts.any { it.user.pk == account.user.pk }

    fun saveAccount(account:Account){
        if (isAccountExists(account)) return
        saveAccountHelper(account)

    }

    fun getCurrentAccountId() = prefManager.getLong(CURRENT_ACCOUNT)



    fun setCurrentAccount(userId:Long){
        prefManager.set(CURRENT_ACCOUNT,userId)
    }

     fun getAccounts():ArrayList<Account>{
         accounts.forEach { it.isSelected = it.user.pk == currentAccount }
        return accounts
    }


     fun removeAccount():ArrayList<Account>{
         accounts.remove(getCurrentAccount())
         if (accounts.size != 0){
             currentAccount = accounts[0].user.pk
             prefManager.set(CURRENT_ACCOUNT,currentAccount)
             updateAccountHelper(accounts)
             return accounts
         }else{
             prefManager.clear()
             return accounts
         }


     }



     fun updateAccount(user: User) : ArrayList<Account> {
         accounts.first { it.user.pk == user.pk }.user = user
         currentAccount = user.pk
         prefManager.set(CURRENT_ACCOUNT,currentAccount)
        saveAccount()
         return getAccounts()
     }



    fun saveAccount(){
        val accountsJson = Gson().toJson(accounts)
        prefManager.set(ACCOUNT,accountsJson)
    }


    private fun updateAccountHelper(accounts:ArrayList<Account>){
        val accountsJson = Gson().toJson(accounts)
        prefManager.set(ACCOUNT,accountsJson)
    }

    private fun saveAccountHelper(account: Account){
        accounts.add(account)
        val accountsJson = Gson().toJson(accounts)
        currentAccount = account.user.pk
        prefManager.set(CURRENT_ACCOUNT,account.user.pk)
        prefManager.set(ACCOUNT,accountsJson)
//        prefManager.set(IS_LOGIN,true)
    }
    //private fun getAccounts() = accounts

    fun isLogin() = accounts.isEmpty().not()

    fun getCurrentAccount() = accounts.first { it.user.pk == currentAccount }
}