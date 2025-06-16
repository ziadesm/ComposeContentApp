package com.example.contentapp.di
import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.contentapp.data.api.ContentApiService
import com.example.contentapp.data.api.SearchApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HomeRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SearchRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val HOME_API_BASE_URL = "https://api-v2-b2sit6oh3a-uc.a.run.app/"
    private const val SEARCH_API_BASE_URL = "https://mock.apidog.com/m1/735111-711675-default/"

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(ChuckerInterceptor(context))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @HomeRetrofit
    fun provideHomeRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(HOME_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @SearchRetrofit
    fun provideSearchRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SEARCH_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideContentApiService(@HomeRetrofit retrofit: Retrofit): ContentApiService {
        return retrofit.create(ContentApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSearchApiService(@SearchRetrofit retrofit: Retrofit): SearchApiService {
        return retrofit.create(SearchApiService::class.java)
    }
}

