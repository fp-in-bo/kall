package com.fpinbo.kall.response

import com.fpinbo.kall.category.UnitTest
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert.*
import org.junit.Test
import org.junit.experimental.categories.Category

@Category(UnitTest::class)
class TestApplicativeResponse {

    @Test
    fun just() {
        val response = Response.just("Value")

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
        val response = Response("Value")

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
        val first = Response("value")
        val second = Response<(String) -> String>({ it.toUpperCase() })

        val result = first.ap(second)

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
        val first = Response("value")
        val second = Response.Error<(String) -> String>(
                ResponseBody.create(MediaType.parse("application/json"), "second error content"),
                404,
                Headers.of(mapOf("headerKey" to "headerValue")),
                "second error message"
        )

        val result = first.ap(second)

        result.fold({
            assertEquals("second error content", it.errorBody.string())
            assertEquals(404, it.code)
            assertEquals(Headers.of(mapOf("headerKey" to "headerValue")), it.headers)
            assertEquals("second error message", it.message)
        }, {
            fail()
        })
    }

    @Test
    fun apFirstError() {
        val first = Response.Error<String>(
                ResponseBody.create(MediaType.parse("application/json"), "first error content"),
                500,
                Headers.of(mapOf("headerKey" to "headerValue")),
                "server error"
        )

        val second = Response<(String) -> String>({ it.toUpperCase() })

        val result = first.ap(second)

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

        val first = Response.Error<String>(
                ResponseBody.create(MediaType.parse("application/json"), "first error content"),
                500,
                Headers.of(mapOf("FirstHeaderKey" to "FirstHeaderValue")),
                "server error"
        )

        val second = Response.Error<(String) -> String>(
                ResponseBody.create(MediaType.parse("application/json"), "second error content"),
                404,
                Headers.of(mapOf("SecondHeaderKey" to "SecondHeaderValue")),
                "second error message"
        )
        val result = first.ap(second)

        result.fold({
            assertEquals("first error content", it.errorBody.string())
            assertEquals(500, it.code)
            assertEquals(Headers.of(mapOf("FirstHeaderKey" to "FirstHeaderValue")), it.headers)
            assertEquals("server error", it.message)
        }, {
            fail()
        })
    }
}