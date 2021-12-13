package com.example.instacleaner.repositories

import android.os.Build
import com.example.instacleaner.data.InstaApi
import com.example.instacleaner.data.local.Account
import com.example.instacleaner.utils.AccountManager
import com.example.instacleaner.utils.Constance.IG_VERSION
import com.example.instacleaner.utils.Constance.VERSION_CODE
import com.example.instacleaner.utils.Resource
import com.example.instacleaner.utils.handleResponse
import com.example.mohamadkh_instacleaner.data.remote.response.userFollowers.UserFollowers
import com.example.mohamadkh_instacleaner.data.remote.response.userFollowings.UserFollowings
import com.example.mohamadkh_instacleaner.data.remote.response.userInfo.UserInfo
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap


class InstaRepository @Inject constructor(private val instaApi:InstaApi) {

    suspend fun getUserInfo(account:Account): Resource<UserInfo> {

     return try {
          val response = instaApi.getUserInfo(getUserHeaders(account.cookie),account.user.pk)
          handleResponse(response)
      }catch (t:Throwable){
          return Resource.Error(t.message!!)
      }

    }

    suspend fun getFollower(account: Account):Resource<UserFollowers>{
        return try {
            val response = instaApi.getFollowers(getUserHeaders(account.cookie),account.user.pk,"")
            handleResponse(response)
        }catch (t:Throwable){
            return Resource.Error(t.message!!)
        }
    }


    suspend fun getFollowing(account:Account):Resource<UserFollowings>{
        return try {
        val response = instaApi.getFollowings(getUserHeaders(account.cookie),account.user.pk)
            handleResponse(response)
        }catch (t:Throwable){
            return Resource.Error(t.message!!)
        }
    }



    private fun getUserHeaders(cookie:String): HashMap<String, String> {

            val androidId = "android-3cf096f03b6c12064f5"
            val agent = "832dpi; 1349x1184; samsung; SM-T292; gts210velte; qcom"
            val userAgent =
                "Instagram " + IG_VERSION + " Android (" + Build.VERSION.SDK_INT.toString() + "/".toString() + Build.VERSION.RELEASE
                    .toString() + "; " + agent + "; en_US; " + VERSION_CODE + ")"
            return hashMapOf(
                "Cookie" to cookie,
                "User-Agent" to userAgent,
                "X-IG-Device-ID" to UUID.randomUUID().toString(),
                "X-DEVICE-ID" to androidId,
                "X-IG-Android-ID" to androidId,
                "X-Pigeon-Session-Id" to "4045236099%3AzGHlHHhyiwhtK6%3A8",
            )
        }


    }


