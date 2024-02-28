package com.example.racepal

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.ZonedDateTime
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class ServerModule {
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        val httpInterceptor = HttpLoggingInterceptor()
        httpInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(httpInterceptor).build()

        val gson = GsonBuilder()
            //.registerTypeAdapter(ZonedDateTime::class.java, ZonedDateTimeAdapter())
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(SERVER_ADDRESS)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit
    }

    @Singleton
    @Provides
    fun provideLoginClient(retrofit: Retrofit): LoginClient {
        return retrofit.create(LoginClient::class.java)
    }
}