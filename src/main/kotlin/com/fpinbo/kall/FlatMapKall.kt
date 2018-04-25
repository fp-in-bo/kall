package com.fpinbo.kall

import com.fpinbo.kall.response.Response
import com.fpinbo.kall.response.fold
import okhttp3.Request

class FlatMapKall<A, B>(
    private val originalKall: Kall<A>,
    private val f: (A) -> Kall<B>
) : Kall<B> {

    override fun cancel() = originalKall.cancel()

    override fun clone() = this

    override fun execute(): Response<B> {
        val response = originalKall.execute()

        return buildResponse(response)
    }

    override fun executeAsync(onResponse: (Kall<B>, Response<B>) -> Unit, onFailure: (Kall<B>, Throwable) -> Unit) {
        originalKall.executeAsync({ _, response ->
            onResponse(this, buildResponse(response))
        }, { _, throwable ->
            onFailure(this, throwable)
        })
    }

    private fun buildResponse(response: Response<A>): Response<B> {
        return response.fold(
            { Response.Error(it.errorBody, it.code, it.headers, it.message) },
            { f(it.body).execute() })
    }

    override val cancelled: Boolean
        get() = originalKall.cancelled

    override val executed: Boolean
        get() = originalKall.executed

    override val request: Request
        get() = originalKall.request
}