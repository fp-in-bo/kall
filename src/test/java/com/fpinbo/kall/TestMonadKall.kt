package com.fpinbo.kall

import com.fpinbo.kall.api.GitHubAPI
import com.fpinbo.kall.api.User
import com.fpinbo.kall.category.IntegrationTest
import com.fpinbo.kall.response.fold
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.junit.experimental.categories.Category
import java.util.concurrent.CountDownLatch

@Category(IntegrationTest::class)
class TestMonadKall {

    val api = GitHubAPI()

    val dcampogiani = User("dcampogiani", "https://api.github.com/users/dcampogiani/followers")

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

    @Test
    fun flatMapExecuteAsyncSuccess() {
        val latch = CountDownLatch(1)

        val call = api.getUser("dcampogiani").flatMap {
            api.getFollowers(it.followersUrl)
        }

        call.executeAsync(
                onResponse = { _, response ->
                    response.fold(
                            { fail() },
                            {
                                assertEquals("mattpoggi", it.body.first().login)
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
}