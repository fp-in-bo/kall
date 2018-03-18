package com.fpinbo.kall

interface Kall<A> {

    fun cancel()

    fun clone(): Kall<A>

    fun execute(): Response<A>

    fun executeAsync(onResponse: (Kall<A>, Response<A>) -> Unit, onFailure: (Kall<A>, Throwable) -> Unit)

    val cancelled: Boolean

    val executed: Boolean

    val request: okhttp3.Request
}