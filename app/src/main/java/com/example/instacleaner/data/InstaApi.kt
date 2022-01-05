package com.example.instacleaner.data

import com.example.instacleaner.data.remote.response.userFollowers.UserFollowers
import com.example.mohamadkh_instacleaner.data.remote.response.userFollowings.UserFollowings
import com.example.mohamadkh_instacleaner.data.remote.response.userInfo.UserInfo
import retrofit2.Response
import retrofit2.http.*

interface InstaApi {

    @GET("users/{userId}/info")
    suspend fun getUserInfo(
        @HeaderMap map:Map<String,String>,
        @Path("userId")userId:Long): Response<UserInfo>


    @GET("friendships/{userId}/following")
    suspend fun getFollowings(
        @HeaderMap map:Map<String,String>,
        @Path("userId")userId: Long
    ): Response<UserFollowings>


    @GET("friendships/{userId}/followers/")
    suspend fun getFollowers(
        @HeaderMap map:Map<String,String>,
        @Path("userId") userId:Long,
        @Query("max_id")maxId:String
    ): Response<UserFollowers>


    @GET("friendships/create/{userId}/")
    suspend fun follow(
        @HeaderMap map:Map<String,String>,
        @Path("userId") userId:Long,
        )





}