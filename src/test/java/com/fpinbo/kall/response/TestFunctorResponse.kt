package com.fpinbo.kall.response

import com.fpinbo.kall.api.github.GitHubAPI
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class TestFunctorResponse {

    val api = GitHubAPI()

    @Test
    fun success() {

        val call = api.getStargazers("dcampogiani", "AndroidFunctionalValidation")
        val response = call.execute()

        response.fold(
            { fail() },
            {
                assertEquals(200, response.code)
                assertTrue(it.body.isNotEmpty())
            })
    }

    @Test
    fun error() {
        val call = api.getStargazers("dcampogiani", "NotValid")
        val response = call.execute()

        response.fold(
            { assertEquals(404, response.code) },
            { fail() })
    }

    @Test
    fun mapSuccess() {
        val call = api.getStargazers("dcampogiani", "AndroidFunctionalValidation")
        val response = call.execute()
        val mappedResponse = response.map { it.map { it.copy(userName = it.userName.toUpperCase()) } }

        mappedResponse.fold(
            { fail() },
            {
                assertEquals(200, it.code)
                assertEquals("AJOZ", it.body.first().userName)
            })
    }

    @Test
    fun mapError() {
        val call = api.getStargazers("dcampogiani", "NotValid")
        val response = call.execute()
        val mappedResponse = response.map { "Dummy Mapping" }

        mappedResponse.fold(
            { assertEquals(404, response.code) },
            { fail() })
    }
}