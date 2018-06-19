package com.fpinbo.kall

import com.fpinbo.kall.api.github.GitHubAPI
import com.fpinbo.kall.api.github.User
import com.fpinbo.kall.category.IntegrationTest
import com.fpinbo.kall.response.fold
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.junit.experimental.categories.Category
import java.util.concurrent.CountDownLatch

@Category(IntegrationTest::class)
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
}