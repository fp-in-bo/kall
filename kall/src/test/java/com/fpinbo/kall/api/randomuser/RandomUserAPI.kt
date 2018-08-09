package com.fpinbo.kall.api.randomuser

import com.fpinbo.kall.Kall
import com.fpinbo.kall.KallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET

interface RandomUserAPI {

    companion object {
        operator fun invoke(retrofit: Retrofit = instance): RandomUserAPI {
            return retrofit.create(RandomUserAPI::class.java)
        }

        private val instance: Retrofit by lazy {
            Retrofit.Builder()
                    .baseUrl("https://randomuser.me/api/")
                    .addConverterFactory(JacksonConverterFactory.create())
                    .addCallAdapterFactory(KallAdapterFactory())
                    .client(OkHttpClient.Builder()
                            .addInterceptor(HttpLoggingInterceptor().apply {
                                level = HttpLoggingInterceptor.Level.BODY
                            })
                            .build())
                    .build()
        }
    }

    @GET(" ")
    fun getUser(): Kall<Response>
}