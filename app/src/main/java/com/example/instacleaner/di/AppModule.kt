package com.example.instacleaner.di

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import com.example.instacleaner.App
import com.example.instacleaner.repositories.InstaRepository
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



    @Singleton
    @Provides
    fun providesClipboardManager(@ApplicationContext context:Context):ClipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager



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



    @Singleton
    @Provides
    fun providesDownloadManager(@ApplicationContext context: Context) =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager














}