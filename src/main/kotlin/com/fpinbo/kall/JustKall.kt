package com.fpinbo.kall

import com.fpinbo.kall.response.Response

class JustKall<A>(private val value: Response<A>) : Kall<A> {

    private var mutableCancelled = false
    private var mutableExecuted = false

    override fun cancel() {
        mutableCancelled = true
    }

    override fun clone(): Kall<A> = this

    override fun execute(): Response<A> {
        mutableExecuted = true
        return value
    }

    override fun executeAsync(onResponse: (Kall<A>, Response<A>) -> Unit, onFailure: (Kall<A>, Throwable) -> Unit) {
        mutableExecuted = true
        onResponse(this, value)
    }

    override val cancelled: Boolean
        get() = mutableCancelled

    override val executed: Boolean
        get() = mutableExecuted
}