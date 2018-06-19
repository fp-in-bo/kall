package com.fpinbo.kall

import com.fpinbo.kall.category.UnitTest
import com.fpinbo.kall.response.Response
import com.fpinbo.kall.response.fold
import junit.framework.TestCase.*
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Test
import org.junit.experimental.categories.Category

@Category(UnitTest::class)
class TestApplicativeKall {

    @Test
    fun just() {
        val kall = Kall.just("Value")
        val response = kall.execute()

        response.fold(
                { fail() },
                {
                    assertEquals("Value", it.body)
                    assertEquals(200, it.code)
                    assertEquals(0, it.headers.size())
                    assertNull(it.message)
                })
    }

    @Test
    fun invokeJust() {
        val kall = Kall("Value")
        val response = kall.execute()

        response.fold(
                { fail() },
                {
                    assertEquals("Value", it.body)
                    assertEquals(200, it.code)
                    assertEquals(0, it.headers.size())
                    assertNull(it.message)
                })
    }

    @Test
    fun apBothSuccess() {
        val firstKall = Kall("value")
        val secondKall = Kall<(String) -> String>({ it.toUpperCase() })

        val apKall = firstKall.ap(secondKall)
        val result = apKall.execute()

        result.fold(
                { fail() },
                {
                    assertEquals("VALUE", it.body)
                    assertEquals(200, it.code)
                    assertEquals(0, it.headers.size())
                    assertNull(it.message)
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