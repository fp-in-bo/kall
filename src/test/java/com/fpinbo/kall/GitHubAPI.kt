package com.fpinbo.kall

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface GitHubAPI {

    @GET("repos/{owner}/{repoName}/stargazers")
    fun getStargazers(@Path("owner") owner: String, @Path("repoName") repoName: String): Kall<List<Stargazer>>

    @GET("users/{username}")
    fun getUser(@Path("username") userName: String): Kall<User>

    @GET
    fun getFollowers(@Url url: String): Kall<List<User>>

}