package com.fpinbo.kall

import com.fpinbo.kall.api.jokes.JokesAPI
import com.fpinbo.kall.api.randomuser.RandomUserAPI
import com.fpinbo.kall.response.fold
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class TestUserFact {

    val userApi = RandomUserAPI()
    val jokeApi = JokesAPI()

    @Test
    fun userApi() {
        val call = userApi.getUser()
        val response = call.execute()

        response.fold(
            { fail() },
            {
                assertTrue(!it.body.results.first().name.first.isEmpty())
                assertTrue(!it.body.results.first().name.last.isEmpty())
            })
    }

    @Test
    fun jokeApi() {
        val call = jokeApi.getJoke("Daniele", "Campogiani")
        val response = call.execute()

        response.fold(
            { fail() },
            {
                assertTrue(!it.body.value.joke.isEmpty())
            })
    }

    @Test
    fun getUserAndThenJoke() {
        val call = userApi.getUser().map { Pair(it.results.first().name.first, it.results.first().name.last) }
            .flatMap { jokeApi.getJoke(it.first, it.second) }
        val response = call.execute()

        response.fold(
            { fail() },
            {
                assertTrue(!it.body.value.joke.isEmpty())
            })
    }
}