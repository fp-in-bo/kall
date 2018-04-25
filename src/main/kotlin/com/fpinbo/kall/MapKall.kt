package com.fpinbo.kall

import com.fpinbo.kall.response.Response
import com.fpinbo.kall.response.map
import okhttp3.Request

class MapKall<A, B>(
    private val originalKall: Kall<A>,
    private val f: (A) -> B) : Kall<B> {

    override fun cancel() = originalKall.cancel()

    override fun clone() = this

    override fun execute(): Response<B> {
        val response = originalKall.execute()
        return response.map(f)
    }

    override fun executeAsync(onResponse: (Kall<B>, Response<B>) -> Unit, onFailure: (Kall<B>, Throwable) -> Unit) {
        originalKall.executeAsync({ _, response ->
            onResponse(this, response.map(f))
        }, { _, throwable ->
            onFailure(this, throwable)
        })
    }

    override val cancelled: Boolean
        get() = originalKall.cancelled

    override val executed: Boolean
        get() = originalKall.executed

    override val request: Request
        get() = originalKall.request
}