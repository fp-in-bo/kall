package com.fpinbo.kall.api.jokes

import com.fpinbo.kall.Kall
import com.fpinbo.kall.KallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface JokesAPI {

    companion object {
        operator fun invoke(retrofit: Retrofit = instance): JokesAPI {
            return retrofit.create(JokesAPI::class.java)
        }

        private val instance: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl("http://api.icndb.com/jokes/")
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

    @GET("random")
    fun getJoke(@Query("firstName") firstName: String, @Query("lastName") lastName: String): Kall<Response>
}