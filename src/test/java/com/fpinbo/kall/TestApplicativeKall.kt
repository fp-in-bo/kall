package com.fpinbo.kall

import com.fpinbo.kall.response.Response
import com.fpinbo.kall.response.fold
import junit.framework.Assert.assertEquals
import junit.framework.Assert.fail
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Test

class TestApplicativeKall {

    @Test
    fun just() {
        val kall = Kall.just("Value")
        val response = kall.execute()

        response.fold(
            { Assert.fail() },
            {
                Assert.assertEquals("Value", it.body)
                Assert.assertEquals(200, it.code)
                Assert.assertEquals(0, it.headers.size())
                Assert.assertNull(it.message)
            })
    }

    @Test
    fun invokeJust() {
        val kall = Kall("Value")
        val response = kall.execute()

        response.fold(
            { Assert.fail() },
            {
                Assert.assertEquals("Value", it.body)
                Assert.assertEquals(200, it.code)
                Assert.assertEquals(0, it.headers.size())
                Assert.assertNull(it.message)
            })
    }

    @Test
    fun apBothSuccess() {
        val firstKall = Kall("value")
        val secondKall = Kall<(String) -> String>({ it.toUpperCase() })

        val apKall = firstKall.ap(secondKall)
        val result = apKall.execute()

        result.fold(
            { Assert.fail() },
            {
                Assert.assertEquals("VALUE", it.body)
                Assert.assertEquals(200, it.code)
                Assert.assertEquals(0, it.headers.size())
                Assert.assertNull(it.message)
            })
    }

    @Test
    fun apSecondError() {
        val firstKall = Kall("value")
        val secondKall = Kall.error<(String) -> String>(
            Response.Error<Nothing>(
                ResponseBody.create(MediaType.parse("application/json"), "second error content"),
                404,
                Headers.of(mapOf("headerKey" to "headerValue")),
                "second error message"
            )
        )

        val apKall = firstKall.ap(secondKall)
        val result = apKall.execute()

        result.fold(
            {
                assertEquals("second error content", it.errorBody.string())
                assertEquals(404, it.code)
                assertEquals(Headers.of(mapOf("headerKey" to "headerValue")), it.headers)
                assertEquals("second error message", it.message)

            },
            {
                fail()
            })
    }

    @Test
    fun apFirstError() {
        val firstKall = Kall.error(Response.Error<String>(
            ResponseBody.create(MediaType.parse("application/json"), "first error content"),
            500,
            Headers.of(mapOf("headerKey" to "headerValue")),
            "server error"
        ))

        val secondKall = Kall<(String) -> String>({ it.toUpperCase() })

        val apKall = firstKall.ap(secondKall)
        val result = apKall.execute()

        result.fold({
            assertEquals("first error content", it.errorBody.string())
            assertEquals(500, it.code)
            assertEquals(Headers.of(mapOf("headerKey" to "headerValue")), it.headers)
            assertEquals("server error", it.message)
        }, {
            fail()
        })
    }

    @Test
    fun apBothError() {

        val firstKall = Kall.error(Response.Error<String>(
            ResponseBody.create(MediaType.parse("application/json"), "first error content"),
            500,
            Headers.of(mapOf("FirstHeaderKey" to "FirstHeaderValue")),
            "server error"
        ))

        val secondKall = Kall.error(Response.Error<(String) -> String>(
            ResponseBody.create(MediaType.parse("application/json"), "second error content"),
            404,
            Headers.of(mapOf("SecondHeaderKey" to "SecondHeaderValue")),
            "second error message"
        ))

        val apKall = firstKall.ap(secondKall)
        val result = apKall.execute()

        result.fold({
            assertEquals("second error content", it.errorBody.string())
            assertEquals(404, it.code)
            assertEquals(Headers.of(mapOf("SecondHeaderKey" to "SecondHeaderValue")), it.headers)
            assertEquals("second error message", it.message)
        }, {
            fail()
        })
    }
}