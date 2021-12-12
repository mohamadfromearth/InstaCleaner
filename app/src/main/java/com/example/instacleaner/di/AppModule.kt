package com.example.instacleaner.di

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import com.example.instacleaner.App
import com.example.instacleaner.utils.AccountManager
import com.example.instacleaner.utils.Constance.APPLICATION_SHARE_PREF
import com.example.instacleaner.utils.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesSharePreference(@ApplicationContext context:Context) =
        context.applicationContext.getSharedPreferences(APPLICATION_SHARE_PREF,
            Context.MODE_PRIVATE
        )



    @SuppressLint("HardwareIds")
    @Singleton
    @Provides
    fun providesDeviceID(@ApplicationContext context: Context) =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)


    @Singleton
    @Provides
    fun provideApplicationClass(@ApplicationContext app: Context): App =
        app as App


    @Singleton
    @Provides
    fun providesPreferenceManager(sharePref: SharedPreferences): PreferenceManager =
        PreferenceManager(sharePref)



    @Singleton
    @Provides
    fun providesAccountManager(preferenceManager: PreferenceManager): AccountManager =
        AccountManager(preferenceManager)














}