package com.example.instacleaner.di

import com.example.instacleaner.data.InstaApi
import com.example.instacleaner.repositories.InstaRepository
import com.example.instacleaner.utils.Constance.ACCEPT_ENCODING
import com.example.instacleaner.utils.Constance.ACCEPT_ENCODING_KEY
import com.example.instacleaner.utils.Constance.ACCEPT_LANGUAGE_KEY
import com.example.instacleaner.utils.Constance.BASE_URL
import com.example.instacleaner.utils.Constance.HEADER_ACCEPT_LANGUAGE
import com.example.instacleaner.utils.Constance.HEADER_X_IG_APP_ID
import com.example.instacleaner.utils.Constance.X_BLOCKS_IS_LAYOUT_RTL
import com.example.instacleaner.utils.Constance.X_BLOCKS_IS_LAYOUT_RTL_KEY
import com.example.instacleaner.utils.Constance.X_BLOCKS_VERSION_ID
import com.example.instacleaner.utils.Constance.X_BLOCKS_VERSION_ID_KEY
import com.example.instacleaner.utils.Constance.X_FB_HTTP_ENGINE
import com.example.instacleaner.utils.Constance.X_FB_HTTP_ENGINE_KEY
import com.example.instacleaner.utils.Constance.X_IG_APP_ID_KEY
import com.example.instacleaner.utils.Constance.X_IG_APP_LOCALE
import com.example.instacleaner.utils.Constance.X_IG_APP_LOCALE_KEY
import com.example.instacleaner.utils.Constance.X_IG_BANDWIDTH_TOTAL_TIME_MS_KEY
import com.example.instacleaner.utils.Constance.X_IG_BAND_WITH_SPEED_KBS_KEY
import com.example.instacleaner.utils.Constance.X_IG_BAND_WITH_TOTAL_BYTE_B_KEY
import com.example.instacleaner.utils.Constance.X_IG_BAND__TOTAL_BYTES_B_KEY
import com.example.instacleaner.utils.Constance.X_IG_CAPABILITIES
import com.example.instacleaner.utils.Constance.X_IG_CAPABILITIES_KEY
import com.example.instacleaner.utils.Constance.X_IG_CONNECTION_SPEED
import com.example.instacleaner.utils.Constance.X_IG_CONNECTION_SPEED_KEY
import com.example.instacleaner.utils.Constance.X_IG_CONNECTION_TYPE
import com.example.instacleaner.utils.Constance.X_IG_CONNECTION_TYPE_KEY
import com.example.instacleaner.utils.Constance.X_IG_DEVICE_LOCALE_KEY
import com.example.instacleaner.utils.Constance.X_PIGEON_RAW_CLIENT_TIME_KEY
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {



    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        return logging
    }


    @Singleton
    @Provides
    fun providesInterceptor(): Interceptor {

        return Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder().apply {
                addHeader(ACCEPT_ENCODING_KEY, ACCEPT_ENCODING)
                addHeader(X_IG_APP_ID_KEY, HEADER_X_IG_APP_ID)
                addHeader(ACCEPT_LANGUAGE_KEY, HEADER_ACCEPT_LANGUAGE)
                addHeader(X_IG_APP_LOCALE_KEY, X_IG_APP_LOCALE)
                addHeader(X_IG_BANDWIDTH_TOTAL_TIME_MS_KEY, "${Random().nextInt(90000) + 200000}")
                addHeader(X_FB_HTTP_ENGINE_KEY, X_FB_HTTP_ENGINE)
                addHeader(X_IG_BAND__TOTAL_BYTES_B_KEY, "${Random().nextInt(9999999) + 90000000}")
                addHeader(X_IG_CAPABILITIES_KEY, X_IG_CAPABILITIES)
                addHeader(
                    X_IG_BAND_WITH_TOTAL_BYTE_B_KEY,
                    "${Random().nextInt(9999999) + 90000000}"
                )
                addHeader(X_IG_DEVICE_LOCALE_KEY, X_IG_DEVICE_LOCALE_KEY)
                addHeader(X_BLOCKS_VERSION_ID_KEY, X_BLOCKS_VERSION_ID)
                addHeader(X_IG_CONNECTION_TYPE_KEY, X_IG_CONNECTION_TYPE)
                addHeader(X_IG_CONNECTION_SPEED_KEY, X_IG_CONNECTION_SPEED)
                addHeader(X_BLOCKS_IS_LAYOUT_RTL_KEY, X_BLOCKS_IS_LAYOUT_RTL)
                addHeader(
                    X_IG_BAND_WITH_SPEED_KBS_KEY,
                    (Random().nextInt(450) + 250).toString() + "" + (Random().nextInt(899) + 100) + ""
                )
                addHeader(
                    X_PIGEON_RAW_CLIENT_TIME_KEY,
                    System.currentTimeMillis().toString() + "" + Random().nextInt(3) + ""
                )

            }

            chain.proceed(requestBuilder.build())
        }

    }



    @Singleton
    @Provides
    fun providesOkHttpClient(interceptor: Interceptor, httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(interceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()



    @Provides
    @Singleton
    fun provideInstaApi(client: OkHttpClient):InstaApi{
        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(InstaApi::class.java)
    }


    @Provides
    @Singleton
    fun providesInstaRepository(instaApi: InstaApi) = InstaRepository(instaApi)

}