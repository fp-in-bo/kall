package com.fpinbo.kall

import com.fpinbo.kall.api.GitHubAPI
import com.fpinbo.kall.response.fold
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class TestFunctorKall {

    val api = GitHubAPI()

    @Test
    fun mapSuccess() {
        val call = api.getUser("dcampogiani").map { it.login.toUpperCase() }
        val response = call.execute()

        response.fold(
            { fail() },
            { assertEquals("DCAMPOGIANI", it.body) })
    }

    @Test
    fun mapError() {
        val call = api.getStargazers("dcampogiani", "NotValid").map { "Dummy Mapping" }
        val response = call.execute()

        response.fold(
            { assertEquals(404, response.code) },
            { fail() })
    }
}