package com.example.runpal.server

import com.example.runpal.ServerException
import com.example.runpal.SERVER_ADDRESS
import com.example.runpal.repositories.LoginManager
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

        val scalars = ScalarsConverterFactory.create()
        val gson = GsonBuilder()
            //.registerTypeAdapter(ZonedDateTime::class.java, ZonedDateTimeAdapter())
            .create()


        val retrofit = Retrofit.Builder()
            .baseUrl(SERVER_ADDRESS)
            .client(client)
            .addConverterFactory(scalars)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit
    }
    @Singleton
    @Provides
    @AuthorizedRetrofit
    fun provideAuthorizedRetrofit(loginManager: LoginManager): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val authorizationInterceptor = object: Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val token = loginManager.currentToken()
                if (token == null) throw ServerException("Token expired.")
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

        val scalars = ScalarsConverterFactory.create()
        val gson = GsonBuilder()
            //.registerTypeAdapter(ZonedDateTime::class.java, ZonedDateTimeAdapter())
            .create()


        val retrofit = Retrofit.Builder()
            .baseUrl(SERVER_ADDRESS)
            .client(client)
            .addConverterFactory(scalars)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit
    }


    @Singleton
    @Provides
    fun provideLoginApi(@UnauthorizedRetrofit retrofit: Retrofit): LoginApi = retrofit.create(LoginApi::class.java)

    @Singleton
    @Provides
    fun provideRunApi(@AuthorizedRetrofit retrofit: Retrofit): RunApi = retrofit.create(RunApi::class.java)

    @Singleton
    @Provides
    fun provideUserApi(@AuthorizedRetrofit retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Singleton
    @Provides
    fun provideUploadApi(@AuthorizedRetrofit retrofit: Retrofit): UploadApi = retrofit.create(UploadApi::class.java)

    @Singleton
    @Provides
    fun provideRoomApi(@AuthorizedRetrofit retrofit: Retrofit): RoomApi = retrofit.create((RoomApi::class.java))

    @Singleton
    @Provides
    fun provideEventApi(@AuthorizedRetrofit retrofit: Retrofit): EventApi = retrofit.create((EventApi::class.java))
}