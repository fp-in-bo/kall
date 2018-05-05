package com.fpinbo.kall

import com.fpinbo.kall.api.GitHubAPI
import com.fpinbo.kall.api.User
import com.fpinbo.kall.response.fold
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import java.util.concurrent.CountDownLatch

class TestKall {

    val api = GitHubAPI()

    val dcampogiani = User("dcampogiani", "https://api.github.com/users/dcampogiani/followers")

    @Test
    fun executeSuccess() {
        val call = api.getUser("dcampogiani")
        val response = call.execute()

        response.fold(
            { fail() },
            { assertEquals(dcampogiani, it.body) })
    }

    @Test
    fun executeAsyncSuccess() {
        val latch = CountDownLatch(1)

        val call = api.getUser("dcampogiani")
        call.executeAsync(
            onResponse = { _, response ->
                response.fold(
                    { fail() },
                    {
                        assertEquals(dcampogiani, it.body)
                        latch.countDown()
                    })
            },
            onFailure = { _, _ ->
                latch.countDown()
                fail()
            }
        )

        latch.await()
    }

    @Test
    fun flatMapSuccess() {
        val call = api.getUser("dcampogiani").flatMap {
            api.getFollowers(it.followersUrl)
        }
        val response = call.execute()

        response.fold(
            { fail() },
            { assertEquals("mattpoggi", it.body.first().login) })
    }

    @Test
    fun flatMapError() {
        val call = api.getUser("-1").flatMap {
            api.getFollowers(it.followersUrl)
        }
        val response = call.execute()

        response.fold(
            { assertEquals(404, response.code) },
            { fail() })
    }
}