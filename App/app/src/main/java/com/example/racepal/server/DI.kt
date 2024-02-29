package com.example.racepal.server

import com.example.racepal.SERVER_ADDRESS
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class UnauthorizedRetrofit

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthorizedRetrofit

@Module
@InstallIn(SingletonComponent::class)
class ServerModule {
    @Singleton
    @Provides
    @UnauthorizedRetrofit
    fun provideUnauthorizedRetrofit(): Retrofit {
        val httpInterceptor = HttpLoggingInterceptor()
        httpInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(httpInterceptor).build()

        val gson = GsonBuilder()
            //.registerTypeAdapter(ZonedDateTime::class.java, ZonedDateTimeAdapter())
            .create()
        val scalars = ScalarsConverterFactory.create()

        val retrofit = Retrofit.Builder()
            .baseUrl(SERVER_ADDRESS)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(scalars)
            .build()

        return retrofit
    }
    @Singleton
    @Provides
    @AuthorizedRetrofit
    fun provideAuthorizedRetrofit(): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val authorizationInterceptor = object: Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                /*
                TO DO - Implement JWT token authorization
                 */
                val token = ""
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                return chain.proceed(request)
            }

        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authorizationInterceptor)
            .build()

        val gson = GsonBuilder()
            //.registerTypeAdapter(ZonedDateTime::class.java, ZonedDateTimeAdapter())
            .create()
        val scalars = ScalarsConverterFactory.create()

        val retrofit = Retrofit.Builder()
            .baseUrl(SERVER_ADDRESS)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(scalars)
            .build()

        return retrofit
    }


    @Singleton
    @Provides
    fun provideLoginApi(@UnauthorizedRetrofit retrofit: Retrofit): LoginApi {
        return retrofit.create(LoginApi::class.java)
    }

    @Singleton
    @Provides
    fun provideRunApi(@AuthorizedRetrofit retrofit: Retrofit): RunApi {
        return retrofit.create(RunApi::class.java)
    }

    @Singleton
    @Provides
    fun provideUserApi(@AuthorizedRetrofit retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)
}