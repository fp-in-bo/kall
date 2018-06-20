package com.fpinbo.kall.api.github

import com.fpinbo.kall.Kall
import com.fpinbo.kall.KallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface GitHubAPI {

    companion object {
        operator fun invoke(retrofit: Retrofit = instance): GitHubAPI {
            return retrofit.create(GitHubAPI::class.java)
        }

        private val instance: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl("https://api.github.com/")
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

    @GET("repos/{owner}/{repoName}/stargazers")
    fun getStargazers(@Path("owner") owner: String, @Path("repoName") repoName: String): Kall<List<Stargazer>>

    @GET("users/{username}")
    fun getUser(@Path("username") userName: String): Kall<User>

    @GET
    fun getFollowers(@Url url: String): Kall<List<User>>
}